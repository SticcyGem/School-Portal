package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.accounts.Professor
import net.bscs22.schoolportal.models.enums.DeliveryMode // Import the new Enum
import net.bscs22.schoolportal.models.times.AcademicTerm
import net.bscs22.schoolportal.models.times.Schedule
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sections", schema = "school")
class Section(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_no")
    var sectionNo: Long? = null,

    @Column(name = "available_slots")
    var availableSlots: Long = 40,

    @Column(name = "version")
    var version: Long = 0,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "delivery_mode", columnDefinition = "school.delivery_mode_enum")
    var deliveryMode: DeliveryMode = DeliveryMode.FACE_TO_FACE,

    @Column(name = "subject_code")
    var subjectCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_code", insertable = false, updatable = false)
    var subject: Subject,

    @Column(name = "professor_id")
    var professorId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", referencedColumnName = "professor_id", insertable = false, updatable = false)
    var professor: Professor? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_term_no")
    var academicTerm: AcademicTerm? = null,

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    var schedules: MutableList<Schedule> = mutableListOf(),

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    var blocks: MutableList<SectionBlock> = mutableListOf()
)