package net.bscs22.schoolportal.dtos.block
import com.fasterxml.jackson.annotation.JsonProperty

data class BlockRequest(
    @field:JsonProperty("block_no") val blockNo: Long?,
    @field:JsonProperty("course_code") val courseCode: String,
    @field:JsonProperty("year_level") val yearLevel: Long,
    @field:JsonProperty("block_number") val blockNumber: Long
)