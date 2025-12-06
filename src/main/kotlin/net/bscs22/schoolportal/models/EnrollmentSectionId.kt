package net.bscs22.schoolportal.models;

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class EnrollmentSectionId(
    @Column(name = "enrollment_no")
    var enrollmentNo: Long? = null,

    @Column(name = "section_no")
    var sectionNo: Long? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EnrollmentSectionId) return false
        return enrollmentNo == other.enrollmentNo && sectionNo == other.sectionNo
    }

    override fun hashCode(): Int {
        return 31 * (enrollmentNo?.hashCode() ?: 0) + (sectionNo?.hashCode() ?: 0)
    }
}