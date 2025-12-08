package net.bscs22.schoolportal.models.accounts

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.ProfessorStatus
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "professors", schema = "school")
class Professor(
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    var account: Account? = null,

    @Column(name = "professor_id", unique = true)
    var professorId: String? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "professor_status", columnDefinition = "school.professor_status_type")
    var professorStatus: ProfessorStatus = ProfessorStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "employee_type", columnDefinition = "school.employee_type_enum")
    var employeeType: EmployeeType? = null,

    @Column(name = "prof_hired_at")
    var hiredAt: LocalDateTime = LocalDateTime.now()

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