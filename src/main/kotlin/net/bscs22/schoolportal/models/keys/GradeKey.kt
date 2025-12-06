package net.bscs22.schoolportal.models.keys

import java.io.Serializable

data class GradeKey(
    var gradeComponent: Long? = null,
    var enrollment: Long? = null
) : Serializable