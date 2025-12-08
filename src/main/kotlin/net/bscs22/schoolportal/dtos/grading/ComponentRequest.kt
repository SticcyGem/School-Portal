package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class ComponentRequest(
    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("weight_percent")
    val weightPercent: Long,

    @field:JsonProperty("children")
    val children: List<ChildComponentRequest>
)