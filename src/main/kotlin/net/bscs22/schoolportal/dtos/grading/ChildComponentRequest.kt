package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class ChildComponentRequest(
    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("max_score")
    val maxScore: Long
)