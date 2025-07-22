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
package com.adl.et.telco.selfcare.usermanagement.application.validator;

import com.adl.et.telco.selfcare.usermanagement.application.exception.type.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * This is the request validator class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/17
 */
@Component
public class RequestEntityValidator {

    @Autowired
    private Validator validator;

    /**
     * This method is to Validate all the requests
     *
     * @param target
     */
    public void validate(RequestEntityInterface target) throws ValidationException {

        Set<ConstraintViolation<RequestEntityInterface>> errors = this.validator.validate(target);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}