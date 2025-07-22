/**
 * Copyrights 2023 Axiata Digital Labs Pvt Ltd.
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

import com.adl.et.telco.selfcare.usermanagement.application.dto.approveEnterpriseUser.ApproveEnterpriseUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserList.GetEnterpriseUserListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.ApproveEnterpriseUserTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.GetEnterpriseUserDetailsTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.GetEnterpriseUserListTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityValidator;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser.ApproveEnterpriseUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.GetEnterpriseUserListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.EnterpriseUserManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This is the Enterprise User Manager Controller class.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@RestController
@RequestMapping("${base-url.context}/user-management")
public class EnterpriseUserManagerController extends BaseController {
    @Autowired
    private RequestEntityValidator validator;
    @Autowired
    private EnterpriseUserManagerService enterpriseUserManagerService;
    @Autowired
    private ResponseEntityTransformer responseEntityTransformer;
    @Autowired
    private GetEnterpriseUserDetailsTransformer getEnterpriseUserDetailsTransformer;
    @Autowired
    private GetEnterpriseUserListTransformer getEnterpriseUserListTransformer;
    @Autowired
    private ApproveEnterpriseUserTransformer approveEnterpriseUserTransformer;


    /**
     * Get Enterprise User Details Controller
     *
     * @param getEnterpriseUserDetailsRequest
     * @return
     * @throws DomainException
     */
    @PostMapping(value = "/get/enterprise-user/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEnterpriseUserDetails(@Validated @RequestBody(required = true) GetEnterpriseUserDetailsRequest getEnterpriseUserDetailsRequest) throws DomainException {

        validator.validate(getEnterpriseUserDetailsRequest);

        GetEnterpriseUserDetailsResponse response = enterpriseUserManagerService.getEnterpriseUserDetails(getEnterpriseUserDetailsRequest);
        Map trResponse = responseEntityTransformer.transform(response, getEnterpriseUserDetailsTransformer);
        return getResponseEntity(response.getResponseHeader().getResponseCode(), trResponse);
    }

    /**
     * Get Enterprise User List Controller
     *
     * @param getEnterpriseUserListRequest The request object containing filters for retrieving the enterprise user list.
     * @param pageSize The number of records per page (default is 10).
     * @param pageNo The page number to retrieve (default is 1).
     * @param sortBy The field by which the results should be sorted (default is "id").
     * @param sortDir The sorting direction, either "asc" or "desc" (default is "asc").
     * @return A ResponseEntity containing the paginated list of enterprise users.
     * @throws DomainException If any validation or processing error occurs.
     */
    @PostMapping(value = "/enterprise/user/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEnterpriseUserList(
            @Validated @RequestBody(required = true) GetEnterpriseUserListRequest getEnterpriseUserListRequest,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Max(value=100, message = "Page size cannot exceed 100") int pageSize,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir)
            throws DomainException {

        validator.validate(getEnterpriseUserListRequest);

        GetEnterpriseUserListResponse response = enterpriseUserManagerService.getEnterpriseUserList(getEnterpriseUserListRequest, pageSize, pageNo, sortBy, sortDir);
        Map trResponse = responseEntityTransformer.transform(response, getEnterpriseUserListTransformer);
        return getResponseEntity(response.getResponseHeader().getResponseCode(), trResponse);
    }

    /**
     * Approve Enterprise User Controller
     *
     * @param approveEnterpriseUserRequest The request object containing the user ID to be approved.
     * @return A ResponseEntity containing the response details after approving the user.
     * @throws DomainException If the user does not exist or any validation or processing error occurs.
     */
    @PostMapping(value = "/enterprise/user/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> approveEnterpriseUser(@Validated @RequestBody(required = true) ApproveEnterpriseUserRequest approveEnterpriseUserRequest) throws DomainException, JsonProcessingException {

        validator.validate(approveEnterpriseUserRequest);

        ApproveEnterpriseUserResponse response = enterpriseUserManagerService.approveEnterpriseUser(approveEnterpriseUserRequest);
        Map trResponse = responseEntityTransformer.transform(response, approveEnterpriseUserTransformer);
        return getResponseEntity(response.getResponseHeader().getResponseCode(), trResponse);
    }
}