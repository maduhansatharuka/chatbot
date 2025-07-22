/**
 * Copyrights 2023 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUserIdTypes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the UserDetails class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.02.25
 */
@Data
@NoArgsConstructor
public class UserDetails {

    private Long userId;
    private String firstName;
    private String lastName;
    private String idType;
    private String idNumber;
    private String mobileNumber;
    private String email;
    private String userName;
    private String accountId;
    private String createdDate;
    private String createdBy;
    private String lastUpdatedDate;
    private String updatedBy;
    private String notes;

    public UserDetails(Long userId, String firstName, String lastName, EnterpriseUserIdTypes idType, String idNumber, String mobileNumber, String email, String userName, String accountId, LocalDateTime createdDate, String createdBy, LocalDateTime lastUpdatedDate, String updatedBy, String notes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idType = idType.name();
        this.idNumber = idNumber;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.userName = userName;
        this.accountId = accountId;
        this.createdDate = createdDate !=null ? createdDate.format(formatter) :null;
        this.createdBy = createdBy;
        this.lastUpdatedDate = lastUpdatedDate != null ? lastUpdatedDate.format(formatter) : null;
        this.updatedBy = updatedBy;
        this.notes = notes;
    }
}
