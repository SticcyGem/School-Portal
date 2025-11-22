package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.ProfessorProfile
import net.bscs22.schoolportal.repository.ProfessorRepository
import org.springframework.stereotype.Service

@Service
class ProfessorProfileService(
    private val repository: ProfessorRepository
) : ProfileService<ProfessorProfile> {

    override val roleName: String = "professor"

    override fun load(accountId: String): ProfessorProfile? {
        return repository.findByAccountId(accountId)
    }
}