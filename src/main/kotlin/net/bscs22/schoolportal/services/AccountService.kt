package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun changePassword(
        accountId: UUID,
        oldPass: String,
        newPass: String
    ): String {
        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Account not found") }
        if (!passwordEncoder.matches(oldPass, account.passwordHash)) {
            throw IllegalArgumentException("Incorrect old password")
        }
        account.passwordHash = passwordEncoder.encode(newPass)
        accountRepository.save(account)

        return "Password updated successfully"
    }

    @Transactional
    fun updateAccountDetails(
        targetAccountId: UUID,
        newEmail: String?,
        newFirstName: String?,
        newLastName: String?,
        newStatus: AccountStatus?
    ): String {
        val account = accountRepository.findById(targetAccountId)
            .orElseThrow { IllegalArgumentException("Account not found") }

        if (newEmail != null && newEmail.isNotEmpty() && newEmail != account.email) {
            if (accountRepository.existsByEmail(newEmail)) {
                throw IllegalArgumentException("Email already in use")
            }
            account.email = newEmail
        }

        if (newStatus != null) {
            account.status = newStatus
        }
        accountRepository.save(account)

        val profile = userProfileRepository.findById(targetAccountId)
            .orElseThrow { IllegalArgumentException("Profile not found") }

        if (newFirstName != null) profile.firstName = newFirstName
        if (newLastName != null) profile.lastName = newLastName
        userProfileRepository.save(profile)

        return "Account details updated for ${account.email}"
    }
}