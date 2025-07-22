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
package com.adl.et.telco.selfcare.usermanagement.application.dto.resetPassword;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the ResetPassword Request Body class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestBody {
    @NotNull(message = "Session Token cannot be null")
    private String sessionToken;

    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Password cannot be null")
    private String password;
}