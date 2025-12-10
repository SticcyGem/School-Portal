package net.bscs22.schoolportal.entities.views

import java.io.Serializable
import java.util.UUID

data class CreditedSubjectId(
    val studentAccountId: UUID? = null,
    val subjectCode: String? = null
) : Serializable