package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.SchoolProfile

interface ProfileService<T : SchoolProfile> {
    val roleName: String

    fun load(accountId: String): T?
}