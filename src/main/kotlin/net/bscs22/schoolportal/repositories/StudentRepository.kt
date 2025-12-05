package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentRepository : JpaRepository<Student, UUID> {
    fun existsByStudentNo(studentNo: Long): Boolean
}