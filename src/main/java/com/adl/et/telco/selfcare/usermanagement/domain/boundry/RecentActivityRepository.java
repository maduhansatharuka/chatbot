/**
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.domain.boundry;


import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.RecentActivityDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.RecentActivity.RecentActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * This is the RecentActivityRepository interface.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Repository
public interface RecentActivityRepository extends JpaRepository<RecentActivityEntity,Long> {
    @Query("SELECT new com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.RecentActivityDetails(" +
            "ra.activityId, ra.activity, ra.userName, ra.status, ra.details, ra.createdDate) " +
            "FROM RecentActivityEntity ra " +
            "WHERE ra.accountId = :accountId AND (ra.recordExpiryDate > :currentDate OR ra.recordExpiryDate IS NULL)")
    Page<RecentActivityDetails> getRecentActivityList(
            @Param("currentDate") LocalDateTime currentDate,
            @Param("accountId") String accountId,
            Pageable pageable);
}
