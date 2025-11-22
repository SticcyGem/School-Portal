package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.ProfessorProfile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
/**
 * Repository for accessing professor profile data from the database.
 *
 * This class implements [ProfileRepository] to provide database operations
 * specific to the [ProfessorProfile] model. It uses a JDBC-based approach
 * to map query results into Kotlin data classes.
 *
 * @property jdbcTemplate The Spring JDBC template used for executing SQL queries.
 */
class ProfessorRepository(private val jdbcTemplate: JdbcTemplate) : ProfileRepository<ProfessorProfile> {
    /**
     * Maps rows from the `view_professor_details` view into [ProfessorProfile] objects.
     */
    private val rowMapper = RowMapper { rs: ResultSet, _ ->
        ProfessorProfile(
            accountId = rs.getString("account_id"),
            email = rs.getString("email"),
            professorNo = rs.getString("professor_no"),
            professorName = rs.getString("professor_name"),
            employeeType = rs.getString("employee_type")
        )
    }

    /**
     * Retrieves a professor profile by account ID.
     *
     * @param accountId The unique account identifier.
     * @return A [ProfessorProfile] if found, or `null` if no record exists.
     */
    override fun findByAccountId(accountId: String): ProfessorProfile? {
        val sql = """
            SELECT * 
            FROM view_professor_details
            WHERE account_id = ?
        """.trimIndent()

        return try {
            jdbcTemplate.queryForObject(sql, rowMapper, accountId)
        } catch (_: org.springframework.dao.EmptyResultDataAccessException) {
            null
        }
    }
}
