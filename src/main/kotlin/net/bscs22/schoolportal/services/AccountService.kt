package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.controllers.AdminController
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

    @Transactional(readOnly = true)
    fun getAllUserDetails(): List<AdminController.UserDetailDTO> {
        // NOTE: This assumes you join Account, UserProfile, and Roles data.
        // For simplicity, we mock data retrieval here:

        // In reality, this should be a custom query joining Account, UserProfile, and Roles
        val accounts = accountRepository.findAll() // Fetch all accounts

        return accounts.mapNotNull { account ->
            val profile = userProfileRepository.findById(account.accountId).orElse(null)

            if (profile != null) {
                AdminController.UserDetailDTO(
                    accountId = account.accountId,
                    email = account.email,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    status = account.status.name,
                    roles = account.roles.map { it.roleName }
                )
            } else {
                null // Skip accounts without a profile (if any)
            }
        }
    }
}