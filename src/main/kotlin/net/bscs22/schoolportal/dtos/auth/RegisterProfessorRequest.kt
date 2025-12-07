package net.bscs22.schoolportal.dtos.auth

import net.bscs22.schoolportal.models.enums.EmployeeType
import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterProfessorRequest(
    val email: String,
    val password: String,

    @field:JsonProperty(value = "first_name")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty(value = "last_name")
    val lastName: String,

    @field:JsonProperty(value = "professor_id")
    val professorId: String? = null,

    @field:JsonProperty(value = "employee_type")
    val employeeType: EmployeeType
)
