package net.bscs22.schoolportal.models.views

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_professor_profile", schema = "school")
class ProfessorDetail(
    @Id
    @Column(name = "account_id")
    val accountId: UUID,

    @Column(name = "professor_id")
    val professorId: String,

    @Column(name = "professor_name")
    val professorName: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "professor_status")
    val professorStatus: String,

    @Column(name = "employee_type")
    val employeeType: String
)