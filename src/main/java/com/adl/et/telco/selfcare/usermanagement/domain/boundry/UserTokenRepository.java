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

import com.adl.et.telco.selfcare.usermanagement.domain.entities.TokenStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This is the repository interface for UserToken entity.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/04
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Long> {

    /**
     * Validate the Token by userId, email and uniqueId
     *
     * @param email
     * @param userId
     * @return
     */
    @Query("SELECT ute FROM UserTokenEntity ute " +
            "WHERE (ute.userId = :userId) " +
            "AND (ute.email = :email) " +
            "AND (ute.uniqueId = :uniqueId) " +
            "AND (ute.status = :status)")
    UserTokenEntity validateTheUserToken(
            @Param("userId") String userId,
            @Param("email") String email,
            @Param("uniqueId") String uniqueId,
            @Param("status") TokenStatus status);

}