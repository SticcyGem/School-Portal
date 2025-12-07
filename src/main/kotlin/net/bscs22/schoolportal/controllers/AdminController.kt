package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.dtos.auth.RegisterAdminRequest
import net.bscs22.schoolportal.dtos.auth.RegisterProfessorRequest
import net.bscs22.schoolportal.dtos.auth.RegisterStudentRequest
import net.bscs22.schoolportal.dtos.subject.CreateSubjectRequest
import net.bscs22.schoolportal.dtos.subject.UpdateSubjectRequest
import net.bscs22.schoolportal.dtos.user.UpdateUserRequest
import net.bscs22.schoolportal.dtos.user.UserResponse
import net.bscs22.schoolportal.services.AccountService
import net.bscs22.schoolportal.services.AuthService
import net.bscs22.schoolportal.services.SubjectService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val authService: AuthService,
    private val accountService: AccountService,
    private val subjectService: SubjectService
) {

    // --- SEARCH ENDPOINT ---

    @GetMapping("/users/search")
    fun searchUsers(@RequestParam("q") query: String): ResponseEntity<List<UserResponse>> {
        val results = accountService.searchUsers(query)
        return ResponseEntity.ok(results)
    }

    // --- REGISTER ENDPOINTS ---

    @PostMapping("/register/student")
    fun registerStudent(@RequestBody req: RegisterStudentRequest): ResponseEntity<Any> {
        return try {
            val msg = authService.registerStudent(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody req: RegisterProfessorRequest): ResponseEntity<Any> {
        return try {
            val msg = authService.registerProfessor(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/register")
    fun registerAdmin(@RequestBody req: RegisterAdminRequest): ResponseEntity<Any> {
        return try {
            val msg = authService.registerAdmin(req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // --- USER MANAGEMENT ---

    @PutMapping("/users/{accountId}")
    fun updateUser(
        @PathVariable accountId: UUID,
        @RequestBody req: UpdateUserRequest
    ): ResponseEntity<Any> {
        return try {
            val msg = accountService.updateAccountDetails(accountId, req)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/users")
    fun listAllUsers(): ResponseEntity<List<UserResponse>> {
        val userList = accountService.getAllUserDetails()
        return ResponseEntity.ok(userList)
    }

    // --- SUBJECT CRUD ---

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
}