package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.entities.views.CreditedSubject
import net.bscs22.schoolportal.entities.views.CreditedSubjectId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditedSubjectRepository : JpaRepository<CreditedSubject, CreditedSubjectId> {
    fun findByStudentAccountId(studentAccountId: UUID): List<CreditedSubject>
}