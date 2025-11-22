package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.StudentProfile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
/**
 * Repository responsible for fetching student profile data from the database.
 *
 * This class implements [ProfileRepository] to encapsulate SQL operations
 * related to the [StudentProfile] model. It converts database result rows into
 * Kotlin objects using JDBC mapping.
 *
 * @property jdbcTemplate The Spring JDBC template used to perform SQL queries.
 */
class StudentRepository(private val jdbcTemplate: JdbcTemplate) : ProfileRepository<StudentProfile> {
    /**
     * Maps rows from the `view_student_details` view into [StudentProfile] objects.
     */
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

    /**
     * Retrieves a student profile by account ID.
     *
     * @param accountId The unique account identifier.
     * @return A [StudentProfile] if found, or `null` if the account does not have a student profile.
     */
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
