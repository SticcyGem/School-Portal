package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.StudentProfile
import net.bscs22.schoolportal.repository.StudentRepository
import org.springframework.stereotype.Service

/**
 * Service class responsible for handling StudentProfile operations.
 * Provides a clean interface for retrieving student data without exposing repository details.
 *
 * @property repository The repository used to fetch student data from the database.
 */
@Service
class StudentProfileService(
    private val repository: StudentRepository
) : ProfileService<StudentProfile> {

    /** The role associated with this service. Used for identification and metadata. */
    override val roleName: String = "student"

    /**
     * Loads the student profile for a given account ID.
     *
     * @param accountId The unique identifier of the account.
     * @return A [StudentProfile] if found, otherwise null.
     */
    override fun load(accountId: String): StudentProfile? {
        return repository.findByAccountId(accountId)
    }
}