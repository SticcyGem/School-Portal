package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.Accounts
import net.bscs22.schoolportal.models.Professors
import net.bscs22.schoolportal.models.Students
import net.bscs22.schoolportal.models.UserProfiles
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.StudentType
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.LoginRepository
import net.bscs22.schoolportal.repositories.ProfessorRepository
import net.bscs22.schoolportal.repositories.StudentRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studentRepository: StudentRepository,
    private val professorRepository: ProfessorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun authenticate(
        email: String,
        rawPassword: String
    ): Map<String, Any>? {
        val user = loginRepository.findByEmail(email) ?: return null

        if (user.status != "ACTIVE") {
            return null
        }

        if (passwordEncoder.matches(rawPassword, user.passwordHash)) {
            val roleName = when(user.roleNo) {
                1L -> "STUDENT"
                2L -> "PROFESSOR"
                3L -> "ADMIN"
                else -> "UNKNOWN"
            }
            val token = jwtService.generateToken(user.email, user.accountId, roleName)
            return mapOf(
                "token" to token,
                "accountId" to user.accountId.toString(),
                "role" to roleName
            )
        }
        return null
    }

    @Transactional
    fun registerStudent(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String,
        studentNo: Long,
        educationLevel: EducationLevel,
        studentType: StudentType
    ): String {
        if (accountRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        if (studentRepository.existsByStudentNo(studentNo)) {
            throw IllegalArgumentException("Student No already in use")
        }
        val account = accountRepository.save(Accounts(email = email, passwordHash = passwordEncoder.encode(rawPassword)))
        userProfileRepository.save(UserProfiles(accountId = account.accountId, firstName = firstName, lastName = lastName))
        studentRepository.save(
            Students(
                accountId = account.accountId,
                studentNo = studentNo,
                educationLevel = educationLevel,
                studentType = studentType
            )
        )
        accountRepository.addRole(account.accountId, 1L)
        return "Student created: $studentNo"
    }

    @Transactional
    fun registerProfessor(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String,
        professorId: String,
        employeeType: EmployeeType
    ): String {
        if (accountRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        if (professorRepository.existsByProfessorId(professorId)) {
            throw IllegalArgumentException("Professor ID already in use")
        }
        val account = accountRepository.save(Accounts(email = email, passwordHash = passwordEncoder.encode(rawPassword)))
        userProfileRepository.save(UserProfiles(accountId = account.accountId, firstName = firstName, lastName = lastName))
        professorRepository.save(
            Professors(
                accountId = account.accountId,
                professorId = professorId,
                employeeType = employeeType
            )
        )
        accountRepository.addRole(account.accountId, 2L)
        return "Professor created: $professorId"
    }

    @Transactional
    fun registerAdmin(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String
    ): String {
        if (accountRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        val newAccount = Accounts(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)
        )
        val savedAccount = accountRepository.save(newAccount)
        val profile = UserProfiles(
            accountId = savedAccount.accountId,
            firstName = firstName,
            lastName = lastName
        )
        userProfileRepository.save(profile)
        savedAccount.accountId.let { id ->
            accountRepository.addRole(id, 3L)
        }

        return "Admin account created successfully for ${savedAccount.email}"
    }
}