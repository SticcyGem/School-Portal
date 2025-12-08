package net.bscs22.schoolportal.configs.security

import net.bscs22.schoolportal.entities.accounts.Account
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

class AuthenticatedUser(val account: Account) : UserDetails {
    val id: UUID = account.accountId

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return account.roles.map { role ->
            val name = role.roleName
            if (name.startsWith("ROLE_")) SimpleGrantedAuthority(name)
            else SimpleGrantedAuthority("ROLE_$name")
        }
    }

    override fun getPassword(): String = account.passwordHash
    override fun getUsername(): String = account.email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}