package net.bscs22.schoolportal.entities.commons.keys

import java.io.Serializable

data class PendingEnrollmentKey(
    val enrollmentNo: Long = 0,
    val sectionNo: Long = 0
) : Serializable