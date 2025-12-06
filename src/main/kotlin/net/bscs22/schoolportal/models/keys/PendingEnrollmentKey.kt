package net.bscs22.schoolportal.models.keys

import java.io.Serializable

data class PendingEnrollmentKey(
    // These property names MUST match the @Id property names in PendingEnrollmentDetail
    var enrollmentNo: Long = 0,
    var sectionNo: Long = 0
) : Serializable