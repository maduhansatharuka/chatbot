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
package com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the ValidatedUserDetails Customer Object
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@Data
@NoArgsConstructor
public class ValidatedUserDetails {
    private Long userId;
    private String email;
    private String firstName;
    private String mobileNumber;
    private String userName;
    private String password;

    public ValidatedUserDetails(Long userId, String email, String firstName, String mobileNumber, String userName, String password) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.mobileNumber = mobileNumber;
        this.userName = userName;
        this.password = password;
    }
}