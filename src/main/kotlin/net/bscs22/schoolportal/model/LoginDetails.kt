package net.bscs22.schoolportal.model

/**
 * Represents the login information for an account.
 *
 * @property accountId     Unique identifier of the account.
 * @property email         Email associated with the account.
 * @property passwordHash  Hashed password of the user.
 * @property roleName      Role assigned to the user (e.g., admin, user).
 */
data class LoginDetails (
    val accountId: String,
    val email: String,
    val passwordHash: String,
    val roleName: String
)