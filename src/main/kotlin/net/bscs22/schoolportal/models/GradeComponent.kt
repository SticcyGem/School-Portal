package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "grade_components", schema = "school")
class GradeComponent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gc_no")
    var gcNo: Long? = null,

    @Column(name = "gc_name", nullable = false)
    var gcName: String,

    @Column(name = "max_score", nullable = false)
    var maxScore: Long,

    @Column(name = "percent", nullable = false)
    var percent: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_no", nullable = false)
    var section: Section,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_gc")
    var parentGc: GradeComponent? = null
)