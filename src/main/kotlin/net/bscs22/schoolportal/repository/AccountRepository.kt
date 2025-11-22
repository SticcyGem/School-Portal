package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.LoginDetails
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AccountRepository(private val jdbcTemplate: JdbcTemplate) {
    /**
     * Maps a row from the [ResultSet] to a [LoginDetails] object.
     *
     * This mapper reads the following columns:
     * - `account_id` – The unique identifier of the account.
     * - `email` – The email associated with the account.
     * - `password_hash` – The hashed password.
     * - `role_name` – The role assigned to the account.
     *
     * @return a populated [LoginDetails] instance from the current row.
     */
    private val accountRowMapper = RowMapper { rs: ResultSet, _: Int ->
        LoginDetails(
            accountId = rs.getString("account_id"),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            roleName = rs.getString("role_name")
        )
    }
    /**
     * Retrieves a [LoginDetails] record by its associated email address.
     *
     * Executes a query against the `VIEW_LOGINS` view and returns the matching
     * login information if found. If no record exists for the provided email,
     * the function returns `null`.
     *
     * @param email The email address used to search for the login record.
     * @return The corresponding [LoginDetails] object, or `null` if no match is found.
     */
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