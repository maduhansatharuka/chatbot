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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * This is the configuration file for Request Uuid Interceptor.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Component
public class RequestUuidInterceptor implements HandlerInterceptor {

    @Autowired
    private YAMLConfig yamlConfig;

    /**
     * Intercepts incoming HTTP requests and adds a log identifier to MDC.
     *
     * @param request  The incoming HTTP request.
     * @param response The outgoing HTTP response.
     * @param handler  The handler that will process the request.
     * @return true to continue the request handling process.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String logIdentifier = request.getHeader(yamlConfig.getLogIdentifierKey());
        if (logIdentifier != null) {
            MDC.put(yamlConfig.getLogIdentifierKey(), logIdentifier);
        } else {
            MDC.put(yamlConfig.getLogIdentifierKey(), UUID.randomUUID().toString());
        }
        return true;
    }

}
