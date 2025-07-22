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
package com.adl.et.telco.selfcare.usermanagement.application.controller;

import com.adl.et.telco.selfcare.usermanagement.application.dto.userRegistration.EnterpriseUserRegistrationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.EnterpriseUserRegistrationTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityValidator;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userRegistration.EnterpriseUserRegistrationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.EnterpriseUserRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * This controller class is used to manage actions related to enterprise user registration
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@RestController
@RequestMapping("${base-url.context}/user-management")
public class UserRegistrationController extends BaseController {
    @Autowired
    private RequestEntityValidator validator;
    @Autowired
    private ResponseEntityTransformer responseEntityTransformer;
    @Autowired
    private EnterpriseUserRegistrationTransformer enterpriseUserRegistrationTransformer;
    @Autowired
    private EnterpriseUserRegistrationService enterpriseUserRegistrationService;

    /**
     * Controller method for Registering a New Enterprise User
     *
     * @param enterpriseUserRegistrationRequest The request containing the criteria for the enterprise user registration.
     * @param request                           The HTTP request.
     * @return ResponseEntity containing the response message.
     * @throws Exception If there is an error while processing the request.
     */
    @PostMapping(value = "/enterprise/user/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody EnterpriseUserRegistrationRequest enterpriseUserRegistrationRequest, HttpServletRequest request) throws DomainException {

        validator.validate(enterpriseUserRegistrationRequest);

        EnterpriseUserRegistrationResponse enterpriseUserRegistrationResponse = enterpriseUserRegistrationService.registerUser(enterpriseUserRegistrationRequest);

        if (enterpriseUserRegistrationResponse == null) {
            throw new DomainException("Failed to register user. Response is null.");
        }

        Map<String, Object> trResponse = responseEntityTransformer.transform(enterpriseUserRegistrationResponse, enterpriseUserRegistrationTransformer);

        return getResponseEntity(enterpriseUserRegistrationResponse.getResponseHeader().getResponseCode(), trResponse);
    }
}