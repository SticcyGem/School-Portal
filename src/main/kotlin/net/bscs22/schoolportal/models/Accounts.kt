package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.models.enums.AuthProvider
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "accounts", schema = "school")
class Accounts (
    @Id
    @Column(name = "account_id")
    var accountId: UUID = UUID.randomUUID(),

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "school.account_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var status: AccountStatus = AccountStatus.Active,

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", columnDefinition = "school.auth_provider_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var authProvider: AuthProvider = AuthProvider.Local
)