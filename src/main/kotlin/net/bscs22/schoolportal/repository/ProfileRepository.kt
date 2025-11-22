package net.bscs22.schoolportal.repository

import net.bscs22.schoolportal.model.SchoolProfile

interface ProfileRepository<T : SchoolProfile> {
    fun findByAccountId(accountId: String): T?
}