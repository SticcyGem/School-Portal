package net.bscs22.schoolportal.models.views

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_section_grade_sheet", schema = "school")
class SectionGradeRow(
    @Id
    @Column(name = "view_id")
    val viewId: Long? = null,

    @Column(name = "enrollment_no")
    val enrollmentNo: Long,

    @Column(name = "component_id")
    val componentId: Long? = null,

    @Column(name = "section_no")
    val sectionNo: Long,

    @Column(name = "student_account_id")
    val studentAccountId: UUID,

    @Column(name = "student_name")
    val studentName: String,

    @Column(name = "raw_score")
    val rawScore: Long?
)