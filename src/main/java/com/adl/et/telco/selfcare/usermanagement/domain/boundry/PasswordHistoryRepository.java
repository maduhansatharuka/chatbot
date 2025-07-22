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

import com.adl.et.telco.selfcare.usermanagement.domain.entities.PasswordHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is the repository interface for PasswordHistory entity.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/11
 */
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistoryEntity, Long> {
    List<PasswordHistoryEntity> findByUserIdOrderByCreatedDateDesc(String userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_history WHERE id = (SELECT id FROM (SELECT MIN(id) as id FROM password_history WHERE userId = :userId) AS temp)", nativeQuery = true)
    void deleteOldestPassword(@Param("userId") String userId);

}