package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.UserProfiles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserProfileRepository : JpaRepository<UserProfiles, UUID>