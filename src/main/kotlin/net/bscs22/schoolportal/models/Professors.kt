package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.ProfessorStatus
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "professors", schema = "school")
class Professors(
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @Column(name = "professor_id", unique = true, nullable = false)
    var professorId: String,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "professor_status", columnDefinition = "school.professor_status_type")
    var professorStatus: ProfessorStatus = ProfessorStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "employee_type", columnDefinition = "school.employee_type_enum")
    var employeeType: EmployeeType,

    @Column(name = "prof_hired_at")
    var hiredAt: LocalDateTime = LocalDateTime.now()
)