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

import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.UserDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation.EnterpriseUserValidationResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * This is the repository interface for EnterpriseUsers entity.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/18
 */
@Repository
public interface EnterpriseUsersRepository extends JpaRepository<EnterpriseUsers, Long> {

    /**
     * Counts users with a specified username, email, or mobile number that are either in 'PENDING' or 'APPROVED' status.
     *
     * @param userName
     * @param email
     * @param mobileNumber
     * @return count of matching users
     */
    @Query("SELECT " +
            "SUM(CASE WHEN u.userName = :userName AND u.status IN ('PENDING', 'APPROVED') THEN 1 ELSE 0 END) as userNameCount, " +
            "SUM(CASE WHEN u.email = :email AND u.status IN ('PENDING', 'APPROVED') THEN 1 ELSE 0 END) as emailCount, " +
            "SUM(CASE WHEN u.mobileNumber = :mobileNumber AND u.status IN ('PENDING', 'APPROVED') THEN 1 ELSE 0 END) as mobileNumberCount " +
            "FROM EnterpriseUsers u")
    UniqueCountResult countByFields(String userName, String email, String mobileNumber);

    /**
     * Validates an enterprise user by their username, ensuring the user is in the specified status,
     * their password has not expired, and the record is still active.
     *
     * @param userName    the username to validate
     * @param status      the required status of the user
     * @param currentDate the current date used to check for password and record expiration
     * @return a response body containing validated user details if the user is valid; null otherwise
     */
    @Query("SELECT eu FROM EnterpriseUsers eu " +
            "WHERE (eu.userName = :userName) " +
            "AND (eu.status = :status) " +
            "AND (eu.passwordExpiryDate > :currentDate OR eu.passwordExpiryDate IS NULL) " +
            "AND (eu.recordExpiryDate > :currentDate OR eu.recordExpiryDate IS NULL)")
    EnterpriseUsers validateEnterpriseUserByGivenUserName(
            @Param("userName") String userName,
            @Param("status") UserStatus status,
            @Param("currentDate") LocalDateTime currentDate);

    /**
     * Retrieves detailed information about an enterprise user based on optional search parameters.
     *
     * @param userId       the ID of the user to retrieve (optional)
     * @param mobileNumber the mobile number of the user to retrieve (optional)
     * @param userName     the username of the user to retrieve (optional)
     * @param email        the email address of the user to retrieve (optional)
     * @param status       the status of the user to retrieve (optional)
     * @param currentDate  the current date used to check for record expiration
     * @return a response body containing the user's details if found; null otherwise
     */
    @Query("SELECT new com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponseBody" +
            "(eu.userId, eu.firstName, eu.lastName, eu.idType, " +
            "eu.idNumber, eu.mobileNumber, eu.email, eu.userName, eu.status, eu.accountId) " +
            "FROM EnterpriseUsers eu " +
            "WHERE (:userId IS NULL OR eu.userId = :userId) " +
            "AND (:mobileNumber IS NULL OR eu.mobileNumber = :mobileNumber) " +
            "AND (:userName IS NULL OR eu.userName = :userName) " +
            "AND (:email IS NULL OR eu.email = :email) " +
            "AND (:status IS NULL OR eu.status = :status) " +
            "AND (eu.recordExpiryDate > :currentDate OR eu.recordExpiryDate IS NULL)")
    GetEnterpriseUserDetailsResponseBody getEnterpriseUserDetails(
            @Param("userId") Long userId,
            @Param("mobileNumber") String mobileNumber,
            @Param("userName") String userName,
            @Param("email") String email,
            @Param("status") UserStatus status,
            @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT new com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.UserDetails(" +
            "eu.userId, eu.firstName, eu.lastName, eu.idType, eu.idNumber, eu.mobileNumber, eu.email, eu.userName, eu.accountId, eu.createdDate, eu.createdBy, eu.lastUpdatedDate, eu.updatedBy, eu.remarks) " +
            "FROM EnterpriseUsers eu " +
            "WHERE (:email IS NULL OR eu.email LIKE CONCAT('%', :email, '%')) " +
            "AND (:userName IS NULL OR eu.userName LIKE CONCAT('%', :userName, '%')) " +
            "AND (:status IS NULL OR eu.status = :status)" +
            "AND (eu.recordExpiryDate > :currentDate OR eu.recordExpiryDate IS NULL)")
    Page<UserDetails> getEnterpriseUserList(
            @Param("email") String email,
            @Param("userName") String userName,
            @Param("status") UserStatus status,
            @Param("currentDate") LocalDateTime currentDate,
            Pageable pageable);

    /**
     * Find the Enterprise user by the given email
     * @param email
     * @param currentDate
     * @return
     */
    @Query("SELECT eu FROM EnterpriseUsers eu " +
            "WHERE (eu.email = :email) " +
            "AND (eu.recordExpiryDate > :currentDate OR eu.recordExpiryDate IS NULL)")
    EnterpriseUsers findUserByTheEmail(
            @Param("email") String email,
            @Param("currentDate") LocalDateTime currentDate);

    /**
     * Find the Enterprise user by the userId
     * @param userId
     * @return
     */
    @Query("SELECT eu FROM EnterpriseUsers eu " +
            "WHERE (eu.userId = :userId)")
    EnterpriseUsers findUserByUserId(
            @Param("userId") String userId);

    @Query("SELECT e FROM EnterpriseUsers e WHERE e.userId = :userId AND e.status = :status AND (e.recordExpiryDate > :currentDate OR e.recordExpiryDate IS NULL)")
    EnterpriseUsers findByUserIdAndStatus(@Param("userId") Long userId,
                                                 @Param("status") UserStatus status,
                                                 @Param("currentDate") LocalDateTime currentDate);

    interface UniqueCountResult {
        Long getUserNameCount();

        Long getEmailCount();

        Long getMobileNumberCount();
    }
}