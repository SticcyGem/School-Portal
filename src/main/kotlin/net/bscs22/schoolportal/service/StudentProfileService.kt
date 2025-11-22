package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.model.StudentProfile
import net.bscs22.schoolportal.repository.StudentRepository
import org.springframework.stereotype.Service

@Service
class StudentProfileService(
    private val repository: StudentRepository
) : ProfileService<StudentProfile> {

    override val roleName: String = "student"

    override fun load(accountId: String): StudentProfile? {
        return repository.findByAccountId(accountId)
    }
}
