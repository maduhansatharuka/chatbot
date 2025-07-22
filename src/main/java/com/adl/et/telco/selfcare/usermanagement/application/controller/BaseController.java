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
package com.adl.et.telco.selfcare.usermanagement.application.controller;

import com.adl.et.telco.selfcare.usermanagement.application.config.YAMLConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * This is the base controller for User management Service.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.10.16
 */
public class BaseController {

    @Autowired
    private YAMLConfig yamlConfig;

    /**
     * Populate Response Header
     *
     * @param responseCode
     * @param trResponse
     * @return
     */
    public ResponseEntity<Map<String, Object>> getResponseEntity(String responseCode, Map<String, Object> trResponse) {
        switch (responseCode) {
            case "200":
                return ResponseEntity.status(HttpStatus.OK).body(trResponse);
            case "202":
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(trResponse);
            case "400":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(trResponse);
            case "401":
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(trResponse);
            case "404":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(trResponse);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(trResponse);
        }
    }
}
