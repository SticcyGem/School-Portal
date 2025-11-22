package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.ProfessorProfile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProfessorRepository(private val jdbcTemplate: JdbcTemplate) : ProfileRepository<ProfessorProfile> {
    private val rowMapper = RowMapper { rs: ResultSet, _ ->
        ProfessorProfile(
            accountId = rs.getString("account_id"),
            email = rs.getString("email"),
            professorNo = rs.getString("professor_no"),
            professorName = rs.getString("professor_name"),
            employeeType = rs.getString("employee_type")
        )
    }

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
