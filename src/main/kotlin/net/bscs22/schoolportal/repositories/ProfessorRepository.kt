package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Professors
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProfessorRepository : JpaRepository<Professors, UUID> {
    fun existsByProfessorId(professorId: String): Boolean
}