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
package com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the PlaceHolderList class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceholderList {
    @NotNull(message = "Key cannot be null")
    @Size(min = 1, max = 20, message = "Key must be between 1 and 20 characters")
    private String key;

    @NotNull(message = "Value cannot be null")
    @Size(min = 1, max = 600, message = "Value must be between 1 and 600 characters")
    private String value;
}