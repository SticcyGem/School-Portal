package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.course.CourseRequest
import net.bscs22.schoolportal.entities.academics.Course
import net.bscs22.schoolportal.repositories.CollegeRepository
import net.bscs22.schoolportal.repositories.CourseRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepo: CourseRepository,
    private val collegeRepo: CollegeRepository
) {
    fun getAllCourses(query: String): List<Course> {
        val sort = Sort.by("courseCode")
        return if (query.isBlank()) courseRepo.findAll(sort)
        else courseRepo.searchCourses(query, sort)
    }

    @Transactional
    fun createCourse(req: CourseRequest): Course {
        if (courseRepo.existsById(req.courseCode)) {
            throw IllegalArgumentException("Course code '${req.courseCode}' already exists.")
        }
        val college = collegeRepo.findById(req.collegeCode)
            .orElseThrow { IllegalArgumentException("College '${req.collegeCode}' not found") }

        return courseRepo.save(Course(
            courseCode = req.courseCode,
            courseName = req.courseName,
            courseTier = req.courseTier,
            college = college
        ))
    }

    @Transactional
    fun updateCourse(code: String, req: CourseRequest): Course {
        val course = courseRepo.findById(code)
            .orElseThrow { IllegalArgumentException("Course '$code' not found.") }

        val college = collegeRepo.findById(req.collegeCode)
            .orElseThrow { IllegalArgumentException("College '${req.collegeCode}' not found") }

        course.courseName = req.courseName
        course.courseTier = req.courseTier
        course.college = college // Handle college transfer

        return courseRepo.save(course)
    }

    @Transactional
    fun deleteCourse(code: String) {
        if (!courseRepo.existsById(code)) throw IllegalArgumentException("Course not found")
        courseRepo.deleteById(code)
    }
}