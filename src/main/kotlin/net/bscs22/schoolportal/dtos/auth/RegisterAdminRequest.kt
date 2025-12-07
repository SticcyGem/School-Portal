package net.bscs22.schoolportal.dtos.auth

data class RegisterAdminRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)