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
package com.adl.et.telco.selfcare.usermanagement.domain.service;

import ch.qos.logback.classic.Logger;
import com.adl.et.telco.dte.plugin.logging.services.LoggingUtils;
import com.adl.et.telco.selfcare.usermanagement.application.dto.RecentActivityList.GetRecentActivityListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequest;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.RecentActivityRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.CreateActivity.CreateActivityResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.CreateActivity.CreateActivityResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.Pagination;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.GetRecentActivityListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.GetRecentActivityListResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.RecentActivityDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.RecentActivity.RecentActivityEntity;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.FLOW_TYPE;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.METHOD_OUT_LOG;

/**
 * This is the RecentActivityService class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Service
public class EnterpriseActivityService {

    private static final Logger logger = LoggingUtils.getLogger(AdminActivitySaveService.class.getName());

    @Autowired
    RecentActivityRepository recentActivityRepository;

    /**
     * Retrieves a paginated list of recent activities.
     *
     * @param getRecentActivityListRequest The request containing necessary headers.
     * @param pageSize The number of records per page.
     * @param pageNo The page number to retrieve.
     * @param sortBy The field by which to sort results.
     * @param sortDir The sorting direction (ASC or DESC).
     * @return A response containing the recent activities and pagination details.
     * @throws DomainException If an error occurs while retrieving recent activities.
     */
    public GetRecentActivityListResponse getRecentActivityByPagination(GetRecentActivityListRequest getRecentActivityListRequest, int pageSize, int pageNo, String sortBy, String sortDir) throws DomainException {

        try {
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.fromString(sortDir), sortBy);

            Page<RecentActivityDetails> recentActivityPage =
                    recentActivityRepository.getRecentActivityList(LocalDateTime.now(),getRecentActivityListRequest.getRequestBody().getAccountId(), pageable);

            int totalCount = (int) recentActivityPage.getTotalElements();
            Pagination pagination = new Pagination(totalCount, pageNo);

            GetRecentActivityListResponseBody responseBody = new GetRecentActivityListResponseBody(recentActivityPage.getContent(), pagination);
            ResponseHeader responseHeader = new ResponseHeader(getRecentActivityListRequest.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(),
                    DomainErrorCode.SUCCESS.getDescription(), RES_HTTP_CODE);

            return new GetRecentActivityListResponse(responseHeader, responseBody);

        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(DomainErrorCode.GET_RECENT_ACTIVITY_ERROR.getDescription(),
                    DomainErrorCode.GET_RECENT_ACTIVITY_ERROR.getCode());
        }
    }

    public CreateActivityResponse createEnterpriseActivity(CreateActivityRequest createActivityRequest) throws DomainException {

        try {
            RecentActivityEntity recentActivityResponse = saveExternalActivity(
                    createActivityRequest.getRequestBody().getActivity(),
                    createActivityRequest.getRequestBody().getUsername(),
                    createActivityRequest.getRequestBody().getStatus(),
                    createActivityRequest.getRequestBody().getDetails(),
                    createActivityRequest.getRequestBody().getActivityDate()
            );

            CreateActivityResponseBody responseBody = new CreateActivityResponseBody(recentActivityResponse.getActivityId());
            ResponseHeader responseHeader = new ResponseHeader(createActivityRequest.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(),
                    DomainErrorCode.SUCCESS.getDescription(), RES_HTTP_CODE);

            return new CreateActivityResponse(responseHeader, responseBody);
        }
        catch(Exception e){
            logger.error(METHOD_OUT_LOG, MDC.get(FLOW_TYPE), SAVE_ENTERPRISE_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED, e);
            throw new DomainException(DomainErrorCode.CREATE_ENTERPRISE_ACTIVITY_ERROR.getDescription(),
                    DomainErrorCode.CREATE_ENTERPRISE_ACTIVITY_ERROR.getCode());
        }
    }
    /**
     * Asynchronously saves a recent activity log.
     *
     * @param activity The name or type of the activity.
     * @param userName The username associated with the activity.
     * @param status The status of the activity (e.g., SUCCESS, FAILED).
     * @param details Additional details about the activity.
     * @param activityDate The timestamp of the activity.
     * @return A CompletableFuture containing the saved RecentActivityEntity.
     */
    @Async("asyncExecutor")
    public CompletableFuture<RecentActivityEntity> saveActivity(String activity, String userName, Status status, String details, LocalDateTime activityDate) {
        RecentActivityEntity recentActivityResponse = null;
        try {
            RecentActivityEntity recentActivity = new RecentActivityEntity();
            recentActivity.setActivity(activity);
            recentActivity.setUserName(userName);
            recentActivity.setStatus(status);
            recentActivity.setDetails(details);
            recentActivity.setActivityDate(activityDate);
            recentActivityResponse = recentActivityRepository.save(recentActivity);
        }
        catch(Exception e){
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
        return CompletableFuture.completedFuture(recentActivityResponse);
    }

    public RecentActivityEntity saveExternalActivity(String activity, String userName, Status status, String details, LocalDateTime activityDate) {
        RecentActivityEntity recentActivityResponse = null;
        try {
            RecentActivityEntity recentActivity = new RecentActivityEntity();
            recentActivity.setActivity(activity);
            recentActivity.setUserName(userName);
            recentActivity.setStatus(status);
            recentActivity.setDetails(details);
            recentActivity.setActivityDate(activityDate);
            recentActivityResponse = recentActivityRepository.save(recentActivity);
        }
        catch(Exception e){
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
        return recentActivityResponse;
    }
}
