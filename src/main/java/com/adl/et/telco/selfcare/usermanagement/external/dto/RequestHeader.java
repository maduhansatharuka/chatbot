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
package com.adl.et.telco.selfcare.usermanagement.external.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the requestHeader class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHeader {
    @NotNull(message = "requestId can't be null or empty")
    private String requestId;

    @NotNull(message = "timestamp can't be null or empty")
    private String timestamp;

    @NotNull(message = "channel can't be null or empty")
    private String channel;
}