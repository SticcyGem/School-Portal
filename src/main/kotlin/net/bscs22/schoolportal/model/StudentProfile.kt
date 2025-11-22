package net.bscs22.schoolportal.model

data class StudentProfile(
    override val accountId: String,
    override val email: String,
    val studentNo: Int,
    val studentName: String,
    val studentType: String,
    val yearLevel: Int,
    val blockNumber: Int,
    val blockName: String,
    val courseCode: String
) : SchoolProfile