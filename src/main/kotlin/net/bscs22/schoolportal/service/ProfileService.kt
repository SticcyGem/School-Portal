package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.SchoolProfile

/**
 * Generic service interface for handling profile-related operations.
 * Supports polymorphic handling of different profile types (Student, Professor, etc.).
 *
 * @param T The type of profile that this service manages.
 */
interface ProfileService<T : SchoolProfile> {
    /** The role name associated with the service (e.g., "student", "professor"). */
    val roleName: String

    /**
     * Loads the profile corresponding to the given account ID.
     *
     * @param accountId The unique identifier of the account.
     * @return The profile of type [T] if found, otherwise null.
     */
    fun load(accountId: String): T?
}