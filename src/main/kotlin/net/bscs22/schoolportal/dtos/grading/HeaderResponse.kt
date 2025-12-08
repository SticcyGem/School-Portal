package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class HeaderResponse(
    @field:JsonProperty("id")
    val id: Long,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("max_score")
    val maxScore: Long,

    @field:JsonProperty("parent_name")
    val parentName: String?
)