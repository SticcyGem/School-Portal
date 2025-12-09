package net.bscs22.schoolportal.dtos.user

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.entities.commons.enums.AccountStatus
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

    val roles: List<String>,

    @field:JsonProperty("student_no")
    val studentNo: Long? = null,

    @field:JsonProperty("course_code")
    val courseCode: String? = null,

    @field:JsonProperty("student_type")
    val studentType: String? = null,

    @field:JsonProperty("education_level")
    val educationLevel: String? = null,

    @field:JsonProperty("professor_id")
    val professorId: String? = null,

    @field:JsonProperty("employee_type")
    val employeeType: String? = null
)