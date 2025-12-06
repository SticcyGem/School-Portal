package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.StudentStatus
import net.bscs22.schoolportal.models.enums.StudentType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "students", schema = "school")
class Student(
    @Id
    @Column(name = "account_id")
    var accountId: UUID,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // <--- ADD THIS ANNOTATION
    @JoinColumn(name = "account_id")
    var account: Account? = null,

    @Column(name = "student_no", unique = true, insertable = false, updatable = false)
    var studentNo: Long? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "student_status", columnDefinition = "school.student_status_enum")
    var studentStatus: StudentStatus = StudentStatus.ADMITTED,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "education_level", columnDefinition = "school.education_level_enum")
    var educationLevel: EducationLevel = EducationLevel.UNDERGRADUATE,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "student_type", columnDefinition = "school.student_type_enum")
    var studentType: StudentType = StudentType.REGULAR,

    @Column(name = "year_level")
    var yearLevel: Long = 1,

    @Column(name = "student_admitted_at")
    var studentAdmittedAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "course_code", nullable = false)
    var courseCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", insertable = false, updatable = false)
    var course: Course? = null,

    @Column(name = "block_no")
    var blockNo: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_no", insertable = false, updatable = false)
    var block: Block? = null
)