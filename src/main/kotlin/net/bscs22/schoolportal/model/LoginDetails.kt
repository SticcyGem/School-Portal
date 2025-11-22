package net.bscs22.schoolportal.model

data class LoginDetails (
    val accountId: String,
    val email: String,
    val passwordHash: String,
    val roleName: String
)