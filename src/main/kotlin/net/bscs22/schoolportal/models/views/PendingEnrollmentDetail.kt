package net.bscs22.schoolportal.models.views

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import net.bscs22.schoolportal.models.keys.PendingEnrollmentKey // Import the key class
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_pending_enrollment_details", schema = "school")
@IdClass(PendingEnrollmentKey::class) // <-- FIX: Apply the external key class here
class PendingEnrollmentDetail(

    // Composite Key Fields - MUST be annotated @Id and match the names in PendingEnrollmentKey.kt
    @Id
    @Column(name = "enrollment_no")
    val enrollmentNo: Long,

    @Id
    @Column(name = "section_no")
    val sectionNo: Long,

    // Other View Fields (match the column names aggregated in the SQL view)
    @Column(name = "student_no")
    val studentNo: Long,

    @Column(name = "student_name")
    val studentName: String,

    @Column(name = "course_code")
    val courseCode: String,

    @Column(name = "term_name")
    val termName: String,

    @Column(name = "subject_code")
    val subjectCode: String,

    @Column(name = "subject_name") // Assuming the SQL view aliases subject_name correctly
    val subjectTitle: String,

    @Column(name = "units")
    val units: Long,

    @Column(name = "full_schedule")
    val schedule: String
)