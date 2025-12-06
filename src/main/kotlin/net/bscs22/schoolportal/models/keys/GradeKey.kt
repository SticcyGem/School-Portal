// GradeKey.kt
package net.bscs22.schoolportal.models.keys

import java.io.Serializable

data class GradeKey(
    val gradeComponent: Long = 0,
    val enrollment: Long = 0
) : Serializable