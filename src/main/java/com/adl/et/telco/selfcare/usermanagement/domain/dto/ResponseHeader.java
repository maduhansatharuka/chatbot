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
package com.adl.et.telco.selfcare.usermanagement.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the responseHeader class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseHeader {
    private String requestId;
    private String timestamp;
    private String code;
    private String desc;
    @JsonIgnore
    private String responseCode;

    public ResponseHeader(String requestId, String timestamp, String code, String desc) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.code = code;
        this.desc = desc;
    }


}

