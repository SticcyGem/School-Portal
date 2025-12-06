package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.keys.PendingEnrollmentKey
import net.bscs22.schoolportal.models.views.PendingEnrollmentDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PendingEnrollmentDetailRepository :
    JpaRepository<PendingEnrollmentDetail, PendingEnrollmentKey>