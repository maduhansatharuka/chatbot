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


import com.adl.et.telco.selfcare.usermanagement.application.dto.RecentActivityList.GetRecentActivityListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequest;
import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.CreateActivityTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers.GetRecentActivityListTransformer;
import com.adl.et.telco.selfcare.usermanagement.application.validator.RequestEntityValidator;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.CreateActivity.CreateActivityResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.GetRecentActivityListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.EnterpriseActivityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This is the AdminActivityController class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@RestController
@RequestMapping("${base-url.context}/activity")
public class RecentActivityController extends BaseController{

    @Autowired
    private RequestEntityValidator validator;
    @Autowired
    private EnterpriseActivityService enterpriseActivityService;
    @Autowired
    private ResponseEntityTransformer responseEntityTransformer;
    @Autowired
    private GetRecentActivityListTransformer getRecentActivityListTransformer;
    @Autowired
    private CreateActivityTransformer createActivityTransformer;

    /**
     * This controller method retrieves a paginated list of recent activities.
     *
     * @param getRecentActivityListRequest - the request body containing the filters and criteria for fetching recent activities
     * @param pageSize - the number of items to display per page (default is 6)
     * @param pageNo - the current page number to fetch (default is 1)
     * @param sortBy - the field used for sorting the recent activities (default is "activityId")
     * @param sortDir - the sorting direction, either "asc" or "desc" (default is "desc")
     * @return ResponseEntity<Object> - the response entity containing the paginated list of recent activities
     * @throws DomainException - if validation fails or any service-related issues occur during processing
     */
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getRecentActivityList(
            @Valid @RequestBody GetRecentActivityListRequest getRecentActivityListRequest,
            @RequestParam(value = "pageSize", required = false, defaultValue = "6") @Max(value=100, message = "Page size cannot exceed 100") int pageSize,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "sortBy", required = false, defaultValue = "activityId") String sortBy,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir)
            throws DomainException {

        validator.validate(getRecentActivityListRequest);
        GetRecentActivityListResponse getRecentActivityListResponse = enterpriseActivityService.getRecentActivityByPagination(getRecentActivityListRequest, pageSize, pageNo, sortBy, sortDir);
        Map trResponse = responseEntityTransformer.transform(getRecentActivityListResponse, getRecentActivityListTransformer);
        return getResponseEntity(getRecentActivityListResponse.getResponseHeader().getResponseCode(), trResponse);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createEnterpriseActivity(@Valid @RequestBody CreateActivityRequest createActivityRequest) throws DomainException {

        validator.validate(createActivityRequest);
        CreateActivityResponse response = enterpriseActivityService.createEnterpriseActivity(createActivityRequest);
        Map trResponse = responseEntityTransformer.transform(response, createActivityTransformer);
        return getResponseEntity(response.getResponseHeader().getResponseCode(), trResponse);
    }
}
