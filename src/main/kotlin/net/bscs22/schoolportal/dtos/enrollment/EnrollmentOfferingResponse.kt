package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty

data class EnrollmentOfferingResponse(
    @field:JsonProperty("term_name")
    val termName: String,

    @field:JsonProperty("student_type")
    val studentType: String,

    @field:JsonProperty("is_block_based")
    val isBlockBased: Boolean,

    @field:JsonProperty("enrollment_status")
    val enrollmentStatus: String,

    val remarks: String?,
    val sections: List<SectionOfferingResponse>
)