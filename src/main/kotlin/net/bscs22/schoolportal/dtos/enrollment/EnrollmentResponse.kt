package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty

data class EnrollmentResponse(
    @field:JsonProperty("enrollment_no")
    val enrollmentNo: Long,

    @field:JsonProperty("student_name")
    val studentName: String,

    @field:JsonProperty("student_id")
    val studentId: String,

    @field:JsonProperty("course_code")
    val courseCode: String,

    @field:JsonProperty("term_name")
    val termName: String,

    @field:JsonProperty("total_units")
    val totalUnits: Long,

    val sections: List<EnrollmentSectionResponse>
)