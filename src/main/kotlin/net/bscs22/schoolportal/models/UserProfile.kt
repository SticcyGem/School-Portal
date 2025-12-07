package net.bscs22.schoolportal.models

import jakarta.persistence.*
import org.springframework.data.domain.Persistable
import java.util.UUID

@Entity
@Table(name = "user_profiles", schema = "school")
class UserProfile(
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "middle_name")
    var middleName: String? = null,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    var account: Account? = null

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