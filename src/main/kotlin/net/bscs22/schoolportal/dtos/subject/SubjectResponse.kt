package net.bscs22.schoolportal.dtos.subject
import com.fasterxml.jackson.annotation.JsonProperty

data class SubjectResponse(
    @field:JsonProperty("subject_code") val subjectCode: String,
    @field:JsonProperty("subject_name") val subjectName: String,
    @field:JsonProperty("lec_units") val lecUnits: Long,
    @field:JsonProperty("lab_units") val labUnits: Long,
    @field:JsonProperty("prerequisite_code") val prerequisiteCode: String?,
    @field:JsonProperty("course_code") val courseCode: String?
)