package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.StudentProfile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class StudentRepository(private val jdbcTemplate: JdbcTemplate) : ProfileRepository<StudentProfile> {
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        StudentProfile(
            accountId = rs.getString("account_id"),
            email = rs.getString("email"),
            studentNo = rs.getInt("student_no"),
            studentName = rs.getString("student_name"),
            studentType = rs.getString("student_type"),
            yearLevel = rs.getInt("year_level"),
            blockNumber = rs.getInt("block_number"),
            blockName = rs.getString("block_name"),
            courseCode = rs.getString("course_code")
        )
    }

    override fun findByAccountId(accountId: String): StudentProfile? {
        val sql = """
            SELECT * 
            FROM VIEW_STUDENT_DETAILS
            WHERE account_id = ?
        """.trimIndent()

        return try {
            jdbcTemplate.queryForObject(sql, rowMapper, accountId)
        } catch (_: org.springframework.dao.EmptyResultDataAccessException) {
            null
        }
    }
}
