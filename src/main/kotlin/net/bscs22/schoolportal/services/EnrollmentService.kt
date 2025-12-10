package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.enrollment.AdminEnrollmentDetailResponse
import net.bscs22.schoolportal.dtos.enrollment.EnrollmentOfferingResponse
import net.bscs22.schoolportal.mappers.EnrollmentMapper
import net.bscs22.schoolportal.entities.enrollments.Enrollment
import net.bscs22.schoolportal.entities.enrollments.EnrollmentSection
import net.bscs22.schoolportal.entities.commons.enums.EnrollmentStatus
import net.bscs22.schoolportal.entities.commons.enums.StudentStatus
import net.bscs22.schoolportal.entities.commons.enums.StudentType
import net.bscs22.schoolportal.entities.commons.enums.SubjectStatus
import net.bscs22.schoolportal.repositories.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EnrollmentService(
    private val studentRepository: StudentRepository,
    private val accountRepository: AccountRepository,
    private val termRepository: AcademicTermRepository,
    private val sectionRepository: SectionRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val creditedSubjectRepository: CreditedSubjectRepository,
    private val pendingEnrollmentRepository: PendingEnrollmentDetailRepository,
    private val enrollmentMapper: EnrollmentMapper
) {

    // --- GET OFFERINGS ---
    @Transactional(readOnly = true)
    fun getEnrollmentOptions(accountId: UUID): EnrollmentOfferingResponse {
        val student = studentRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Student record not found") }

        val currentTerm = termRepository.findActiveEnrollmentTerm()
            ?: throw IllegalStateException("No active enrollment period found.")

        val availableSections = sectionRepository.findByAcademicTerm_AcademicTermNo(
            currentTerm.academicTermNo!!
        )

        val existingEnrollment = enrollmentRepository.findByStudentAccount_AccountIdAndTerm_AcademicTermNo(
            accountId, currentTerm.academicTermNo!!
        )

        val sectionDTOs = availableSections.map { enrollmentMapper.toSectionOfferingResponse(it) }

        return EnrollmentOfferingResponse(
            termName = currentTerm.termName,
            studentType = student.studentType.name,
            isBlockBased = (student.studentType == StudentType.REGULAR),
            enrollmentStatus = existingEnrollment?.enrollmentStatus?.name ?: "NONE",
            remarks = existingEnrollment?.remarks,
            sections = sectionDTOs
        )
    }

    // --- SUBMIT ENROLLMENT ---
    @Transactional
    fun submitEnrollment(accountId: UUID, sectionIds: List<Long>): String {
        val account = accountRepository.getReferenceById(accountId)

        val currentTerm = termRepository.findActiveEnrollmentTerm()
            ?: throw IllegalStateException("Enrollment is closed.")

        if (sectionIds.isEmpty()) throw IllegalArgumentException("No sections selected.")

        val sectionsToEnroll = sectionRepository.findAllById(sectionIds)
        if (sectionsToEnroll.size != sectionIds.size) throw IllegalArgumentException("Some selected sections are invalid.")

        val passedSubjects = creditedSubjectRepository.findByStudentAccountId(accountId).map { it.subjectCode }.toSet()

        sectionsToEnroll.forEach { section ->
            val prereqs = section.subject.prerequisites
            val missing = prereqs.filter { it.subjectCode !in passedSubjects }
            if (missing.isNotEmpty()) {
                val names = missing.joinToString { it.subjectName }
                throw IllegalArgumentException("Cannot enroll in ${section.subject.subjectName}. Missing prerequisites: $names")
            }
        }

        var enrollment = enrollmentRepository.findByStudentAccount_AccountIdAndTerm_AcademicTermNo(
            accountId, currentTerm.academicTermNo!!
        )

        if (enrollment != null) {
            if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
                throw IllegalArgumentException("You are already enrolled. Please contact registrar for changes.")
            }
            enrollment.sections.clear()
            enrollment.remarks = null
            enrollment.enrollmentStatus = EnrollmentStatus.DRAFT
        } else {
            enrollment = Enrollment(
                studentAccount = account,
                term = currentTerm,
                enrollmentStatus = EnrollmentStatus.DRAFT
            )
        }

        sectionsToEnroll.forEach { section ->
            enrollment.sections.add(
                EnrollmentSection(
                    enrollment = enrollment,
                    section = section,
                    subjectStatus = SubjectStatus.ENROLLED
                )
            )
        }

        enrollmentRepository.save(enrollment)
        return "Draft enrollment saved. Waiting for Admin approval."
    }

    // --- APPROVE ENROLLMENT ---

    @Transactional
    fun approveEnrollment(enrollmentId: Long): String {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment record not found") }

        if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
            return "Already approved."
        }

        enrollment.sections.forEach { enrollmentSection ->
            val section = enrollmentSection.section!!

            if (section.availableSlots <= 0) {
                throw IllegalStateException("Cannot approve. Section ${section.subject.subjectCode} is FULL.")
            }

            section.availableSlots -= 1
            sectionRepository.save(section)
        }

        enrollment.enrollmentStatus = EnrollmentStatus.ENROLLED
        enrollment.remarks = "Approved by Admin" // Update remarks
        enrollmentRepository.save(enrollment)

        val student = studentRepository.findById(enrollment.studentAccount.accountId).orElse(null)
        if (student != null && student.studentStatus == StudentStatus.ADMITTED) {
            student.studentStatus = StudentStatus.ENROLLED
            studentRepository.save(student)
        }

        return "Student successfully enrolled."
    }

    // --- REJECT ENROLLMENT ---

    @Transactional
    fun rejectEnrollment(enrollmentId: Long, reason: String): String {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment record not found") }

        if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
            throw IllegalArgumentException("Cannot reject active enrollment. Use Drop instead.")
        }

        enrollment.enrollmentStatus = EnrollmentStatus.REJECTED
        enrollment.remarks = reason
        enrollmentRepository.save(enrollment)

        return "Enrollment rejected."
    }

    // --- LIST PENDING ENROLLMENT ---

    @Transactional(readOnly = true)
    fun getPendingEnrollments(): List<AdminEnrollmentDetailResponse> {
        val rawData = pendingEnrollmentRepository.findAll()

        return rawData.groupBy { it.enrollmentNo }.map { (enrollmentNo, records) ->
            val firstRecord = records.first()
            val totalUnits = records.sumOf { it.units }

            val sectionResponses = records.map { enrollmentMapper.toSectionApprovalResponse(it) }

            AdminEnrollmentDetailResponse(
                enrollmentNo = enrollmentNo,
                studentName = firstRecord.studentName,
                studentId = firstRecord.studentNo.toString(),
                courseCode = firstRecord.courseCode,
                termName = firstRecord.termName,
                totalUnits = totalUnits,
                sections = sectionResponses
            )
        }
    }
}