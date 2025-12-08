package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.entities.grades.GradeComponent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GradeComponentRepository : JpaRepository<GradeComponent, Long> {
    fun findBySection_SectionNoOrderByGcNoAsc(sectionNo: Long): List<GradeComponent>
}