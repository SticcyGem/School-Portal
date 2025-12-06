package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.views.SectionGradeRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectionGradeRowRepository : JpaRepository<SectionGradeRow, Long> {
    fun findBySectionNo(sectionNo: Long): List<SectionGradeRow>
}