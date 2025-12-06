package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.keys.SectionBlockKey

@Entity
@Table(name = "section_blocks", schema = "school")
@IdClass(SectionBlockKey::class)
class SectionBlock(
    @Id
    @Column(name = "section_no")
    val sectionNo: Long,

    @Id
    @Column(name = "block_no")
    val blockNo: Long,

    // Relationship back to Section
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_no", insertable = false, updatable = false)
    val section: Section? = null,

    // Relationship to Block (Assuming you have a Block entity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_no", insertable = false, updatable = false)
    val block: Block? = null
)