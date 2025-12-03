package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.Accounts
import net.bscs22.schoolportal.models.UserProfiles
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.LoginRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val loginRepository: LoginRepository,
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun authenticate(email: String, rawPassword: String): Map<String, Any>? {
        val user = loginRepository.findByEmail(email) ?: return null

        if (user.status != "Active") {
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
    fun registerAdmin(email: String, rawPassword: String, firstName: String, lastName: String): String {
        // 1. Check if email exists
        if (accountRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }

        // 2. Create Account
        val newAccount = Accounts(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)
        )
        val savedAccount = accountRepository.save(newAccount)

        // 3. Create Profile
        val profile = UserProfiles(
            accountId = savedAccount.accountId,
            firstName = firstName,
            lastName = lastName
        )
        userProfileRepository.save(profile)

        // 4. Assign Role (3 = ADMIN)
        // Ensure accountId is not null (it shouldn't be after save)
        savedAccount.accountId.let { id ->
            accountRepository.addRole(id, 3L)
        }

        return "Admin account created successfully for ${savedAccount.email}"
    }
}