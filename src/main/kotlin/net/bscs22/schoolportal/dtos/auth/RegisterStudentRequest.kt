package net.bscs22.schoolportal.dtos.auth

import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.StudentType
import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterStudentRequest(
    val email: String,
    val password: String,

    @field:JsonProperty("first_name")
    val firstName: String,

    @field:JsonProperty("middle_name")
    val middleName: String? = null,

    @field:JsonProperty("last_name")
    val lastName: String,

    @field:JsonProperty("student_no")
    val studentNo: Long? = null,

    @field:JsonProperty("education_level")
    val educationLevel: EducationLevel,

    @field:JsonProperty("student_type")
    val studentType: StudentType,

    @field:JsonProperty("course_code")
    val courseCode: String,

    @field:JsonProperty("block_no")
    val blockNo: Long? = null
)
