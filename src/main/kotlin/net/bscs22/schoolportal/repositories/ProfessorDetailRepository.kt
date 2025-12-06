package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.views.ProfessorDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProfessorDetailRepository : JpaRepository<ProfessorDetail, UUID>