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

import com.adl.et.telco.dte.plugin.alarming.dto.AlarmDef;
import com.adl.et.telco.selfcare.usermanagement.application.alarm.AlarmGen;
import com.adl.et.telco.selfcare.usermanagement.application.exception.type.BaseException;
import com.adl.et.telco.selfcare.usermanagement.application.exception.type.ValidationException;
import com.adl.et.telco.selfcare.usermanagement.application.util.Constant;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.exception.WebClientException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is the exception class for custom exceptions.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.10.22
 */
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    private final AlarmGen alarm;
    private String sampleRequestId = "1676541979935";

    public CustomErrorAttributes(AlarmGen alarm) {
        this.alarm = alarm;
    }

    /**
     * Returns custom error attributes for exceptions based on their type.
     *
     * @param webRequest the current web request
     * @param options    options to customize the error attributes
     * @return a map containing the error details
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Throwable error = getError(webRequest);
        if (error == null) {
            return Collections.emptyMap();
        }
        switch (error.getClass().getSimpleName()) {
            case "ValidationException":
                return handleValidationException((ValidationException) error);
            case "MethodArgumentNotValidException":
                return handleMethodArgumentNotValidException((MethodArgumentNotValidException) error);
            case "ControllerException", "FilterException", "DomainException":
                return handleDomainException((DomainException) error);
            case "WebClientException":
                return handleRecoverableException((BaseException) error);
            case "HttpMessageNotReadableException":
                return handleHttpMessageNotReadableException((HttpMessageConversionException) error);
            case "HandlerMethodValidationException":
                return handleMethodValidationException((HandlerMethodValidationException) error);
            default:
                return handleGenericException(error);
        }
    }

    /**
     * Handle Domain Exceptions and send response accordingly
     *
     * @param error exception
     * @return error description
     */
    private Map<String, Object> handleDomainException(DomainException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();

        responseHeader.put(Constant.REQ_ID, error.getRequestId() != null ? error.getRequestId() : sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, error.getCode() != null ? error.getCode() : Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, error.getMessage());

        errorResponse.put(Constant.RES_HEADER, responseHeader);

        return errorResponse;
    }


    /**
     * Handle HTTP message not readable exceptions
     *
     * @param error HttpMessageConversionException
     * @return error description
     */
    private Map<String, Object> handleHttpMessageNotReadableException(HttpMessageConversionException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();

        String message = null;
        if (error.getRootCause() instanceof InvalidFieldException) {
            Throwable rootCause = error.getRootCause();
            String additionalData = null;
            if (rootCause != null) {
                additionalData = rootCause.getMessage();
            }
            message = Constant.VALIDATION_ERROR + additionalData;
        } else {
            message = error.getMessage();
        }

        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, message);
        errorResponse.put(Constant.RES_HEADER, responseHeader);
        alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());
        return errorResponse;
    }

    /**
     * Handle unrecoverable and more generic exceptions
     *
     * @param error exception
     * @return error description
     */
    private Map<String, Object> handleGenericException(Throwable error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();
        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, Constant.SYSTEM_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, error.getMessage());
        errorResponse.put(Constant.RES_HEADER, responseHeader);

        if (error.getCause() instanceof WebClientException) {
            alarm.alert(AlarmDef.MessageType.API, error.getMessage());
        } else {
            alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());
        }
        return errorResponse;
    }

    /**
     * Handle recoverable exceptions
     *
     * @param error exception
     * @return error description
     */
    private Map<String, Object> handleRecoverableException(BaseException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();

        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, error.getCode() != null ? error.getCode() : Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, error.getMessage());
        errorResponse.put(Constant.RES_HEADER, responseHeader);

        alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());
        return errorResponse;
    }

    /**
     * Handle handleMethodArgumentNotValidException
     *
     * @param error exception
     * @return error description
     */
    private Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();
        String message = null;
        if (!CollectionUtils.isEmpty(error.getBindingResult().getAllErrors())) {

            message = error.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, message != null ? message : error.getMessage());
        errorResponse.put(Constant.RES_HEADER, responseHeader);
        alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());

        return errorResponse;
    }

    /**
     * Handles method validation exceptions and constructs a structured error response.
     *
     * @param error The HandlerMethodValidationException containing validation error details.
     * @return A map containing the error response with response headers and validation error messages.
     */
    private Map<String, Object> handleMethodValidationException(HandlerMethodValidationException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();
        String message = null;
        if (error.getAllErrors() != null && !error.getAllErrors().isEmpty()) {
            message = error.getAllErrors().stream()
                    .map(violation ->violation.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }

        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, message != null ? message : error.getMessage());
        errorResponse.put(Constant.RES_HEADER, responseHeader);
        alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());

        return errorResponse;
    }

    /**
     * Handle validation exceptions
     *
     * @param error exception
     * @return error description
     */
    private Map<String, Object> handleValidationException(ValidationException error) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> responseHeader = new LinkedHashMap<>();
        Optional<ConstraintViolation<RequestEntityInterface>> firstError = error.getErrors().stream().findFirst();
        String message = firstError.isPresent() ? firstError.get().getMessage() : "validation fail";

        responseHeader.put(Constant.REQ_ID, sampleRequestId);
        responseHeader.put(Constant.TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        responseHeader.put(Constant.RES_CODE, Constant.VALIDATION_ERROR_TEXT);
        responseHeader.put(Constant.RES_DESC, message);
        errorResponse.put(Constant.RES_HEADER, responseHeader);
        alarm.alert(AlarmDef.MessageType.FUNCTIONAL, error.getMessage());
        return errorResponse;
    }
}