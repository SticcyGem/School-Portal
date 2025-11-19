package net.bscs22.schoolportal.model

import com.fasterxml.jackson.annotation.JsonProperty

data class StudentModel (
    @field:JsonProperty("student_no")
    val studentNo: Int,

    @field:JsonProperty("account_id")
    val accountId: String,

    @field:JsonProperty("student_type")
    val studentType: String,

    @field:JsonProperty("year_level")
    val yearLevel: Int,

    @field:JsonProperty("block_id")
    val blockId: Int
)