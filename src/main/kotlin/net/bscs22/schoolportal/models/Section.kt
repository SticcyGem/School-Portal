package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.DeliveryMode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sections", schema = "school")
class Section(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_no")
    var sectionNo: Long? = null,

    @Column(name = "available_slots", nullable = false)
    var availableSlots: Long = 40,

    @Column(name = "version", nullable = false)
    var version: Long = 0,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "delivery_mode", columnDefinition = "school.delivery_mode_enum", nullable = false)
    var deliveryMode: DeliveryMode,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_code", nullable = false)
    var subject: Subject,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", referencedColumnName = "professor_id", nullable = false)
    var professor: Professor,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_term_no", nullable = false)
    var term: AcademicTerm,

    @OneToMany(mappedBy = "section", cascade = [CascadeType.ALL], orphanRemoval = true)
    var schedules: MutableList<Schedule> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "section_blocks",
        schema = "school",
        joinColumns = [JoinColumn(name = "section_no")],
        inverseJoinColumns = [JoinColumn(name = "block_no")]
    )
    var blocks: MutableSet<Block> = mutableSetOf(),

    @OneToMany(mappedBy = "section", cascade = [CascadeType.ALL], orphanRemoval = true)
    var gradeComponents: MutableList<GradeComponent> = mutableListOf()
)