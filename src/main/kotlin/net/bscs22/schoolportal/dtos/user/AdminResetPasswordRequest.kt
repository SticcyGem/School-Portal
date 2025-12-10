package net.bscs22.schoolportal.dtos.user

import com.fasterxml.jackson.annotation.JsonProperty

data class AdminResetPasswordRequest(
    @field:JsonProperty("new_password")
    val newPass: String
)