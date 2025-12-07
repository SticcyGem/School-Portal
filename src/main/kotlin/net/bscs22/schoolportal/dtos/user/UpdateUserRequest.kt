package net.bscs22.schoolportal.dtos.user

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.models.enums.AccountStatus

data class UpdateUserRequest(
    val email: String? = null,

    @field:JsonProperty("first_name")
    val firstName: String? = null,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty("last_name")
    val lastName: String? = null,

    val status: AccountStatus? = null
)