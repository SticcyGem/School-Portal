package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.LoginDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LoginRepository : JpaRepository<LoginDetails, UUID> {
    fun findByEmail(email: String): LoginDetails?
}