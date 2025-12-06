package net.bscs22.schoolportal.models

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_profiles", schema = "school")
class UserProfile(
    // The account_id is the primary key and the foreign key
    @Id
    @Column(name = "account_id")
    var accountId: UUID,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "middle_name")
    var middleName: String? = null,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    // --- New: One-to-One relationship with Account ---
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indicates that the PK (accountId) is also the FK
    @JoinColumn(name = "account_id")
    var account: Account? = null // Inverse side of the mapping
)