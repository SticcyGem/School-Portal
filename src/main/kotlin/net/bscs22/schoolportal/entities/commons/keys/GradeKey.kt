package net.bscs22.schoolportal.entities.commons.keys

import java.io.Serializable

data class GradeKey(
    val gradeComponent: Long = 0,
    val enrollment: Long = 0
) : Serializable