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
package com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations;

import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the response class for Get Application Configurations.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/01/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAppConfigResponse {

    @Valid
    @NotNull(message = "response body must not be null")
    private GetAppConfigResponseBody responseBody;

    @Valid
    @NotNull(message = "response header must not be null")
    private ResponseHeader responseHeader;
}