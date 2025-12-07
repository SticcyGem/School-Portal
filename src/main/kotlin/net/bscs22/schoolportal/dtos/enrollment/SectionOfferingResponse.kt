package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.models.enums.SectionStatus

data class SectionOfferingResponse(
    @field:JsonProperty("section_no")
    val sectionNo: Long,

    @field:JsonProperty("section_name")
    val sectionName: String,

    @field:JsonProperty("subject_code")
    val subjectCode: String,

    @field:JsonProperty("subject_title")
    val subjectTitle: String,

    val units: Long,
    val schedule: String,
    val status: SectionStatus
)