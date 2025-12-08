package net.bscs22.schoolportal.models.views

import jakarta.persistence.*
import net.bscs22.schoolportal.models.keys.PendingEnrollmentKey
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_pending_enrollment_details", schema = "school")
@IdClass(PendingEnrollmentKey::class)
class PendingEnrollmentDetail(
    @Id
    @Column(name = "enrollment_no")
    val enrollmentNo: Long,

    @Id
    @Column(name = "section_no")
    val sectionNo: Long,

    @Column(name = "student_account_id")
    val studentAccountId: UUID,

    @Column(name = "student_no")
    val studentNo: Long,

    @Column(name = "student_name")
    val studentName: String,

    @Column(name = "term_name")
    val termName: String,

    @Column(name = "subject_code")
    val subjectCode: String,

    @Column(name = "subject_name")
    val subjectName: String,

    @Column(name = "units")
    val units: Long,

    @Column(name = "course_code")
    val courseCode: String,

    @Column(name = "full_schedule")
    val fullSchedule: String?
)