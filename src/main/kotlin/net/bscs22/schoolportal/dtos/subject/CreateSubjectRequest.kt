package net.bscs22.schoolportal.dtos.subject

data class CreateSubjectRequest(
    val subjectCode: String,
    val subjectName: String,
    val lecUnits: Int,
    val labUnits: Int
)