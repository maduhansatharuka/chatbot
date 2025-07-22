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

import com.adl.et.telco.selfcare.usermanagement.domain.entities.SessionTokenEntity;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * This is the repository interface for SessionToken entity.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/04
 */
@Repository
public interface SessionTokenRepository extends JpaRepository<SessionTokenEntity, Long> {

    @Query("SELECT ste FROM SessionTokenEntity ste " +
            "WHERE (ste.userId = :userId) " +
            "AND (ste.sessionToken = :sessionToken) " +
            "AND (ste.status = :status) " +
            "AND (ste.expiryTime >= :currentDate)")
    SessionTokenEntity validateSessionToken(
            @Param("userId") String userId,
            @Param("sessionToken") String sessionToken,
            @Param("status") TokenStatus status,
            @Param("currentDate") LocalDateTime currentDate
    );
}