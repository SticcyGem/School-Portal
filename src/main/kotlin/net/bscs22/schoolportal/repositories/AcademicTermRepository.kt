package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.AcademicTerm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AcademicTermRepository : JpaRepository<AcademicTerm, Long> {
    @Query("SELECT t FROM AcademicTerm t WHERE :date BETWEEN t.enrollmentStartDate AND t.enrollmentEndDate")
    fun findActiveEnrollmentTerm(date: LocalDate = LocalDate.now()): AcademicTerm?
}