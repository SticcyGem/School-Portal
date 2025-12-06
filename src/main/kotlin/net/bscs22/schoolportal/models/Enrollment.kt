package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EnrollmentStatus
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "enrollments", schema = "school")
class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_no")
    var enrollmentNo: Long? = null,

    @Column(name = "enrolled_at", nullable = false)
    var enrolledAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "enrollment_status", columnDefinition = "school.enrollment_status_enum")
    var enrollmentStatus: EnrollmentStatus = EnrollmentStatus.DRAFT,

    @Column(name = "remarks", length = 500)
    var remarks: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    var studentAccount: Account,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_term_no", nullable = false)
    var term: AcademicTerm,

    @OneToMany(mappedBy = "enrollment", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sections: MutableSet<EnrollmentSection> = mutableSetOf()
)