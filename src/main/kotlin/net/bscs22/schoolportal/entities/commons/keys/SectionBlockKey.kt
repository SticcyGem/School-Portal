package net.bscs22.schoolportal.models.keys

import java.io.Serializable

data class SectionBlockKey(
    val sectionNo: Long = 0,
    val blockNo: Long = 0
) : Serializable