package net.bscs22.schoolportal.dtos.enrollment

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class UpdateTermConfigRequest(
    @field:JsonProperty("enrollment_start")
    val enrollmentStart: LocalDate,

    @field:JsonProperty("enrollment_end")
    val enrollmentEnd: LocalDate,

    @field:JsonProperty("force_close")
    val forceClose: Boolean = false
)