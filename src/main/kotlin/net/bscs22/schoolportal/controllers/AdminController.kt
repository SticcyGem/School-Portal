package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.StudentType
import net.bscs22.schoolportal.services.AccountService
import net.bscs22.schoolportal.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val authService: AuthService,
    private val accountService: AccountService
) {
    data class RegisterStudentRequest(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val studentNo: Long,
        val educationLevel: EducationLevel,
        val studentType: StudentType
    )

    data class RegisterProfessorRequest(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val professorId: String,
        val employeeType: EmployeeType
    )

    data class RegisterAdminRequest(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String
    )

    data class UpdateAccountRequest(
        val email: String?,
        val firstName: String?,
        val lastName: String?,
        val status: AccountStatus?
    )

    @PostMapping("/register/student")
    fun registerStudent(@RequestBody req: RegisterStudentRequest): ResponseEntity<Any> {
        return try {
            val msg = authService.registerStudent(
                req.email,
                req.password,
                req.firstName,
                req.lastName,
                req.studentNo,
                req.educationLevel,
                req.studentType
            )
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody req: RegisterProfessorRequest): ResponseEntity<Any> {
        return try {
            val msg = authService.registerProfessor(
                req.email,
                req.password,
                req.firstName,
                req.lastName,
                req.professorId,
                req.employeeType
            )
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register")
    fun registerAdmin(@RequestBody request: RegisterAdminRequest): ResponseEntity<Any> {
        return try {
            val resultMessage = authService.registerAdmin(
                request.email,
                request.password,
                request.firstName,
                request.lastName
            )
            ResponseEntity.ok(mapOf("message" to resultMessage))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/users/{accountId}")
    fun updateUser(
        @PathVariable accountId: UUID,
        @RequestBody req: UpdateAccountRequest
    ): ResponseEntity<Any> {
        return try {
            val msg = accountService.updateAccountDetails(
                accountId,
                req.email,
                req.firstName,
                req.lastName,
                req.status
            )
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}