package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Enrollment
import net.bscs22.schoolportal.models.enums.EnrollmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    fun findByStudentAccount_AccountIdAndTerm_AcademicTermNo(
        accountId: UUID,
        termNo: Long
    ): Enrollment?

    fun countByEnrollmentStatus(status: EnrollmentStatus): Long

    // --- REQUIRED FOR ADMIN PENDING LIST ---
    fun findByEnrollmentStatus(status: EnrollmentStatus): List<Enrollment>

    // Required for GradeService optimization (assuming it was needed elsewhere)
    fun findByEnrollmentStatusIn(statuses: List<EnrollmentStatus>): List<Enrollment>

    // Existing: Find by section (for grading) - Added this back for completeness
    @Query("""
        SELECT e FROM Enrollment e 
        JOIN e.sections es 
        WHERE es.section.sectionNo = :sectionId 
        AND e.enrollmentStatus = 'ENROLLED'
    """)
    fun findApprovedEnrollmentsBySection(sectionId: Long): List<Enrollment>
}