/**
 * © Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 * <p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 * <p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 */
package com.adl.et.telco.selfcare.usermanagement.application.util;

import org.springframework.stereotype.Service;

/**
 * This class is used to log request details
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Service
public class LoggerUtilService {

    /**
     * log request details
     *
     * @param action
     * @param requestId
     * @param remoteHost
     * @param messages
     * @return
     */
    public String lognow(String action, String requestId, String remoteHost, String[] messages) {

        StringBuilder sb = new StringBuilder("Request Received. ");

        sb.append("End Point :").append(action).append(". Request ID :").append(requestId).append(". Client IP :").append(remoteHost);

        if (messages != null) {
            sb.append(". Message Info :");
            for (String msg : messages) {
                sb.append(msg).append(",");
            }
        }
        return sb.toString();

    }

    /**
     * log request details
     *
     * @param action
     * @param requestId
     * @param remoteHost
     * @param messages
     * @return
     */
    public String lognowWithRequestBody(String action, String requestId, String remoteHost, String[] messages, Object request) {

        StringBuilder sb = new StringBuilder("Request Received. ");

        sb.append("End Point :").append(action).append(". Request ID :").append(requestId).append(". Client IP :").append(remoteHost).append(". requestBody: ").append(request);

        if (messages != null) {
            sb.append(". Message Info :");
            for (String msg : messages) {
                sb.append(msg).append(",");
            }
        }
        return sb.toString();

    }
}
