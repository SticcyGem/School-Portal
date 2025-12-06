package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Section
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectionRepository : JpaRepository<Section, Long> {
    fun findByBlocks_BlockNo(blockNo: Long): List<Section>
    fun findBySubject_SubjectCodeIn(subjectCodes: List<String>): List<Section>
}