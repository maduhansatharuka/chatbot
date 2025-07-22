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
package com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the EnterpriseUserValidation Request Body class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseUserValidationRequestBody {
    @NotNull(message = "User Name cannot be null")
    @Size(min = 3, max = 20, message = "UserName must be between 3 and 20 characters")
    private String userName;

    @NotNull(message = "Password cannot be null")
//    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String password;
}