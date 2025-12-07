package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.dtos.auth.* // <--- CRITICAL IMPORT
import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.services.AccountService
import net.bscs22.schoolportal.services.AuthService
import net.bscs22.schoolportal.services.EnrollmentService
import net.bscs22.schoolportal.services.SubjectService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val authService: AuthService,
    private val accountService: AccountService,
    private val enrollmentService: EnrollmentService,
    private val subjectService: SubjectService
) {
    // --- DTOs for other endpoints ---
    data class UpdateAccountRequest(
        val email: String?,
        val firstName: String?,
        val lastName: String?,
        val status: AccountStatus?
    )

    data class CreateSubjectRequest(
        val subjectCode: String,
        val subjectName: String,
        val lecUnits: Long,
        val labUnits: Long
    )

    data class UpdateSubjectRequest(
        val subjectName: String,
        val lecUnits: Long,
        val labUnits: Long
    )

    data class RejectRequest(val reason: String)

    // --- REGISTER ENDPOINTS (FIXED) ---

    @PostMapping("/register/student")
    fun registerStudent(@RequestBody req: RegisterStudentRequest): ResponseEntity<Any> {
        return try {
            // FIX: Pass the single DTO object
            val msg = authService.registerStudent(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody req: RegisterProfessorRequest): ResponseEntity<Any> {
        return try {
            // FIX: Pass the single DTO object
            val msg = authService.registerProfessor(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register")
    fun registerAdmin(@RequestBody req: RegisterAdminRequest): ResponseEntity<Any> {
        return try {
            // FIX: Pass the single DTO object
            val msg = authService.registerAdmin(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // --- OTHER ENDPOINTS (Unchanged) ---

    @PutMapping("/users/{accountId}")
    fun updateUser(
        @PathVariable accountId: UUID,
        @RequestBody req: UpdateAccountRequest
    ): ResponseEntity<Any> {
        return try {
            val msg = accountService.updateAccountDetails(
                accountId, req.email, req.firstName, req.lastName, req.status
            )
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/approve/{enrollmentId}")
    fun approveEnrollment(@PathVariable enrollmentId: Long): ResponseEntity<Any> {
        return try {
            val msg = enrollmentService.approveEnrollment(enrollmentId)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/reject/{enrollmentId}")
    fun rejectEnrollment(
        @PathVariable enrollmentId: Long,
        @RequestBody req: RejectRequest
    ): ResponseEntity<Any> {
        return try {
            val msg = enrollmentService.rejectEnrollment(enrollmentId, req.reason)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/subjects")
    fun getAllSubjects(): ResponseEntity<Any> {
        return ResponseEntity.ok(subjectService.getAllSubjects())
    }

    @PostMapping("/subjects")
    fun createSubject(@RequestBody req: CreateSubjectRequest): ResponseEntity<Any> {
        return try {
            val created = subjectService.createSubject(
                req.subjectCode, req.subjectName, req.lecUnits, req.labUnits
            )
            ResponseEntity.ok(mapOf("message" to "Subject created successfully", "data" to created))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/subjects/{subjectCode}")
    fun updateSubject(
        @PathVariable subjectCode: String,
        @RequestBody req: UpdateSubjectRequest
    ): ResponseEntity<Any> {
        return try {
            val updated = subjectService.updateSubject(
                subjectCode, req.subjectName, req.lecUnits, req.labUnits
            )
            ResponseEntity.ok(mapOf("message" to "Subject updated successfully", "data" to updated))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/subjects/{subjectCode}")
    fun deleteSubject(@PathVariable subjectCode: String): ResponseEntity<Any> {
        return try {
            val msg = subjectService.deleteSubject(subjectCode)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (_: DataIntegrityViolationException) {
            ResponseEntity.badRequest().body(mapOf("error" to "Cannot delete subject: It is currently in use."))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    data class UserDetailDTO(
        val accountId: UUID,
        val email: String,
        val firstName: String,
        val lastName: String,
        val status: String,
        val roles: List<String>
    )

    @GetMapping("/users")
    fun listAllUsers(): ResponseEntity<List<UserDetailDTO>> {
        val userList = accountService.getAllUserDetails()
        return ResponseEntity.ok(userList)
    }
}