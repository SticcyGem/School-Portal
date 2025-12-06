package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.RoomType

@Entity
@Table(name = "rooms", schema = "school")
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_no")
    var roomNo: Long? = null,

    @Column(name = "room_name", length = 50, nullable = false)
    var roomName: String,

    @Column(name = "room_capacity")
    var roomCapacity: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", columnDefinition = "school.room_type_enum", nullable = false)
    var roomType: RoomType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_no")
    var building: Building? = null
)