package net.bscs22.schoolportal.models

import jakarta.persistence.*

@Entity
@Table(name = "subjects", schema = "school")
class Subject(
    @Id
    @Column(name = "subject_code", length = 10)
    var subjectCode: String,

    @Column(name = "subject_name", length = 100, nullable = false)
    var subjectName: String,

    @Column(name = "lec_units", nullable = false)
    var lecUnits: Long = 0,

    @Column(name = "lab_units", nullable = false)
    var labUnits: Long = 0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "subject_prereqs",
        schema = "school",
        joinColumns = [JoinColumn(name = "subject_code")],
        inverseJoinColumns = [JoinColumn(name = "prereq_subject_code")]
    )
    var prerequisites: MutableSet<Subject> = mutableSetOf()
)