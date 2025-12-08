package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.auth.RegisterAdminRequest
import net.bscs22.schoolportal.dtos.auth.RegisterProfessorRequest
import net.bscs22.schoolportal.dtos.auth.RegisterStudentRequest
import net.bscs22.schoolportal.dtos.subject.CreateSubjectRequest
import net.bscs22.schoolportal.dtos.subject.UpdateSubjectRequest
import net.bscs22.schoolportal.dtos.user.AdminResetPasswordRequest
import net.bscs22.schoolportal.dtos.user.UpdateUserRequest
import net.bscs22.schoolportal.dtos.user.UserResponse
import net.bscs22.schoolportal.dtos.enrollment.RejectEnrollmentRequest
import net.bscs22.schoolportal.dtos.enrollment.AdminEnrollmentDetailResponse
import net.bscs22.schoolportal.services.EnrollmentService
import net.bscs22.schoolportal.entities.academics.Subject
import net.bscs22.schoolportal.services.AccountService
import net.bscs22.schoolportal.services.RegistrationService
import net.bscs22.schoolportal.services.SubjectService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import kotlin.math.min

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val registrationService: RegistrationService,
    private val accountService: AccountService,
    private val subjectService: SubjectService,
    private val enrollmentService: EnrollmentService
) {

    // --- SEARCH ENDPOINT ---

    @GetMapping("/users/search")
    fun searchUsers(
        @RequestParam("q") query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<UserResponse>>> {
        val safeSize = min(size, 20)
        val userPage = accountService.searchUsers(query, page, safeSize)
        return ResponseEntity.ok(ApiResponse.success(userPage))
    }

    // --- REGISTER ENDPOINTS ---

    @PostMapping("/register/student")
    fun registerStudent(@RequestBody req: RegisterStudentRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerStudent(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody req: RegisterProfessorRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerProfessor(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/register")
    fun registerAdmin(@RequestBody req: RegisterAdminRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerAdmin(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // --- USER MANAGEMENT ---

    @PutMapping("/users/{accountId}")
    fun updateUser(
        @PathVariable accountId: UUID,
        @RequestBody req: UpdateUserRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = accountService.updateAccountDetails(accountId, req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @GetMapping("/users")
    fun listAllUsers(): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val userList = accountService.getAllUserDetails()
        return ResponseEntity.ok(ApiResponse.success(userList))
    }

    // --- SUBJECT CRUD ---

    @GetMapping("/subjects")
    fun getAllSubjects(): ResponseEntity<ApiResponse<List<Subject>>> {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getAllSubjects()))
    }

    @PostMapping("/subjects")
    fun createSubject(@RequestBody req: CreateSubjectRequest): ResponseEntity<ApiResponse<Subject>> {
        val created = subjectService.createSubject(
            req.subjectCode, req.subjectName, req.lecUnits, req.labUnits
        )
        return ResponseEntity.ok(ApiResponse.success(created, "Subject created successfully"))
    }

    @PutMapping("/subjects/{subjectCode}")
    fun updateSubject(
        @PathVariable subjectCode: String,
        @RequestBody req: UpdateSubjectRequest
    ): ResponseEntity<ApiResponse<Subject>> {
        val updated = subjectService.updateSubject(
            subjectCode, req.subjectName, req.lecUnits, req.labUnits
        )
        return ResponseEntity.ok(ApiResponse.success(updated, "Subject updated successfully"))
    }

    @DeleteMapping("/subjects/{subjectCode}")
    fun deleteSubject(@PathVariable subjectCode: String): ResponseEntity<ApiResponse<Nothing>> {
        val msg = subjectService.deleteSubject(subjectCode)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // --- PASSWORD RESET ---

    @PutMapping("/users/{accountId}/password")
    fun resetUserPassword(
        @PathVariable accountId: UUID,
        @RequestBody req: AdminResetPasswordRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = accountService.adminResetPassword(accountId, req.newPass)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // --- ENROLLMENT MANAGEMENT ---

    @GetMapping("/enrollments/pending")
    fun getPendingEnrollments(): ResponseEntity<ApiResponse<List<AdminEnrollmentDetailResponse>>> {
        val pending = enrollmentService.getPendingEnrollments()
        return ResponseEntity.ok(ApiResponse.success(pending))
    }

    @PostMapping("/approve/{enrollmentId}")
    fun approveEnrollment(@PathVariable enrollmentId: Long): ResponseEntity<ApiResponse<Nothing>> {
        val msg = enrollmentService.approveEnrollment(enrollmentId)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/reject/{enrollmentId}")
    fun rejectEnrollment(
        @PathVariable enrollmentId: Long,
        @RequestBody req: RejectEnrollmentRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = enrollmentService.rejectEnrollment(enrollmentId, req.reason)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }
}