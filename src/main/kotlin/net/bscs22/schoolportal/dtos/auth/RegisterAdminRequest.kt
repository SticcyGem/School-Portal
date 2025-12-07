package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterAdminRequest(
    val email: String,
    val password: String,

    @field:JsonProperty("first_name")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty("last_name")
    val lastName: String
)