package net.bscs22.schoolportal.models

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_profiles", schema = "school")
class UserProfiles (
    @Id
    @Column(name = "account_id")
    var accountId: UUID? = null,

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Column(name = "middle_name")
    var middleName: String? = null
)