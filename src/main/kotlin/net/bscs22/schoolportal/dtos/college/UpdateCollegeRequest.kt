package net.bscs22.schoolportal.dtos.college

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateCollegeRequest(
    @field:JsonProperty("college_name") val collegeName: String
)