package net.bscs22.schoolportal.dtos.auth

data class LoginRequest(
    val email: String,
    val password: String
)