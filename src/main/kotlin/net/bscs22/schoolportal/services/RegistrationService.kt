package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.auth.RegisterAdminRequest
import net.bscs22.schoolportal.dtos.auth.RegisterProfessorRequest
import net.bscs22.schoolportal.dtos.auth.RegisterStudentRequest
import net.bscs22.schoolportal.mappers.AuthMapper
import net.bscs22.schoolportal.entities.accounts.Account
import net.bscs22.schoolportal.entities.accounts.UserProfile
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.ProfessorRepository
import net.bscs22.schoolportal.repositories.StudentRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegistrationService(
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studentRepository: StudentRepository,
    private val professorRepository: ProfessorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authMapper: AuthMapper
) {

    @Transactional
    fun registerStudent(req: RegisterStudentRequest): String {
        if (req.studentNo != null && studentRepository.existsByStudentNo(req.studentNo)) {
            throw IllegalArgumentException("Student No ${req.studentNo} already in use")
        }

        val account = authMapper.toAccount(req)
        val profile = authMapper.toUserProfile(req)

        // 1. Create Base Account & Profile
        val savedAccount = createBaseAccount(account, profile, req.password, 1L) // 1L = STUDENT role

        // 2. Create Specific Student Record
        val student = authMapper.toStudent(req).apply {
            this.account = savedAccount
            this.accountId = savedAccount.accountId
        }
        studentRepository.save(student)

        return "Student created successfully: ${req.studentNo ?: "ID Pending"}"
    }

    @Transactional
    fun registerProfessor(req: RegisterProfessorRequest): String {
        if (req.professorId != null && professorRepository.existsByProfessorId(req.professorId)) {
            throw IllegalArgumentException("Professor ID ${req.professorId} exists")
        }

        val account = authMapper.toAccount(req)
        val profile = authMapper.toUserProfile(req)

        val savedAccount = createBaseAccount(account, profile, req.password, 2L) // 2L = PROFESSOR

        val professor = authMapper.toProfessor(req).apply {
            this.account = savedAccount
            this.accountId = savedAccount.accountId
        }
        professorRepository.save(professor)

        return "Professor created successfully: ${req.professorId}"
    }

    @Transactional
    fun registerAdmin(req: RegisterAdminRequest): String {
        val account = authMapper.toAccount(req)
        val profile = authMapper.toUserProfile(req)

        createBaseAccount(account, profile, req.password, 3L) // 3L = ADMIN

        return "Admin created successfully: ${req.email}"
    }

    /**
     * Shared logic for all user types:
     * 1. Check Email
     * 2. Hash Password
     * 3. Save Account
     * 4. Save Profile
     * 5. Assign Role
     */
    private fun createBaseAccount(
        account: Account,
        profile: UserProfile,
        rawPassword: String,
        roleId: Long
    ): Account {
        if (accountRepository.existsByEmail(account.email)) {
            throw IllegalArgumentException("Email '${account.email}' is already in use")
        }

        account.passwordHash = passwordEncoder.encode(rawPassword)
        val savedAccount = accountRepository.save(account)

        profile.account = savedAccount
        profile.accountId = savedAccount.accountId
        userProfileRepository.save(profile)

        // Native query to insert role (fastest way for many-to-many link tables)
        accountRepository.addRole(savedAccount.accountId, roleId)

        return savedAccount
    }
}