package net.bscs22.schoolportal.repositories
import net.bscs22.schoolportal.entities.academics.College
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CollegeRepository : JpaRepository<College, String> {
    @Query("""
        SELECT c FROM College c 
        WHERE LOWER(c.collegeCode) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(c.collegeName) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    fun searchColleges(query: String, sort: Sort): List<College>
}