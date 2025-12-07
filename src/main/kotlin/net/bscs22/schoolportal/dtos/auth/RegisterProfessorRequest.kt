package net.bscs22.schoolportal.dtos.auth

import net.bscs22.schoolportal.models.enums.EmployeeType

data class RegisterProfessorRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val professorId: String,
    val employeeType: EmployeeType
)
