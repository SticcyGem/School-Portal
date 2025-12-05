package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, UUID>