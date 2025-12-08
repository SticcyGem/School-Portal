package net.bscs22.schoolportal.entities.commons.keys

import java.io.Serializable

data class SectionBlockKey(
    val sectionNo: Long = 0,
    val blockNo: Long = 0
) : Serializable