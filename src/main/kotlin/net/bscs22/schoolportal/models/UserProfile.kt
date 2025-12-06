package net.bscs22.schoolportal.models

import jakarta.persistence.*
import org.springframework.data.domain.Persistable // Import this!
import java.util.UUID

@Entity
@Table(name = "user_profiles", schema = "school")
class UserProfile(
    @Id
    @Column(name = "account_id")
    var accountId: UUID,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "middle_name")
    var middleName: String? = null,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    var account: Account? = null
) : Persistable<UUID> { // <--- 1. Implement Interface

    // 2. Add a transient flag to track state
    @Transient
    private var isNewEntry: Boolean = true

    // 3. Override standard methods
    override fun getId(): UUID? = accountId

    override fun isNew(): Boolean = isNewEntry

    // 4. Update flag after saving or loading
    @PostLoad
    @PostPersist
    fun markNotNew() {
        isNewEntry = false
    }
}