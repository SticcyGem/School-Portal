package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.college.CollegeRequest
import net.bscs22.schoolportal.entities.academics.College
import net.bscs22.schoolportal.repositories.CollegeRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CollegeService(
    private val collegeRepo: CollegeRepository
) {
    fun getAllColleges(query: String): List<College> {
        val sort = Sort.by("collegeCode")
        return if (query.isBlank()) collegeRepo.findAll(sort)
        else collegeRepo.searchColleges(query, sort)
    }

    @Transactional
    fun createCollege(req: CollegeRequest): College {
        if (collegeRepo.existsById(req.collegeCode)) {
            throw IllegalArgumentException("College code '${req.collegeCode}' already exists.")
        }
        return collegeRepo.save(College(req.collegeCode, req.collegeName))
    }

    @Transactional
    fun updateCollege(code: String, req: CollegeRequest): College {
        val college = collegeRepo.findById(code)
            .orElseThrow { IllegalArgumentException("College '$code' not found.") }

        college.collegeName = req.collegeName
        return collegeRepo.save(college)
    }

    @Transactional
    fun deleteCollege(code: String) {
        if (!collegeRepo.existsById(code)) throw IllegalArgumentException("College not found")
        collegeRepo.deleteById(code)
    }
}