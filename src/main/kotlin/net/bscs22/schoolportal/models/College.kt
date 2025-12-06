package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "colleges", schema = "school")
class College(
    @Id
    @Column(name = "college_code", length = 10)
    var collegeCode: String,

    @Column(name = "college_name", length = 100, nullable = false)
    var collegeName: String
)