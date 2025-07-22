/*
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

package com.adl.et.telco.selfcare.usermanagement.external.service;

import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.EXTERNAL_API_CALL_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;


/**
 * This is the ExternalAPIService for handling external api calls.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/29
 */
@Service
public class ExternalAPIService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Asynchronous method to call external APIs and return the response data.
     *
     * @param apiName    the name of the API being called
     * @param httpMethod the HTTP method type (e.g., GET, POST)
     * @param url        the URL of the external API
     * @param request    the request payload for the API
     * @return a CompletableFuture containing the API response values
     */
    @Async("asyncExecutor")
    public CompletableFuture<List> callExternalAPI(String apiName, HttpMethod httpMethod, String url, Object request) throws DomainException {

        List<Object> responseValues = new ArrayList<>();

        ResponseEntity<HashMap> responseEntity = callForAllExternalAPIS(httpMethod, url, request);
        if (responseEntity != null) {
            responseValues.add(responseEntity.getBody());
            responseValues.add(responseEntity.getStatusCode());
        }

        return CompletableFuture.completedFuture(responseValues);

    }
    public CompletableFuture<List> callExternalAPI(HttpMethod httpMethod, String url, Object request) throws DomainException {

        try {
            List<Object> responseValues = new ArrayList<>();

            ResponseEntity<HashMap> responseEntity = callForAllExternalAPIS(httpMethod, url, request);
            if (responseEntity != null) {
                responseValues.add(responseEntity.getBody());
                responseValues.add(responseEntity.getStatusCode());
            }

            return CompletableFuture.completedFuture(responseValues);
        }
        catch(Exception e){
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(EXTERNAL_API_CALL_ERROR.getDescription(), EXTERNAL_API_CALL_ERROR.getCode());
        }

    }

    /**
     * Calls an external API from the BSS Adaptor category and handles the response.
     *
     * @param httpMethod the HTTP method type (e.g., GET, POST)
     * @param url        the URL of the external API
     * @param request    the request payload for the API
     * @return a ResponseEntity containing the API response as a HashMap
     */
    public ResponseEntity<HashMap> callForAllExternalAPIS(HttpMethod httpMethod, String url, Object request) throws DomainException {

        ResponseEntity<HashMap> response;

        // Create header and set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> requestEntity = new HttpEntity<>(request);

        try {
            response = restTemplate.exchange(url, httpMethod, requestEntity, HashMap.class);
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(EXTERNAL_API_CALL_ERROR.getDescription(), EXTERNAL_API_CALL_ERROR.getCode());
        }
        return response;
    }
}