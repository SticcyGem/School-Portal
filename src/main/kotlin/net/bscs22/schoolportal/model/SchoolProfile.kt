package net.bscs22.schoolportal.model

/**
 * Represents a generic profile within the school portal system.
 *
 * This interface is implemented by all specific profile types (e.g., [StudentProfile], [ProfessorProfile]).
 * It defines the minimal shared properties that every profile must have.
 */
interface SchoolProfile {
    /** The unique identifier of the account associated with this profile. */
    val accountId: String
    /** The email address associated with this profile. */
    val email: String
}