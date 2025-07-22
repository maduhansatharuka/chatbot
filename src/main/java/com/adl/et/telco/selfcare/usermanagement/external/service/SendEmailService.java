/*
 *
 *  Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 *  All Rights Reserved.
 *
 *  These material are unpublished, proprietary, confidential source
 *  code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 *  SECRET of ADL.
 *
 *  ADL retains all title to and intellectual property rights in these
 *  materials.
 *
 */

package com.adl.et.telco.selfcare.usermanagement.external.service;

import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.EXTERNAL_API_CALL_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logInfo;

/**
 * This service class is used to implement logic related to Forget Password
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/26
 */
@Service
public class SendEmailService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${send.email.url}")
    private String sendEmailURL;

    @Async("asyncExecutor")
    @Retryable(value = {ResourceAccessException.class, RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<ResponseEntity<String>> sendEmail(SendEmailRequest sendEmailRequest) throws DomainException {
        try {
            HttpEntity<SendEmailRequest> requestEntity = new HttpEntity<>(sendEmailRequest);

            ResponseEntity<String> response = restTemplate.exchange(sendEmailURL, HttpMethod.POST, requestEntity, String.class);
            logInfo(response.getBody());
            return CompletableFuture.completedFuture(ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(EXTERNAL_API_CALL_ERROR.getDescription(), EXTERNAL_API_CALL_ERROR.getCode());
        }
    }
}