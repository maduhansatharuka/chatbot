/*
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
package com.adl.et.telco.selfcare.usermanagement.application.config;

import org.springframework.context.annotation.Bean;

/**
 * This is the configuration file for REST API calls.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/17
 */
public class RequestContextListener {
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
