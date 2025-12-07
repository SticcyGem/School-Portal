package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class ChangePasswordRequest(
    @field:JsonProperty("old_pass")
    val oldPass: String,

    @field:JsonProperty("new_pass")
    val newPass: String
)
