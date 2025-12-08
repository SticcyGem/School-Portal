package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AuthResponse(
    @field:JsonProperty("token")
    val token: String,

    @field:JsonProperty("roles")
    val roles: List<String>,

    @field:JsonProperty("account_id")
    val accountId: UUID,

    @field:JsonProperty("profile")
    val profile: Any?
)