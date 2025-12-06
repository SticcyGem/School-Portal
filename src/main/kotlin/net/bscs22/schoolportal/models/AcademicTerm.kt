package net.bscs22.schoolportal.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "academic_terms", schema = "school")
class AcademicTerm(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "academic_term_no")
    var academicTermNo: Long? = null,

    @Column(name = "term_name", nullable = false, length = 50)
    var termName: String,

    @Column(name = "term_start_date", nullable = false)
    var termStartDate: LocalDate,

    @Column(name = "term_end_date", nullable = false)
    var termEndDate: LocalDate,

    @Column(name = "enrollment_start_date", nullable = false)
    var enrollmentStartDate: LocalDate,

    @Column(name = "enrollment_end_date", nullable = false)
    var enrollmentEndDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sy_no", nullable = false)
    var schoolYear: SchoolYear,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_type_id", nullable = false)
    var termType: TermType
)