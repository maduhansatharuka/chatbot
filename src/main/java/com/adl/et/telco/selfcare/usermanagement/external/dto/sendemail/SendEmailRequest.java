/*
 *
 *  Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 *  All Rights Reserved.
 *
 *  These material are unpublished, proprietary, confidential source
 *  code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 *  SECRET of ADL.
 *
 *  ADL retains all title to and intellectual property rights in these
 *  materials.
 *
 */

package com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail;

import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.external.dto.RequestHeader;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the EnterpriseUserRegistration Request class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest implements RequestEntityInterface {
    @Valid
    @NotNull(message = "Request Body must not be null")
    private SendEmailRequestBody requestBody;

    @Valid
    @NotNull(message = "requestHeader must not be null")
    private RequestHeader requestHeader;
}