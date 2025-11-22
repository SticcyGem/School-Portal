package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.ProfessorProfile
import net.bscs22.schoolportal.repository.ProfessorRepository
import org.springframework.stereotype.Service

/**
 * Service class responsible for handling ProfessorProfile operations.
 * Acts as a bridge between the repository and application logic.
 *
 * @property repository The repository used to fetch professor data from the database.
 */
@Service
class ProfessorProfileService(
    private val repository: ProfessorRepository
) : ProfileService<ProfessorProfile> {

    /** The role associated with this service. Used for identification and metadata. */
    override val roleName: String = "professor"

    /**
     * Loads the professor profile for a given account ID.
     *
     * @param accountId The unique identifier of the account.
     * @return A [ProfessorProfile] if found, otherwise null.
     */
    override fun load(accountId: String): ProfessorProfile? {
        return repository.findByAccountId(accountId)
    }
}