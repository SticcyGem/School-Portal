package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface AccountRepository : JpaRepository<Account, UUID> {
    fun findByEmail(email: String): Account?
    fun existsByEmail(email: String): Boolean

    @Query("""
        SELECT DISTINCT a FROM Account a 
        LEFT JOIN FETCH a.profile p 
        LEFT JOIN FETCH a.student s 
        LEFT JOIN FETCH a.professor prof 
        WHERE
           LOWER(a.email) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR CAST(a.status AS string) LIKE UPPER(CONCAT('%', :query, '%'))
           OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
           OR CAST(s.studentNo AS string) LIKE :query
           OR CAST(s.yearLevel AS string) LIKE :query
           OR CAST(s.studentStatus AS string) LIKE UPPER(CONCAT('%', :query, '%'))
           OR CAST(s.studentType AS string) LIKE UPPER(CONCAT('%', :query, '%'))
           OR CAST(s.educationLevel AS string) LIKE UPPER(CONCAT('%', :query, '%'))
           OR LOWER(prof.professorId) LIKE LOWER(CONCAT('%', :query, '%'))
           OR CAST(prof.professorStatus AS string) LIKE UPPER(CONCAT('%', :query, '%'))
           OR CAST(prof.employeeType AS string) LIKE UPPER(CONCAT('%', :query, '%'))
    """)
    fun searchAccounts(query: String, pageable: Pageable): Page<Account>

    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO school.account_roles (account_id, role_no) VALUES (:accountId, :roleNo)",
        nativeQuery = true
    )
    fun addRole(accountId: UUID, roleNo: Long)
}