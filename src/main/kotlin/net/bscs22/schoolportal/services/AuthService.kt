package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.Account
import net.bscs22.schoolportal.models.Professor
import net.bscs22.schoolportal.models.Student
import net.bscs22.schoolportal.models.UserProfile
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.StudentType
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
    // Note: Assuming you have RoleRepository or similar available to fetch roles by name/ID
) {

    fun authenticate(email: String, pass: String): Map<String, Any>? {
        val account = accountRepository.findByEmail(email) ?: return null

        // 1. Validate Password
        if (!passwordEncoder.matches(pass, account.passwordHash)) {
            return null
        }

        // 2. Generate Token
        val roleName = account.roles.firstOrNull()?.roleName ?: "USER"

        val token = jwtService.generateToken(
            account.email,
            account.accountId,
            roleName
        )

        // 3. Determine Role & Fetch Profile View
        val roles = account.roles.map { it.roleName }
        var profileData: Any? = null

        if (roles.contains("STUDENT")) {
            profileData = studentDetailRepository.findById(account.accountId).orElse(null)
        } else if (roles.contains("PROFESSOR")) {
            profileData = professorDetailRepository.findById(account.accountId).orElse(null)
        } else {
            // Fallback for Admin or basic users
            val basicProfile = userProfileRepository.findById(account.accountId).orElse(null)
            profileData = mapOf(
                "name" to "${basicProfile?.lastName}, ${basicProfile?.firstName}",
                "email" to account.email
            )
        }

        return mapOf(
            "token" to token,
            "roles" to roles,
            "accountId" to account.accountId,
            "profile" to (profileData ?: "Profile not found")
        )
    }

    @Transactional
    fun registerStudent(
        email: String,
        rawPassword: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        studentNo: Long?,
        educationLevel: EducationLevel,
        studentType: StudentType,
        courseCode: String,
        blockNo: Long?
    ): String {
        if (accountRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        if (studentNo != null && studentRepository.existsByStudentNo(studentNo)) {
            throw IllegalArgumentException("Student No already in use")
        }

        // 1. Create base Account
        val account = Account(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)
        )

        // 2. SAVE ACCOUNT FIRST and CAPTURE the result
        // This ensures 'savedAccount' is the strictly managed entity with the generated ID
        val savedAccount = accountRepository.save(account)

        // 3. Create UserProfile using 'savedAccount'
        // Since we added Persistable, Hibernate will now correctly INSERT this.
        val profile = UserProfile(
            accountId = savedAccount.accountId,
            account = savedAccount, // Link the SAVED entity
            firstName = firstName,
            middleName = middleName,
            lastName = lastName
        )

        // 4. Create Student using 'savedAccount'
        val student = Student(
            accountId = savedAccount.accountId,
            account = savedAccount, // Link the SAVED entity
            studentNo = studentNo,
            educationLevel = educationLevel,
            studentType = studentType,
            courseCode = courseCode,
            blockNo = blockNo
        )

        // 5. Save Dependents
        userProfileRepository.save(profile)
        studentRepository.save(student)

        // 6. Add Role
        accountRepository.addRole(savedAccount.accountId, 1L)

        return if (studentNo != null) "Student created: $studentNo" else "Student created (ID pending generation)"
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

        // 1. Create Account
        val account = Account(email = email, passwordHash = passwordEncoder.encode(rawPassword))

        // 2. Create UserProfile, linking Account
        val profile = UserProfile(
            accountId = account.accountId,
            account = account, // CRITICAL: Link the Account entity reference
            firstName = firstName,
            lastName = lastName
        )

        // 3. Create Professor, linking Account
        val professor = Professor(
            accountId = account.accountId,
            account = account, // CRITICAL: Link the Account entity reference (Assuming Professor.kt uses @MapsId)
            professorId = professorId,
            employeeType = employeeType
        )

        // 4. Persistence
        accountRepository.save(account)
        userProfileRepository.save(profile)
        professorRepository.save(professor)

        // 5. Add Role
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

        // 1. Create Account
        val newAccount = Account(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)
        )

        // 2. Create UserProfile, linking Account
        val profile = UserProfile(
            accountId = newAccount.accountId,
            account = newAccount, // CRITICAL: Link the Account entity reference
            firstName = firstName,
            lastName = lastName
        )

        // 3. Persistence
        val savedAccount = accountRepository.save(newAccount)
        userProfileRepository.save(profile)

        // 4. Add Role
        savedAccount.accountId.let { id ->
            accountRepository.addRole(id, 3L)
        }
        return "Admin account created successfully for ${savedAccount.email}"
    }
}