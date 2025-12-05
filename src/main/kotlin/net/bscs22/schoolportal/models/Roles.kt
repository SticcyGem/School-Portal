package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "roles", schema = "school")
class Roles(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_no")
    val roleNo: Long? = null,

    @Column(name = "role_name", unique = true, nullable = false, length = 20)
    val roleName: String
)