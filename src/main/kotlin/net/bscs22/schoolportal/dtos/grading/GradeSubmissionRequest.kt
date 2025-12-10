package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class GradeSubmissionRequest(
    @field:JsonProperty("enrollment_id")
    val enrollmentId: Long,

    @field:JsonProperty("component_id")
    val componentId: Long,

    @field:JsonProperty("score")
    val score: Long
)