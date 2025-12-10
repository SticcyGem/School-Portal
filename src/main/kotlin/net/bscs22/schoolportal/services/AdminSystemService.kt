package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.enrollment.SystemStatusResponse
import net.bscs22.schoolportal.dtos.enrollment.UpdateTermConfigRequest
import net.bscs22.schoolportal.dtos.subject.SubjectRequest
import net.bscs22.schoolportal.entities.academics.Subject
import net.bscs22.schoolportal.entities.commons.enums.SystemStatus
import net.bscs22.schoolportal.repositories.AcademicTermRepository
import net.bscs22.schoolportal.repositories.CourseRepository
import net.bscs22.schoolportal.repositories.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AdminSystemService(
    private val termRepository: AcademicTermRepository,
    private val subjectRepository: SubjectRepository,
    private val courseRepository: CourseRepository
) {
    @Transactional(readOnly = true)
    fun getCurrentSystemStatus(): SystemStatusResponse {
        val today = LocalDate.now()
        val term = termRepository.findActiveEnrollmentTerm(today)
            ?: termRepository.findAll().lastOrNull()
            ?: throw IllegalStateException("No academic terms defined.")

        val isOpen = !today.isBefore(term.enrollmentStartDate) && !today.isAfter(term.enrollmentEndDate)

        return SystemStatusResponse(
            schoolYear = term.schoolYear.syName,
            termName = term.termName,
            status = if (isOpen) SystemStatus.OPEN else SystemStatus.CLOSED,
            enrollmentStart = term.enrollmentStartDate,
            enrollmentEnd = term.enrollmentEndDate
        )
    }

    @Transactional
    fun updateEnrollmentConfig(req: UpdateTermConfigRequest) {
        val today = LocalDate.now()
        val term = termRepository.findActiveEnrollmentTerm(today)
            ?: termRepository.findAll().lastOrNull()
            ?: throw IllegalStateException("No term to configure.")

        if (req.forceClose) {
            term.enrollmentEndDate = LocalDate.now().minusDays(1)
        } else {
            term.enrollmentStartDate = req.enrollmentStart
            term.enrollmentEndDate = req.enrollmentEnd
        }

        termRepository.save(term)
    }

    @Transactional
    fun createFullSubject(req: SubjectRequest): String {
        if (subjectRepository.existsById(req.subjectCode)) {
            throw IllegalArgumentException("Subject ${req.subjectCode} already exists")
        }

        val newSubject = Subject(
            subjectCode = req.subjectCode,
            subjectName = req.subjectName,
            lecUnits = req.lecUnits.toLong(),
            labUnits = req.labUnits.toLong()
        )

        if (!req.prerequisiteCode.isNullOrBlank() && req.prerequisiteCode != "None") {
            val prereq = subjectRepository.findById(req.prerequisiteCode)
                .orElseThrow { IllegalArgumentException("Prerequisite ${req.prerequisiteCode} not found") }
            newSubject.prerequisites.add(prereq)
        }

        val savedSubject = subjectRepository.save(newSubject)

        if (!req.courseCode.isNullOrBlank()) {
            val course = courseRepository.findById(req.courseCode)
                .orElseThrow { IllegalArgumentException("Course ${req.courseCode} not found") }

            // Add subject to course
            course.subjects.add(savedSubject)
            courseRepository.save(course)
        }
        return "Subject created successfully."
    }
}