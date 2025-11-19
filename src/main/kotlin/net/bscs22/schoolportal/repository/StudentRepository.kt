package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.StudentModel
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class StudentRepository(private val jdbcTemplate: JdbcTemplate) {

    private val studentRowMapper = RowMapper { rs: ResultSet, _: Int ->
        StudentModel(
            studentNo = rs.getInt("student_no"),
            accountId = rs.getString("account_id"),
            studentType = rs.getString("student_type"),
            yearLevel = rs.getInt("year_level"),
            blockId = rs.getInt("block_id")
        )
    }

    fun findByAccountId(accountId: String): StudentModel? {
        val sql = """
            SELECT student_no, account_id, student_type, year_level, block_id
            FROM school.students
            WHERE account_id = ?
        """
        return try {
            jdbcTemplate.queryForObject(sql, studentRowMapper, accountId)
        } catch (_: EmptyResultDataAccessException) {
            null // Return null if no student is found
        }
    }
}