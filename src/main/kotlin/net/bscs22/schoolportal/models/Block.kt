package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "blocks", schema = "school")
class Block(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_no")
    var blockNo: Long? = null,

    @Column(name = "year_level", nullable = false)
    var yearLevel: Long,

    @Column(name = "block_number", nullable = false)
    var blockNumber: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", nullable = false)
    var course: Course
)