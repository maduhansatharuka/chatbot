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
package com.adl.et.telco.selfcare.usermanagement.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is the configuration file for MVC HTTP Request.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Configuration
public class HttpRequestMvcConfig implements WebMvcConfigurer {

    private final RequestUuidInterceptor requestUUIDInterceptor;

    /**
     * Constructor to initialize the MVC configuration with a UUID interceptor.
     *
     * @param requestUUIDInterceptor The interceptor that adds UUIDs to requests.
     */
    public HttpRequestMvcConfig(RequestUuidInterceptor requestUUIDInterceptor) {
        this.requestUUIDInterceptor = requestUUIDInterceptor;
    }

    /**
     * Registers the UUID interceptor to the application's interceptor registry.
     *
     * @param registry The interceptor registry to which the interceptor is added.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestUUIDInterceptor);
    }

}
