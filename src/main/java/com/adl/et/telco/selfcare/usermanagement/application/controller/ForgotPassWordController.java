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

import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.resetPassword.ResetPasswordRequest;
import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.ForgetPasswordGenerateEmailTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.ForgetPasswordValidateUserTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.ResetPasswordTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityValidator;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.forgetPasswordValidateUser.ForgetPasswordValidateUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.resetPassword.ResetPasswordResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.ForgetPasswordService;
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
 * This controller class is used to manage actions related to forgot password
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/26
 */
@RestController
@RequestMapping("${base-url.context}/user-management/enterprise/user")
public class ForgotPassWordController extends BaseController {
    @Autowired
    private RequestEntityValidator validator;
    @Autowired
    private ResponseEntityTransformer responseEntityTransformer;
    @Autowired
    private ForgetPasswordGenerateEmailTransformer forgetPasswordGenerateEmailTransformer;
    @Autowired
    private ForgetPasswordValidateUserTransformer forgetPasswordValidateUserTransformer;
    @Autowired
    private ResetPasswordTransformer resetPasswordTransformer;
    @Autowired
    private ForgetPasswordService forgetPasswordService;

    /**
     * Controller method for Forget Password Generate Email
     *
     * @param forgetPasswordGenerateEmailRequest The request containing the criteria for the forgot password
     * @param request                            The HTTP request.
     * @return ResponseEntity containing the response message.
     * @throws Exception If there is an error while processing the request.
     */
    @PostMapping(value = "/forget/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> forgetPasswordGenerateEmail(@Valid @RequestBody ForgetPasswordGenerateEmailRequest forgetPasswordGenerateEmailRequest, HttpServletRequest request) throws DomainException {

        validator.validate(forgetPasswordGenerateEmailRequest);

        ForgetPasswordGenerateEmailResponse forgetPasswordGenerateEmailResponse = forgetPasswordService.forgetPasswordGenerateEmail(forgetPasswordGenerateEmailRequest);

        Map<String, Object> trResponse = responseEntityTransformer.transform(forgetPasswordGenerateEmailResponse, forgetPasswordGenerateEmailTransformer);

        return getResponseEntity(forgetPasswordGenerateEmailResponse.getResponseHeader().getResponseCode(), trResponse);
    }

    /**
     * Controller method for Forget Password User validation before Reset Password
     *
     * @param forgetPasswordValidateUserRequest The request containing the criteria for the forgot password User Validation
     * @param request                           The HTTP request.
     * @return ResponseEntity containing the response message.
     * @throws Exception If there is an error while processing the request.
     */
    @PostMapping(value = "/validate/token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> forgetPasswordValidateUser(@Valid @RequestBody ForgetPasswordValidateUserRequest forgetPasswordValidateUserRequest, HttpServletRequest request) throws DomainException {

        validator.validate(forgetPasswordValidateUserRequest);

        ForgetPasswordValidateUserResponse forgetPasswordValidateUserResponse = forgetPasswordService.forgetPasswordUserValidation(forgetPasswordValidateUserRequest);

        Map<String, Object> trResponse = responseEntityTransformer.transform(forgetPasswordValidateUserResponse, forgetPasswordValidateUserTransformer);

        return getResponseEntity(forgetPasswordValidateUserResponse.getResponseHeader().getResponseCode(), trResponse);
    }

    /**
     * Controller method for Forget Password User validation before Reset Password
     *
     * @param resetPasswordRequest The request containing the criteria for the reset Password
     * @param request              The HTTP request.
     * @return ResponseEntity containing the response message.
     */
    @PostMapping(value = "/reset/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) throws DomainException {

        validator.validate(resetPasswordRequest);

        ResetPasswordResponse resetPasswordResponse = forgetPasswordService.resetPassword(resetPasswordRequest);

        Map<String, Object> trResponse = responseEntityTransformer.transform(resetPasswordResponse, resetPasswordTransformer);

        return getResponseEntity(resetPasswordResponse.getResponseHeader().getResponseCode(), trResponse);
    }
}