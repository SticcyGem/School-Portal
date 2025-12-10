package net.bscs22.schoolportal.repositories
import net.bscs22.schoolportal.entities.academics.Block
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BlockRepository : JpaRepository<Block, Long> {
    @Query("SELECT b FROM Block b JOIN FETCH b.course")
    override fun findAll(sort: Sort): List<Block>

    @Query("""
        SELECT b FROM Block b 
        JOIN FETCH b.course 
        WHERE LOWER(b.course.courseCode) LIKE LOWER(CONCAT('%', :query, '%'))
           OR CAST(b.yearLevel AS string) LIKE :query
           OR CAST(b.blockNumber AS string) LIKE :query
    """)
    fun searchBlocks(query: String, sort: Sort): List<Block>
}