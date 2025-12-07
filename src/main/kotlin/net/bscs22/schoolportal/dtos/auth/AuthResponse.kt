package net.bscs22.schoolportal.dtos.auth

import java.util.UUID

data class AuthResponse(
    val token: String,
    val roles: List<String>,
    val accountId: UUID,
    val profile: Any?
)