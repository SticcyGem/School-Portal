package net.bscs22.schoolportal.models

import jakarta.persistence.*
import net.bscs22.schoolportal.models.enums.EducationLevel
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "courses", schema = "school")
class Course(
    @Id
    @Column(name = "course_code", length = 10)
    var courseCode: String,

    @Column(name = "course_name", length = 100, nullable = false)
    var courseName: String,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "course_tier", columnDefinition = "school.education_level_enum", nullable = false)
    var courseTier: EducationLevel,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_code", nullable = false)
    var college: College,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_subjects",
        schema = "school",
        joinColumns = [JoinColumn(name = "course_code")],
        inverseJoinColumns = [JoinColumn(name = "subject_code")]
    )
    var subjects: MutableSet<Subject> = mutableSetOf()
)