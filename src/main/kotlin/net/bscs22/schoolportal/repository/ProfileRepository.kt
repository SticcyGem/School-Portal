package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.SchoolProfile

/**
 * Generic repository interface for retrieving profile information.
 *
 * This interface allows different profile types (e.g., students, professors)
 * to share a common contract for loading profile data using an account ID.
 *
 * @param T The specific profile type that implements [SchoolProfile].
 */
interface ProfileRepository<T : SchoolProfile> {
    /**
     * Finds a profile using a given account ID.
     *
     * @param accountId The unique account identifier.
     * @return A profile of type [T], or `null` if no matching record exists.
     */
    fun findByAccountId(accountId: String): T?
}