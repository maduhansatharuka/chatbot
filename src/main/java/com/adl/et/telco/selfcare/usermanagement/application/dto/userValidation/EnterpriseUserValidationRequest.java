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

package com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the EnterpriseUserValidationRequest Request class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseUserValidationRequest implements RequestEntityInterface {

    @Valid
    @NotNull(message = "Request Body must not be null")
    private EnterpriseUserValidationRequestBody requestBody;

    @Valid
    @NotNull(message = "requestHeader must not be null")
    private RequestHeader requestHeader;
}