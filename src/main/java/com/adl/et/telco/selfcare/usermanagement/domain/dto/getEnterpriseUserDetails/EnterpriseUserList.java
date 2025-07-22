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
package com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUserIdTypes;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the EnterpriseUserList Customer Object
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@Data
@NoArgsConstructor
public class EnterpriseUserList {
    private Long userId;
    private String firstName;
    private String lastName;
    private EnterpriseUserIdTypes idType;
    private String idNumber;
    private String mobileNumber;
    private String email;
    private String userName;
    private UserStatus status;
    private String accountId;

    public EnterpriseUserList(Long userId, String firstName, String lastName, EnterpriseUserIdTypes idType,
                              String idNumber, String mobileNumber, String email, String userName, UserStatus status, String accountId) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idType = idType;
        this.idNumber = idNumber;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.userName = userName;
        this.status = status;
        this.accountId = accountId;
    }
}