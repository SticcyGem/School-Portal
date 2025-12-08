package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.entities.grades.Grade
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GradeRepository : JpaRepository<Grade, Long> {
    fun findByEnrollment_EnrollmentNoIn(enrollmentNos: List<Long>): List<Grade>
}