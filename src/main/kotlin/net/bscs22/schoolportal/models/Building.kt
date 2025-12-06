package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "buildings", schema = "school")
class Building(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_no")
    var buildingNo: Long? = null,

    @Column(name = "building_name", length = 100, nullable = false)
    var buildingName: String
)