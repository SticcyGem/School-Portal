package net.bscs22.schoolportal.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "school_years", schema = "school")
class SchoolYear(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sy_no")
    var syNo: Long? = null,

    @Column(name = "sy_name", nullable = false, length = 50)
    var syName: String, // e.g. "2025-2026"

    @Column(name = "sy_start_date", nullable = false)
    var syStartDate: LocalDate,

    @Column(name = "sy_end_date", nullable = false)
    var syEndDate: LocalDate
)