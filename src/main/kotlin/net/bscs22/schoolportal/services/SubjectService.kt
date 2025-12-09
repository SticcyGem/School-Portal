package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.subject.SubjectResponse
import net.bscs22.schoolportal.dtos.subject.UpdateSubjectRequest
import net.bscs22.schoolportal.entities.academics.Course
import net.bscs22.schoolportal.entities.academics.Subject
import net.bscs22.schoolportal.repositories.CourseRepository
import net.bscs22.schoolportal.repositories.SubjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val courseRepository: CourseRepository
) {
    // 1. UPDATE TO RETURN DTO
    @Transactional(readOnly = true)
    fun getAllSubjects(query: String, pageable: Pageable): Page<SubjectResponse> {
        val subjectsPage = if (query.isBlank()) {
            subjectRepository.findAll(pageable)
        } else {
            subjectRepository.searchSubjects(query, pageable)
        }

        return subjectsPage.map { subject ->
            val course = courseRepository.findBySubjectsSubjectCode(subject.subjectCode)
            val prereq = subject.prerequisites.firstOrNull()

            SubjectResponse(
                subjectCode = subject.subjectCode,
                subjectName = subject.subjectName,
                lecUnits = subject.lecUnits,
                labUnits = subject.labUnits,
                prerequisiteCode = prereq?.subjectCode,
                courseCode = course?.courseCode
            )
        }
    }

    fun getAllCourses(): List<Course> = courseRepository.findAll()

    @Transactional
    fun createSubject(
        subjectCode: String,
        subjectName: String,
        lecUnits: Int,
        labUnits: Int,
        prerequisiteCode: String?,
        courseCode: String?
    ): Subject {
        if (subjectRepository.existsById(subjectCode)) {
            throw IllegalArgumentException("Subject code '$subjectCode' already exists.")
        }

        val newSubject = Subject(
            subjectCode = subjectCode,
            subjectName = subjectName,
            lecUnits = lecUnits.toLong(),
            labUnits = labUnits.toLong()
        )

        if (!prerequisiteCode.isNullOrBlank() && prerequisiteCode != "None") {
            val prereq = subjectRepository.findById(prerequisiteCode)
                .orElseThrow { IllegalArgumentException("Prerequisite '$prerequisiteCode' not found") }
            newSubject.prerequisites.add(prereq)
        }

        val savedSubject = subjectRepository.save(newSubject)

        if (!courseCode.isNullOrBlank()) {
            val course = courseRepository.findById(courseCode)
                .orElseThrow { IllegalArgumentException("Course '$courseCode' not found") }
            course.subjects.add(savedSubject)
            courseRepository.save(course)
        }

        return savedSubject
    }

    // 2. UPDATED UPDATE LOGIC
    @Transactional
    fun updateSubject(code: String, req: UpdateSubjectRequest): Subject {
        val subject = subjectRepository.findById(code)
            .orElseThrow { IllegalArgumentException("Subject '$code' not found.") }

        subject.subjectName = req.subjectName
        subject.lecUnits = req.lecUnits.toLong()
        subject.labUnits = req.labUnits.toLong()

        // Handle Prerequisite
        subject.prerequisites.clear()
        if (!req.prerequisiteCode.isNullOrBlank() && req.prerequisiteCode != "None") {
            val prereq = subjectRepository.findById(req.prerequisiteCode)
                .orElseThrow { IllegalArgumentException("Prerequisite '${req.prerequisiteCode}' not found") }
            subject.prerequisites.add(prereq)
        }

        // Handle Course Switching
        val currentCourse = courseRepository.findBySubjectsSubjectCode(code)

        if (req.courseCode != currentCourse?.courseCode) {
            if (currentCourse != null) {
                currentCourse.subjects.remove(subject)
                courseRepository.save(currentCourse)
            }
            if (!req.courseCode.isNullOrBlank()) {
                val newCourse = courseRepository.findById(req.courseCode)
                    .orElseThrow { IllegalArgumentException("Course '${req.courseCode}' not found") }
                newCourse.subjects.add(subject)
                courseRepository.save(newCourse)
            }
        }

        return subjectRepository.save(subject)
    }

    @Transactional
    fun deleteSubject(code: String): String {
        if (!subjectRepository.existsById(code)) {
            throw IllegalArgumentException("Subject '$code' not found.")
        }
        subjectRepository.deleteById(code)
        return "Subject '$code' successfully deleted."
    }
}