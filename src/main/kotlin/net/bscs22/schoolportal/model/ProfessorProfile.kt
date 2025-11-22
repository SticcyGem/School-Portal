package net.bscs22.schoolportal.model

/**
 * Represents the detailed information of a professor.
 *
 * @property accountId     The account identifier associated with the professor.
 * @property email         The official email address of the professor.
 * @property professorNo   The unique identifier assigned to the professor.
 * @property professorName The full name of the professor.
 * @property employeeType  The type of employment (e.g., full-time, part-time, contractual).
 */
data class ProfessorProfile(
    override val accountId: String,
    override val email: String,
    val professorNo: String,
    val professorName: String,
    val employeeType: String
) : SchoolProfile