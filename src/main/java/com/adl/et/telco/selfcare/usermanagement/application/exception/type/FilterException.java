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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This is the Filter exception class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FilterException extends BaseException {

    public FilterException(String message) {
        super(message);
    }

    public FilterException(String message, String code) {
        super(message, code);
    }

}
