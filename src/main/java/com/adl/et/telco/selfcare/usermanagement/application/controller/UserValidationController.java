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

import com.adl.et.telco.selfcare.usermanagement.application.dto.userValidation.EnterpriseUserValidationRequest;
import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.EnterpriseUserValidationTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityValidator;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.userValidation.EnterpriseUserValidationResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.EnterpriseUserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * This controller class is used to manage actions related to User Validation.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@RestController
@RequestMapping("${base-url.context}/user-management")
public class UserValidationController extends BaseController {

    @Autowired
    private RequestEntityValidator validator;
    @Autowired
    private ResponseEntityTransformer responseEntityTransformer;
    @Autowired
    private EnterpriseUserValidationTransformer enterpriseUserValidationTransformer;
    @Autowired
    private EnterpriseUserValidationService enterpriseUserValidationService;

    /**
     * This Controller method is used to validate a user based on the given username and the password
     *
     * @param enterpriseUserValidationRequest
     * @return
     * @throws DomainException
     */
    @PostMapping(value = "/validate/enterprise/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validateEnterpriseUser(@Validated @RequestBody(required = true) EnterpriseUserValidationRequest enterpriseUserValidationRequest) throws DomainException {

        validator.validate(enterpriseUserValidationRequest);

        EnterpriseUserValidationResponse response = enterpriseUserValidationService.validateUserByUserNameAndPassword(enterpriseUserValidationRequest);
        Map trResponse = responseEntityTransformer.transform(response, enterpriseUserValidationTransformer);
        return getResponseEntity(response.getResponseHeader().getResponseCode(), trResponse);
    }
}