package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.LoginDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LoginRepository : JpaRepository<LoginDetails, UUID> {
    fun findByEmail(email: String): LoginDetails?
}