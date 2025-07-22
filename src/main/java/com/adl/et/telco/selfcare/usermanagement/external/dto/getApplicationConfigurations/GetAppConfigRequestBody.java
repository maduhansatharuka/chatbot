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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is the request Body class for Get Application Configurations.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/01/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAppConfigRequestBody {
    private List<String> category;
}