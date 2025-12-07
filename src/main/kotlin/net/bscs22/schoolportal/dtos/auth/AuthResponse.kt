package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AuthResponse(
    val token: String,
    val roles: List<String>,

    @field:JsonProperty("account_id")
    val accountId: UUID,

    val profile: Any?
)