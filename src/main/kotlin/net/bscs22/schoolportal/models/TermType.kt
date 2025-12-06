package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "term_types", schema = "school")
class TermType(
    @Id
    @Column(name = "term_type_id", length = 2)
    var termTypeId: String,

    @Column(name = "term_name", nullable = false, length = 50)
    var termName: String
)