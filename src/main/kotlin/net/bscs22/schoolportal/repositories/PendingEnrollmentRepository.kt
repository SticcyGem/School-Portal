package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.views.PendingEnrollmentDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// Note: Using Long for the ID type might require a custom query or composite key class
// since the view relies on a composite key (enrollmentNo + sectionNo).
// For rapid development, we use a simple Long ID type and rely on custom queries, or
// assume a composite key class (not shown) is defined.
// For now, let's treat the combination as unique for lookup.
@Repository
interface PendingEnrollmentRepository : JpaRepository<PendingEnrollmentDetail, Long> {

    // Query to retrieve all pending enrollments (since the view only contains DRAFT status)
    override fun findAll(): List<PendingEnrollmentDetail>
}