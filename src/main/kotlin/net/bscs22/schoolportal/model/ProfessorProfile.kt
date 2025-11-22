package net.bscs22.schoolportal.model

data class ProfessorProfile(
    override val accountId: String,
    override val email: String,
    val professorNo: String,
    val professorName: String,
    val employeeType: String
) : SchoolProfile