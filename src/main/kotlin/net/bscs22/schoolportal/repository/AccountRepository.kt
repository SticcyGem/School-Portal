package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.LoginDetails
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AccountRepository(private val jdbcTemplate: JdbcTemplate) {
    private val accountRowMapper = RowMapper { rs: ResultSet, _: Int ->
        LoginDetails(
            accountId = rs.getString("account_id"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            roleName = rs.getString("role_name")
        )
    }

    fun findByEmail(email: String): LoginDetails? {
        val sql = """
            SELECT * 
                FROM VIEW_LOGINS 
                WHERE email = ?
            """
        return try {
            jdbcTemplate.queryForObject(sql, accountRowMapper, email)
        } catch (_: EmptyResultDataAccessException) {
            null
        }
    }
}