package net.bscs22.schoolportal.models.keys

import java.io.Serializable

data class PendingEnrollmentKey(
    val enrollmentNo: Long = 0,
    val sectionNo: Long = 0
) : Serializable