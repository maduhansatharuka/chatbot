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
package com.adl.et.telco.selfcare.usermanagement.application.exception;

/**
 * Constructs a new InvalidFieldException with no message or cause.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.10.22
 */
public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException() {
    }

    /**
     * Constructs a new InvalidFieldException with a specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidFieldException with a specified message.
     *
     * @param message the detail message
     */
    public InvalidFieldException(String message) {
        super(message);
    }
}