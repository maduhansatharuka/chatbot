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
package com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUserIdTypes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the EnterpriseUserRegistration Request Body class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseUserRegistrationRequestBody {
    @NotNull(message = "First name cannot be null")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotNull(message = "ID Type cannot be null")
    private EnterpriseUserIdTypes idType;

    @NotNull(message = "ID Number cannot be null")
    @Size(min = 5, max = 30, message = "ID Number must be between 5 and 30 characters")
    private String idNumber;

    @NotNull(message = "Mobile Number cannot be null")
    @Size(min = 7, max = 15, message = "Mobile Number must be between 7 and 15 characters")
    private String mobileNumber;

    @NotNull(message = "Email cannot be null")
    @Size(min = 6, max = 100, message = "Email must be between 6 and 100 characters")
    private String email;

    @NotNull(message = "User Name cannot be null")
    @Size(min = 3, max = 20, message = "UserName must be between 3 and 20 characters")
    private String userName;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 150, message = "Password must be between 8 and 15 characters")
    private String password;

    @NotNull(message = "Confirmed password cannot be null")
    @Size(min = 8, max = 150, message = "Confirmed password must be between 8 and 15 characters")
    private String confirmedPassword;

    @NotNull(message = "Account Id cannot be null")
    @Size(min = 5, max = 30, message = "Account ID must be between 5 and 30 characters")
    private String accountId;

    @Size(max = 100, message = "Special Note must be a maximum of 100 characters")
    private String specialNote;
}