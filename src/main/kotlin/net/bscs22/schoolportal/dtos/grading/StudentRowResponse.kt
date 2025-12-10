package net.bscs22.schoolportal.dtos.grading

import com.fasterxml.jackson.annotation.JsonProperty

data class StudentRowResponse(
    @field:JsonProperty("enrollment_id")
    val enrollmentId: Long,

    @field:JsonProperty("student_name")
    val studentName: String,

    @field:JsonProperty("student_id")
    val studentId: String,

    @field:JsonProperty("grades")
    val grades: Map<Long, Long>
)