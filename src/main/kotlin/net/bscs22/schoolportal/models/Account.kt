package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.models.enums.AuthProvider
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.domain.Persistable
import java.util.UUID

@Entity
@Table(name = "accounts", schema = "school")
class Account(
    @Id
    @Column(name = "account_id")
    var accountId: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "school.account_status")
    var status: AccountStatus = AccountStatus.ACTIVE,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "auth_provider", columnDefinition = "school.auth_provider_enum")
    var authProvider: AuthProvider = AuthProvider.LOCAL,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "account_roles",
        schema = "school",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "role_no")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
) : Persistable<UUID> {
    @OneToOne(mappedBy = "account", cascade = [CascadeType.REMOVE, CascadeType.REFRESH])
    var profile: UserProfile? = null

    @Transient
    private var isNewEntry: Boolean = true

    override fun getId(): UUID = accountId

    override fun isNew(): Boolean = isNewEntry

    @PostLoad
    @PostPersist
    fun markNotNew() {
        isNewEntry = false
    }
}