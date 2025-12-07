package net.bscs22.schoolportal.dtos.auth

import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.StudentType

data class RegisterStudentRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val studentNo: Long? = null,
    val educationLevel: EducationLevel,
    val studentType: StudentType,
    val courseCode: String,
    val blockNo: Long? = null
)
