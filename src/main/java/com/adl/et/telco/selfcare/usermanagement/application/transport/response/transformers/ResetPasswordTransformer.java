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
package com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers;

import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.resetPassword.ResetPasswordResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the Class for ForgetPasswordValidateUserTransformer class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/11
 */
@Component
public class ResetPasswordTransformer implements ResponseEntityInterface {
    public Map transform(Object entity) {
        ResetPasswordResponse response = (ResetPasswordResponse) entity;
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("responseBody", response.getResponseBody());
        mapping.put("responseHeader", response.getResponseHeader());

        return mapping;
    }
}