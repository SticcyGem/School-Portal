package net.bscs22.schoolportal.entities.grades

import jakarta.persistence.*
import net.bscs22.schoolportal.entities.enrollments.Enrollment
import net.bscs22.schoolportal.entities.commons.keys.GradeKey

@Entity
@Table(name = "grades", schema = "school")
@IdClass(GradeKey::class)
class Grade(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gc_no", nullable = false)
    var gradeComponent: GradeComponent,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_no", nullable = false)
    var enrollment: Enrollment,

    @Column(name = "raw_score", nullable = false)
    var rawScore: Long
)