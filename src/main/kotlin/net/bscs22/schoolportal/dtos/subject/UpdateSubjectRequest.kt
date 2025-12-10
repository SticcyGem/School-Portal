package net.bscs22.schoolportal.dtos.subject
import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateSubjectRequest(
    @field:JsonProperty("subject_name") val subjectName: String,
    @field:JsonProperty("lec_units") val lecUnits: Int,
    @field:JsonProperty("lab_units") val labUnits: Int,
    @field:JsonProperty("prerequisite_code") val prerequisiteCode: String?,
    @field:JsonProperty("course_code") val courseCode: String?
)