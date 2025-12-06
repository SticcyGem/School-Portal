package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.controllers.EnrollmentController.EnrollmentOfferingResponse
import net.bscs22.schoolportal.controllers.EnrollmentController.SectionDTO
import net.bscs22.schoolportal.models.*
import net.bscs22.schoolportal.models.enums.EnrollmentStatus
import net.bscs22.schoolportal.models.enums.StudentStatus
import net.bscs22.schoolportal.models.enums.StudentType
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
    private val subjectRepository: SubjectRepository,
    // Ensure this repository name matches the interface you created (PendingEnrollmentDetailRepository)
    private val pendingEnrollmentRepository: PendingEnrollmentDetailRepository
) {

    // --- DTOs for Admin Approval Screen ---
    data class AdminEnrollmentDetailDTO(
        val enrollmentNo: Long,
        val studentName: String,
        val studentId: String,
        val courseCode: String,
        val termName: String,
        val totalUnits: Long,
        val sections: List<SectionApprovalDTO>
    )

    data class SectionApprovalDTO(
        val sectionNo: Long,
        val subjectCode: String,
        val subjectTitle: String,
        val schedule: String
    )

    // 1. GET OFFERINGS
    fun getEnrollmentOptions(accountId: UUID): EnrollmentOfferingResponse {
        val student = studentRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Student record not found") }

        val currentTerm = termRepository.findActiveEnrollmentTerm()
            ?: throw IllegalStateException("No active enrollment period found.")

        // FIX: Fetch sections properly using the academic term
        val availableSections = sectionRepository.findByAcademicTerm_AcademicTermNo(
            currentTerm.academicTermNo!!
        )

        val existingEnrollment = enrollmentRepository.findByStudentAccount_AccountIdAndTerm_AcademicTermNo(
            accountId, currentTerm.academicTermNo!!
        )

        return EnrollmentOfferingResponse(
            termName = currentTerm.termName,
            studentType = student.studentType.name,
            isBlockBased = (student.studentType == StudentType.REGULAR),
            enrollmentStatus = existingEnrollment?.enrollmentStatus?.name ?: "NONE",
            remarks = existingEnrollment?.remarks,
            sections = availableSections.map { toSectionDTO(it) }
        )
    }

    // 2. SUBMIT ENROLLMENT (DRAFT MODE)
    @Transactional
    fun submitEnrollment(accountId: UUID, sectionIds: List<Long>): String {
        val account = accountRepository.findById(accountId).get()
        val currentTerm = termRepository.findActiveEnrollmentTerm()
            ?: throw IllegalStateException("Enrollment is closed.")

        // 1. Fetch Sections
        val sectionsToEnroll = sectionRepository.findAllById(sectionIds)
        if (sectionsToEnroll.isEmpty()) throw IllegalArgumentException("No valid sections selected.")

        // 2. VALIDATION: Check Prerequisites
        val passedSubjects = creditedSubjectRepository.findByStudentAccountId(accountId).map { it.subjectCode }
        sectionsToEnroll.forEach { section ->
            val prereqs = section.subject.prerequisites
            val missingPrereqs = prereqs.filter { it.subjectCode !in passedSubjects }

            if (missingPrereqs.isNotEmpty()) {
                val names = missingPrereqs.joinToString { it.subjectName }
                throw IllegalArgumentException("Cannot enroll in ${section.subject.subjectName}. Missing prerequisites: $names")
            }
        }

        // 3. VALIDATION: Check Schedule Conflicts
        checkScheduleConflicts(sectionsToEnroll)

        // 4. Create or Update Draft
        var enrollment = enrollmentRepository.findByStudentAccount_AccountIdAndTerm_AcademicTermNo(
            accountId, currentTerm.academicTermNo!!
        )

        if (enrollment != null) {
            if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
                throw IllegalArgumentException("You are already officially enrolled. Changes must be made by an admin.")
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
                    section = section
                )
            )
        }

        enrollmentRepository.save(enrollment)
        return "Draft enrollment saved. (${sectionsToEnroll.size} subjects selected). Waiting for Admin approval."
    }

    // 3. APPROVE ENROLLMENT
    @Transactional
    fun approveEnrollment(enrollmentId: Long): String {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment record not found") }

        if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
            return "This enrollment is already approved."
        }

        enrollment.sections.forEach { enrollmentSection ->
            val section = enrollmentSection.section
                ?: throw IllegalStateException("Data Error: Enrollment has a missing section reference.")
            if (section.availableSlots <= 0) {
                throw IllegalStateException("Cannot approve: Section ${section.sectionNo} (${section.subject.subjectCode}) is FULL.")
            }
            section.availableSlots -= 1
            sectionRepository.save(section)
        }

        enrollment.enrollmentStatus = EnrollmentStatus.ENROLLED
        enrollment.remarks = "Approved by Admin"
        enrollmentRepository.save(enrollment)

        val student = studentRepository.findById(enrollment.studentAccount.accountId).get()
        if (student.studentStatus == StudentStatus.ADMITTED) {
            student.studentStatus = StudentStatus.ENROLLED
            studentRepository.save(student)
        }

        return "Student successfully enrolled."
    }

    // 4. REJECT ENROLLMENT
    @Transactional
    fun rejectEnrollment(enrollmentId: Long, reason: String): String {
        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { IllegalArgumentException("Enrollment record not found") }

        if (enrollment.enrollmentStatus == EnrollmentStatus.ENROLLED) {
            throw IllegalArgumentException("Cannot reject an already active enrollment. You must Drop it instead.")
        }

        enrollment.enrollmentStatus = EnrollmentStatus.REJECTED
        enrollment.remarks = reason
        enrollmentRepository.save(enrollment)

        return "Enrollment rejected. Reason logged."
    }

    // =========================================================================
    // FEATURE: ADMIN - LIST PENDING ENROLLMENTS
    // =========================================================================

    @Transactional(readOnly = true)
    fun getPendingEnrollments(): List<AdminEnrollmentDetailDTO> {
        val rawData = pendingEnrollmentRepository.findAll()

        return rawData.groupBy { it.enrollmentNo }.map { (enrollmentNo, records) ->
            val firstRecord = records.first()
            val totalUnits = records.sumOf { it.units }

            AdminEnrollmentDetailDTO(
                enrollmentNo = enrollmentNo,
                studentName = firstRecord.studentName,
                studentId = firstRecord.studentNo.toString(),
                courseCode = firstRecord.courseCode,
                termName = firstRecord.termName,
                totalUnits = totalUnits,

                sections = records.map { record ->
                    SectionApprovalDTO(
                        sectionNo = record.sectionNo,
                        subjectCode = record.subjectCode,

                        // FIX #1: Use correct property names from PendingEnrollmentDetail entity
                        subjectTitle = record.subjectName,
                        schedule = record.fullSchedule ?: "TBA"
                    )
                }
            )
        }
    }

    // --- HELPERS ---

    private fun checkScheduleConflicts(sections: List<Section>) {
        val allSchedules = sections.flatMap { sec ->
            sec.schedules.map { sched -> sec to sched }
        }

        for (i in allSchedules.indices) {
            for (j in i + 1 until allSchedules.size) {
                val (secA, schedA) = allSchedules[i]
                val (secB, schedB) = allSchedules[j]

                if (schedA.dayName == schedB.dayName) {
                    if (schedA.startTime.isBefore(schedB.endTime) && schedA.endTime.isAfter(schedB.startTime)) {
                        throw IllegalArgumentException(
                            "Schedule Conflict: ${secA.subject.subjectCode} overlaps with ${secB.subject.subjectCode} on ${schedA.dayName}"
                        )
                    }
                }
            }
        }
    }

    private fun toSectionDTO(section: Section): SectionDTO {
        val schedString = section.schedules.joinToString(", ") {
            "${it.dayName} ${it.startTime}-${it.endTime} (${it.room.roomName})"
        }

        // FIX #2: Correctly traverse Section -> SectionBlock -> Block -> Course
        val sectionName = section.blocks.firstOrNull()?.let { sectionBlock ->
            val block = sectionBlock.block
            if (block != null && block.course != null) {
                "${block.course!!.courseCode} ${block.yearLevel}-${block.blockNumber}"
            } else {
                "Unknown Block"
            }
        } ?: "Open Section"

        return SectionDTO(
            sectionNo = section.sectionNo!!,
            sectionName = sectionName,
            subjectCode = section.subject.subjectCode,
            subjectTitle = section.subject.subjectName,
            units = section.subject.lecUnits + section.subject.labUnits,
            schedule = schedString,
            status = if(section.availableSlots > 0) "OPEN" else "FULL"
        )
    }
}