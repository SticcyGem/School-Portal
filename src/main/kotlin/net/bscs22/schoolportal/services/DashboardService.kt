package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.enums.EnrollmentStatus
import net.bscs22.schoolportal.repositories.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DashboardService(
    private val studentRepository: StudentRepository,
    private val professorRepository: ProfessorRepository,
    private val subjectRepository: SubjectRepository,
    private val sectionRepository: SectionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {

    data class AdminDashboardStats(
        val totalStudents: Long,
        val totalProfessors: Long,
        val totalSubjects: Long,
        val activeSections: Long,
        val pendingEnrollments: Long,
        val acceptedEnrollments: Long
    )

    @Transactional(readOnly = true)
    fun getAdminStats(): AdminDashboardStats {
        return AdminDashboardStats(
            totalStudents = studentRepository.count(),
            totalProfessors = professorRepository.count(),
            totalSubjects = subjectRepository.count(),
            activeSections = sectionRepository.count(),

            // Critical Metric: How many students are waiting for approval?
            pendingEnrollments = enrollmentRepository.countByEnrollmentStatus(EnrollmentStatus.DRAFT),
            acceptedEnrollments = enrollmentRepository.countByEnrollmentStatus(EnrollmentStatus.ENROLLED)
        )
    }
}