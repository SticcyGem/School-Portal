package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.entities.academics.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : JpaRepository<Subject, String>