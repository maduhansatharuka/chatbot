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
package com.adl.et.telco.selfcare.usermanagement.external.exception;

import com.adl.et.telco.selfcare.usermanagement.application.exception.type.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This is the exception class for handle errors occured while
 * calling external apis
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/23
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WebClientException extends BaseException {

    public WebClientException(String message) {
        super(message);
    }

    public WebClientException(String message, String code) {
        super(message, code);
    }

}
