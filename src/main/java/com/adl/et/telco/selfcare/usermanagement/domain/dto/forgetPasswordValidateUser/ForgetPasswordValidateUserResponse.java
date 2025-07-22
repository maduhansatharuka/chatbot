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
package com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser;

import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the Class for ForgetPasswordValidateUserResponse
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordValidateUserResponse {
    private ForgetPasswordValidateUserResponseBody responseBody;
    private ResponseHeader responseHeader;
}