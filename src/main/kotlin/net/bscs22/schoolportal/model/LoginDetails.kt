package net.bscs22.schoolportal.model

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.util.UUID

@Entity
@Immutable
@Table(name = "vwlogin", schema = "school")
class LoginDetails(
    @Id
    @Column(name = "account_id")
        val accountId: UUID,
    @Column(name = "email")
        val email: String,
    @Column(name = "password_hash")
        val passwordHash: String,
    @Column(name = "status")
        val status: String,
    @Column(name = "role_no")
        val roleNo: Long
)