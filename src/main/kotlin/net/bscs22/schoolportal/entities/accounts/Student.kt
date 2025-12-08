package net.bscs22.schoolportal.models.accounts

import jakarta.persistence.*
import net.bscs22.schoolportal.models.Block
import net.bscs22.schoolportal.models.Course
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.StudentStatus
import net.bscs22.schoolportal.models.enums.StudentType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.domain.Persistable
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "students", schema = "school")
class Student(
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    var account: Account? = null,

    @Column(name = "student_no", unique = true)
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
    var courseCode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", insertable = false, updatable = false)
    var course: Course? = null,

    @Column(name = "block_no")
    var blockNo: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_no", insertable = false, updatable = false)
    var block: Block? = null

) : Persistable<UUID> {
    @Transient
    private var isNewEntry: Boolean = true

    override fun getId(): UUID? = accountId

    override fun isNew(): Boolean = isNewEntry

    @PostLoad
    @PostPersist
    fun markNotNew() {
        isNewEntry = false
    }
}