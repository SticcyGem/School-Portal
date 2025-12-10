package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class GradeSheetResponse(
    @field:JsonProperty("components")
    val components: List<HeaderResponse>,

    @field:JsonProperty("students")
    val students: List<StudentRowResponse>
)