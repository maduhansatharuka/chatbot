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
package com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the Get Enterprise User Details Request class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEnterpriseUserDetailsRequest implements RequestEntityInterface {
    @Valid
    @NotNull(message = "request body must not be null")
    private GetEnterpriseUserDetailsRequestBody requestBody;

    @Valid
    @NotNull(message = "request header must not be null")
    private RequestHeader requestHeader;
}