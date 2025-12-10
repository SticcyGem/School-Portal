package net.bscs22.schoolportal.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import net.bscs22.schoolportal.entities.commons.enums.EducationLevel
import net.bscs22.schoolportal.entities.commons.enums.StudentType

data class RegisterStudentRequest(
    @field:JsonProperty("email")
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:JsonProperty("password")
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,

    @field:JsonProperty("first_name")
    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty("last_name")
    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:JsonProperty("student_no")
    val studentNo: Long? = null,

    @field:JsonProperty("education_level")
    @field:NotNull(message = "Education level is required")
    var educationLevel: EducationLevel,

    @field:JsonProperty("student_type")
    @field:NotNull(message = "Student type is required")
    var studentType: StudentType,

    @field:JsonProperty("course_code")
    @field:NotBlank(message = "Course code is required")
    val courseCode: String,

    @field:JsonProperty("block_no")
    val blockNo: Long? = null
)