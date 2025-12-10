package net.bscs22.schoolportal.dtos.college

import com.fasterxml.jackson.annotation.JsonProperty

data class CollegeRequest(
    @field:JsonProperty("college_code") val collegeCode: String,
    @field:JsonProperty("college_name") val collegeName: String
)