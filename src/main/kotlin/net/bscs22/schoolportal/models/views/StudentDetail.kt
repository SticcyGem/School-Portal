package net.bscs22.schoolportal.models.views

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vw_student_profile", schema = "school")
class StudentDetail(
    @Id
    @Column(name = "account_id")
    val accountId: UUID,

    @Column(name = "student_no")
    val studentNo: Long,

    @Column(name = "student_name")
    val studentName: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "student_status")
    val studentStatus: String,

    @Column(name = "student_type")
    val studentType: String,

    @Column(name = "year_level")
    val yearLevel: Long,

    @Column(name = "education_level")
    val educationLevel: String
)