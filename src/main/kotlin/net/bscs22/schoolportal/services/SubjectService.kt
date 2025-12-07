package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.Subject
import net.bscs22.schoolportal.repositories.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository
) {

    // 1. LIST ALL
    fun getAllSubjects(): List<Subject> {
        return subjectRepository.findAll()
    }

    // 2. CREATE (Now accepts individual fields)
    @Transactional
    fun createSubject(
        subjectCode: String,
        subjectName: String,
        lecUnits: Int,
        labUnits: Int
    ): Subject {
        if (subjectRepository.existsById(subjectCode)) {
            throw IllegalArgumentException("Subject code '$subjectCode' already exists.")
        }

        val newSubject = Subject(
            subjectCode = subjectCode,
            subjectName = subjectName,
            lecUnits = lecUnits.toLong(), // Fix: Convert Int to Long
            labUnits = labUnits.toLong()  // Fix: Convert Int to Long
        )
        return subjectRepository.save(newSubject)
    }

    // 3. UPDATE (Now accepts individual fields)
    @Transactional
    fun updateSubject(
        code: String,
        subjectName: String,
        lecUnits: Int,
        labUnits: Int
    ): Subject {
        val subject = subjectRepository.findById(code)
            .orElseThrow { IllegalArgumentException("Subject '$code' not found.") }

        // Update fields
        subject.subjectName = subjectName
        subject.lecUnits = lecUnits.toLong() // Fix: Convert Int to Long
        subject.labUnits = labUnits.toLong() // Fix: Convert Int to Long

        return subjectRepository.save(subject)
    }

    // 4. HARD DELETE
    @Transactional
    fun deleteSubject(code: String): String {
        if (!subjectRepository.existsById(code)) {
            throw IllegalArgumentException("Subject '$code' not found.")
        }

        subjectRepository.deleteById(code)

        return "Subject '$code' successfully deleted."
    }
}