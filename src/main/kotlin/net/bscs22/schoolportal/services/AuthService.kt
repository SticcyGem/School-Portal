package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.auth.*
import net.bscs22.schoolportal.mappers.AuthMapper
import net.bscs22.schoolportal.models.Account
import net.bscs22.schoolportal.models.UserProfile
import net.bscs22.schoolportal.repositories.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studentRepository: StudentRepository,
    private val professorRepository: ProfessorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val studentDetailRepository: StudentDetailRepository,
    private val professorDetailRepository: ProfessorDetailRepository,
    private val authMapper: AuthMapper
) {

    // --- LOGIN ---
    fun authenticate(req: LoginRequest): AuthResponse? {
        val account = accountRepository.findByEmail(req.email) ?: return null

        if (!passwordEncoder.matches(req.password, account.passwordHash)) {
            return null
        }

        val roleName = account.roles.firstOrNull()?.roleName ?: "USER"
        val token = jwtService.generateToken(account.email, account.accountId, roleName)
        val roles = account.roles.map { it.roleName }

        val profileData: Any? = when {
            roles.contains("STUDENT") -> studentDetailRepository.findById(account.accountId).orElse(null)
            roles.contains("PROFESSOR") -> professorDetailRepository.findById(account.accountId).orElse(null)
            else -> {
                val p = userProfileRepository.findById(account.accountId).orElse(null)
                if (p != null) mapOf(
                    "name" to "${p.lastName}, ${p.firstName}",
                    "email" to account.email
                ) else null
            }
        }

        return AuthResponse(token, roles, account.accountId, profileData)
    }

    // --- REGISTER STUDENT ---
    @Transactional
    fun registerStudent(req: RegisterStudentRequest): String {
        if (req.studentNo != null && studentRepository.existsByStudentNo(req.studentNo)) {
            throw IllegalArgumentException("Student No already in use")
        }

        val account = authMapper.toAccount(req)

        val savedAccount = createBaseAccount(
            account = account,
            profile = authMapper.toUserProfile(req),
            rawPassword = req.password,
            roleId = 1L
        )

        val student = authMapper.toStudent(req)
        student.account = savedAccount
        student.accountId = savedAccount.accountId
        studentRepository.save(student)

        return "Student created: ${req.studentNo ?: "ID Pending"}"
    }

    // --- REGISTER PROFESSOR ---
    @Transactional
    fun registerProfessor(req: RegisterProfessorRequest): String {
        if (req.professorId != null && professorRepository.existsByProfessorId(req.professorId)) {
            throw IllegalArgumentException("Professor ID exists")
        }

        val account = authMapper.toAccount(req)

        val savedAccount = createBaseAccount(
            account = account,
            profile = authMapper.toUserProfile(req),
            rawPassword = req.password,
            roleId = 2L
        )

        val professor = authMapper.toProfessor(req)
        professor.account = savedAccount
        professor.accountId = savedAccount.accountId
        professorRepository.save(professor)

        return "Professor created: ${req.professorId ?: "ID Pending"}"
    }

    // --- REGISTER ADMIN ---
    @Transactional
    fun registerAdmin(req: RegisterAdminRequest): String {
        val account = authMapper.toAccount(req)

        createBaseAccount(
            account = account,
            profile = authMapper.toUserProfile(req),
            rawPassword = req.password,
            roleId = 3L
        )

        return "Admin created: ${req.email}"
    }

    // --- PRIVATE HELPER ---
    private fun createBaseAccount(
        account: Account,
        profile: UserProfile,
        rawPassword: String,
        roleId: Long
    ): Account {
        if (accountRepository.existsByEmail(account.email)) {
            throw IllegalArgumentException("Email already in use")
        }

        account.passwordHash = passwordEncoder.encode(rawPassword)

        val savedAccount = accountRepository.save(account)

        profile.account = savedAccount
        profile.accountId = savedAccount.accountId
        userProfileRepository.save(profile)

        accountRepository.addRole(savedAccount.accountId, roleId)

        return savedAccount
    }
}