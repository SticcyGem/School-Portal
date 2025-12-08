package net.bscs22.schoolportal.models.views

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.SubjectStatus
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_student_credited_subjects", schema = "school")
@IdClass(CreditedSubjectId::class)
class CreditedSubject(
    @Id
    @Column(name = "student_account_id")
    val studentAccountId: UUID,

    @Id
    @Column(name = "subject_code")
    val subjectCode: String,

    @Column(name = "subject_name")
    val subjectName: String,

    @Column(name = "lec_units")
    val lecUnits: Long,

    @Column(name = "lab_units")
    val labUnits: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_status")
    val subjectStatus: SubjectStatus
)