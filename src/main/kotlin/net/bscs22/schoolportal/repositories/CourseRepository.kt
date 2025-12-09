package net.bscs22.schoolportal.repositories
import net.bscs22.schoolportal.entities.academics.Course
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : JpaRepository<Course, String> {
    fun findBySubjectsSubjectCode(subjectCode: String): Course?

    @Query("SELECT c FROM Course c JOIN FETCH c.college")
    override fun findAll(sort: Sort): List<Course>

    @Query("""
        SELECT c FROM Course c 
        JOIN FETCH c.college
        WHERE LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(c.college.collegeCode) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    fun searchCourses(query: String, sort: Sort): List<Course>
}