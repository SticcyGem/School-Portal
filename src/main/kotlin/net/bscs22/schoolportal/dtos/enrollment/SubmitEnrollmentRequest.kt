package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty

data class SubmitEnrollmentRequest(
    @field:JsonProperty("section_id")
    val sectionId: List<Long>
)