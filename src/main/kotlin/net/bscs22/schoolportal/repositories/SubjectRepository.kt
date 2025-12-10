package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.entities.academics.Subject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : JpaRepository<Subject, String> {

    @Query("""
        SELECT s FROM Subject s 
        WHERE LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :query, '%')) 
           OR LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    fun searchSubjects(query: String, pageable: Pageable): Page<Subject>
}