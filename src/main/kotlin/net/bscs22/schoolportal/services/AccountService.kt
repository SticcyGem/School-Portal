package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.user.UpdateUserRequest
import net.bscs22.schoolportal.dtos.user.UserResponse
import net.bscs22.schoolportal.mappers.UserMapper
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper
) {
    // --- SEARCH ---
    @Transactional(readOnly = true)
    fun searchUsers(query: String, page: Int, size: Int): Page<UserResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("email").ascending())
        val accountsPage = accountRepository.searchAccounts(query, pageable)

        return accountsPage.map { account ->
            val profile = userProfileRepository.findById(account.accountId).orElse(null)
            if (profile != null) {
                userMapper.toUserResponse(account, profile)
            } else {
                null
            }
        }
    }

    // --- CHANGE PASSWORD ---
    @Transactional
    fun changePassword(accountId: UUID, oldPass: String, newPass: String): String {
        val account = accountRepository.findById(accountId)
            .orElseThrow { IllegalArgumentException("Account not found") }

        if (!passwordEncoder.matches(oldPass, account.passwordHash)) {
            throw IllegalArgumentException("Incorrect old password")
        }

        account.passwordHash = passwordEncoder.encode(newPass)
        accountRepository.save(account)

        return "Password updated successfully"
    }

    // --- UPDATE ACCOUNT DETAILS ---
    @Transactional
    fun updateAccountDetails(targetAccountId: UUID, req: UpdateUserRequest): String {
        val account = accountRepository.findById(targetAccountId)
            .orElseThrow { IllegalArgumentException("Account not found") }

        if (req.email != null && req.email != account.email) {
            if (accountRepository.existsByEmail(req.email)) {
                throw IllegalArgumentException("Email already in use")
            }
        }

        userMapper.updateAccount(req, account)
        accountRepository.save(account)

        val profile = userProfileRepository.findById(targetAccountId)
            .orElseThrow { IllegalArgumentException("Profile not found") }

        userMapper.updateProfile(req, profile)
        userProfileRepository.save(profile)

        return "Account details updated for ${account.email}"
    }

    // --- GET ALL USER DETAILS ---
    @Transactional(readOnly = true)
    fun getAllUserDetails(): List<UserResponse> {
        val accounts = accountRepository.findAll()

        return accounts.mapNotNull { account ->
            val profile = userProfileRepository.findById(account.accountId).orElse(null)

            if (profile != null) {
                userMapper.toUserResponse(account, profile)
            } else {
                null
            }
        }
    }
}