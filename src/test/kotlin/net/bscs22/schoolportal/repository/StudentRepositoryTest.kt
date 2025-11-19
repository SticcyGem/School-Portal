package net.bscs22.schoolportal.repository

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
// @Transactional rolls back the transaction after the test,
// so your test data doesn't mess up your real database.
@Transactional
class StudentRepositoryTest {

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Test
    fun `should find student by student number`() {
        // 1. Create a student (or use one you know exists from your insert script)
        // Assuming 'STU-001' exists from your init script
        val accountId = "STU-001"

        // 2. Run the query
        val student = studentRepository.findByAccountId(accountId)

        // 3. Verify
        assertNotNull(student, "Student should exist")
        assertEquals("Regular", student?.studentType)
    }
}