package net.bscs22.schoolportal.dtos.subject

data class UpdateSubjectRequest(
    val subjectName: String,
    val lecUnits: Int,
    val labUnits: Int
)