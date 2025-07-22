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
package com.adl.et.telco.selfcare.usermanagement.domain.exception;

import com.adl.et.telco.selfcare.usermanagement.application.exception.type.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception class for handling domain-specific errors.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DomainException extends BaseException {
    private String requestId;

    /**
     * Constructs a new DomainException with the specified detail message.
     *
     * @param message the detail message
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * Constructs a new DomainException with the specified detail message and error code.
     *
     * @param message the detail message
     * @param code    the error code associated with the exception
     */
    public DomainException(String message, String code) {
        super(message, code);
    }

    /**
     * Retrieves the request ID associated with this exception.
     *
     * @return the request ID, or null if not set
     */
    public String getRequestId() {
        return requestId;
    }
}
