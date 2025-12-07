package net.bscs22.schoolportal.dtos.user

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.models.enums.AccountStatus
import java.util.UUID

data class UserResponse(
    @field:JsonProperty("account_id")
    val accountId: UUID,

    val email: String,

    @field:JsonProperty("first_name")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty("last_name")
    val lastName: String,

    val status: AccountStatus,

    val roles: List<String>
)