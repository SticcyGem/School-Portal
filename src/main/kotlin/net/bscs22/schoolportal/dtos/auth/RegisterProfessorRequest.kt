package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import net.bscs22.schoolportal.entities.commons.enums.EmployeeType

data class RegisterProfessorRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,

    @field:JsonProperty(value = "first_name")
    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty(value = "last_name")
    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:JsonProperty(value = "professor_id")
    val professorId: String? = null,

    @field:JsonProperty(value = "employee_type")
    @field:NotNull(message = "Employee type is required")
    var employeeType: EmployeeType
)