package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty

data class SectionApprovalResponse(
    @field:JsonProperty("section_no")
    val sectionNo: Long,

    @field:JsonProperty("subject_code")
    val subjectCode: String,

    @field:JsonProperty("subject_title")
    val subjectTitle: String,

    @field:JsonProperty("schedule")
    val schedule: String
)