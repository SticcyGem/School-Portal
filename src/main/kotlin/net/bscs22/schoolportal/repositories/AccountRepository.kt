package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Accounts
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface AccountRepository : JpaRepository<Accounts, UUID> {
    fun findByEmail(email: String): Accounts?
    fun existsByEmail(email: String): Boolean

    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO school.account_roles (account_id, role_no) VALUES (:accountId, :roleNo)",
        nativeQuery = true
    )
    fun addRole(accountId: UUID, roleNo: Long)
}