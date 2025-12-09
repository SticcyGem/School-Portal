package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.entities.commons.enums.SystemStatus
import java.time.LocalDate

data class SystemStatusResponse(
    @field:JsonProperty("school_year")
    val schoolYear: String,

    @field:JsonProperty("term_name")
    val termName: String,

    @field:JsonProperty("status")
    val status: SystemStatus,

    @field:JsonProperty("enrollment_start")
    val enrollmentStart: LocalDate,

    @field:JsonProperty("enrollment_end")
    val enrollmentEnd: LocalDate
)
