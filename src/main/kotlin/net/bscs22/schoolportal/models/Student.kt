package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.StudentStatus
import net.bscs22.schoolportal.models.enums.StudentType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "students", schema = "school")
class Student(
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @Column(name = "student_no", unique = true, nullable = false)
    var studentNo: Long,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "student_status", columnDefinition = "school.student_status_type")
    var studentStatus: StudentStatus = StudentStatus.ADMITTED,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "education_level", columnDefinition = "school.education_level_type")
    var educationLevel: EducationLevel = EducationLevel.UNDERGRADUATE,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "student_type", columnDefinition = "school.student_type_enum")
    var studentType: StudentType = StudentType.REGULAR,

    @Column(name = "year_level")
    var yearLevel: Long = 1,

    @Column(name = "student_admitted_at")
    var admittedAt: LocalDateTime = LocalDateTime.now()
)