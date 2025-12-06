package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.SubjectStatus
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "enrollment_sections", schema = "school")
class EnrollmentSection(
    @EmbeddedId
    var id: EnrollmentSectionId = EnrollmentSectionId(),

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "subject_status", columnDefinition = "school.subject_status_enum")
    var subjectStatus: SubjectStatus = SubjectStatus.ENROLLED,

    @MapsId("enrollmentNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_no")
    var enrollment: Enrollment? = null,

    @MapsId("sectionNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_no")
    var section: Section? = null
)