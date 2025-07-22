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
package com.adl.et.telco.selfcare.usermanagement.application.exception.type;

import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

/**
 * This is the validation exception class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    private final Set<ConstraintViolation<RequestEntityInterface>> errors;

    public ValidationException(Set<ConstraintViolation<RequestEntityInterface>> errors) {
        this.errors = errors;
    }

    public Set<ConstraintViolation<RequestEntityInterface>> getErrors() {
        return this.errors;
    }

}