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
package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.application.dto.approveEnterpriseUser.ApproveEnterpriseUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.forgetPasswordGenerateEmail.ForgetPasswordGenerateEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserList.GetEnterpriseUserListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserList.GetEnterpriseUserListRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.ActivityStatus;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.PermissionRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.Pagination;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser.ApproveEnterpriseUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser.ApproveEnterpriseUserResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.ComponentsList;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.PermissionsList;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.GetEnterpriseUserListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.GetEnterpriseUserListResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.UserDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import com.adl.et.telco.selfcare.usermanagement.external.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.PlaceholderList;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequest;
import com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail.SendEmailRequestBody;
import com.adl.et.telco.selfcare.usermanagement.external.service.SendEmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.RES_HTTP_CODE;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;
import static com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus.APPROVED;

/**
 * This is the Service Class for EnterpriseUserManager.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.04.30
 */

@Service
public class EnterpriseUserManagerService {

    @Autowired
    private EnterpriseUsersRepository repository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private AdminActivitySaveService adminActivitySaveService;
    @Autowired
    private SendEmailService sendEmailService;

    /**
     * This is getEnterpriseUserDetails
     *
     * @param request
     * @return
     * @throws DomainException
     */
    public GetEnterpriseUserDetailsResponse getEnterpriseUserDetails(GetEnterpriseUserDetailsRequest request) throws DomainException {

        try {
            GetEnterpriseUserDetailsResponseBody enterpriseUserDetails = repository.getEnterpriseUserDetails(request.getRequestBody().getUserId(), request.getRequestBody().getMobileNumber(), request.getRequestBody().getUserName(), request.getRequestBody().getEmail(), APPROVED, LocalDateTime.now());

            Long userId = (enterpriseUserDetails != null) ? enterpriseUserDetails.getUserId() : null;

            List<PermissionsList> permissionsList = (userId != null) ? fetchPermissions(userId) : Collections.emptyList();

            if (enterpriseUserDetails != null) {
                enterpriseUserDetails.setPermissions(permissionsList);
            }

            ResponseHeader responseHeader = new ResponseHeader(request.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(), DomainErrorCode.SUCCESS.getDescription(), "200");
            return new GetEnterpriseUserDetailsResponse(enterpriseUserDetails, responseHeader);
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            throw new DomainException(GET_ENTERPRISE_USER_DETAILS_ERROR.getDescription(), GET_ENTERPRISE_USER_DETAILS_ERROR.getCode());
        }
    }

    /**
     * Retrieves a paginated list of enterprise users based on the provided filters.
     *
     * @param getEnterpriseUserListRequest The request object containing filter criteria for retrieving the user list.
     * @param pageSize The number of records per page.
     * @param pageNo The page number to retrieve.
     * @param sortBy The field by which the results should be sorted.
     * @param sortDir The sorting direction, either "asc" or "desc".
     * @return A response object containing the list of enterprise users and pagination details.
     * @throws DomainException If any error occurs during data retrieval or processing.
     */
    public GetEnterpriseUserListResponse getEnterpriseUserList(GetEnterpriseUserListRequest getEnterpriseUserListRequest, int pageSize, int pageNo, String sortBy, String sortDir) throws DomainException {

        try {
            GetEnterpriseUserListRequestBody requestBody = getEnterpriseUserListRequest.getRequestBody();
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.fromString(sortDir), sortBy);
            Page<UserDetails> userDetailsPage = repository.getEnterpriseUserList(requestBody.getEmail(), requestBody.getUserName(), requestBody.getStatus(), LocalDateTime.now(), pageable);

            int totalCount = (int) userDetailsPage.getTotalElements();
            Pagination pagination = new Pagination(totalCount, pageNo);

            GetEnterpriseUserListResponseBody responseBody = new GetEnterpriseUserListResponseBody(userDetailsPage.getContent(), pagination);
            ResponseHeader responseHeader = new ResponseHeader(getEnterpriseUserListRequest.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(),
                    DomainErrorCode.SUCCESS.getDescription(), RES_HTTP_CODE);

            adminActivitySaveService.adminActivitySave(getEnterpriseUserListRequest.getRequestHeader(), "Get Enterprise User List", "Get Enterprise User List Success", Status.SUCCESS);

            return new GetEnterpriseUserListResponse(responseBody, responseHeader);


        }
        catch(Exception e){
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            adminActivitySaveService.adminActivitySave(getEnterpriseUserListRequest.getRequestHeader(), "Get Enterprise User List", "Get Enterprise User List Failed", Status.FAILED);
            throw new DomainException(GET_ENTERPRISE_USER_LIST_ERROR.getDescription(), GET_ENTERPRISE_USER_LIST_ERROR.getCode());
        }
    }

    /**
     * Approves an enterprise user by updating their status from PENDING to APPROVED.
     *
     * @param approveEnterpriseUserRequest The request object containing the user ID to be approved.
     * @return A response object containing the approved user's ID and response details.
     * @throws DomainException If the user does not exist or any processing error occurs.
     */
    public ApproveEnterpriseUserResponse approveEnterpriseUser(ApproveEnterpriseUserRequest approveEnterpriseUserRequest) throws DomainException, JsonProcessingException {

        try {
            EnterpriseUsers enterpriseUsers = repository.findByUserIdAndStatus(approveEnterpriseUserRequest.getRequestBody().getUserId(), UserStatus.PENDING, LocalDateTime.now());
            if (enterpriseUsers == null) {
                throw new DomainException(ENTERPRISE_USER_NOT_EXISTS_ERROR.getDescription(), ENTERPRISE_USER_NOT_EXISTS_ERROR.getCode());
            }

            enterpriseUsers.setStatus(approveEnterpriseUserRequest.getRequestBody().getStatus());
            repository.saveAndFlush(enterpriseUsers);

            ApproveEnterpriseUserResponseBody responseBody = new ApproveEnterpriseUserResponseBody(enterpriseUsers.getUserId());
            sendEmailService.sendEmail(mapToSendEmailRequest(enterpriseUsers, approveEnterpriseUserRequest));
            ResponseHeader responseHeader = new ResponseHeader(approveEnterpriseUserRequest.getRequestHeader().getRequestId(), LocalDateTime.now().toString(), DomainErrorCode.SUCCESS.getCode(),
                    DomainErrorCode.SUCCESS.getDescription(), RES_HTTP_CODE);

            adminActivitySaveService.adminActivitySave(approveEnterpriseUserRequest.getRequestHeader(), "Approve Enterprise User", "Approve Enterprise User Success", Status.SUCCESS);

            return new ApproveEnterpriseUserResponse(responseBody, responseHeader);
        }
        catch(Exception e){
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            adminActivitySaveService.adminActivitySave(approveEnterpriseUserRequest.getRequestHeader(), "Approve Enterprise User", "Approve Enterprise User Failed", Status.FAILED);
            if (e instanceof DomainException) {
                throw e;
            }
            throw new DomainException(APPROVE_ENTERPRISE_USER_ERROR.getDescription(), APPROVE_ENTERPRISE_USER_ERROR.getCode());
        }
    }

    /**
     * Fetches the list of permissions for a given user ID, including menu IDs and components with actions.
     *
     * @param userId the ID of the user whose permissions are being fetched
     * @return a list containing a single PermissionsList object with menu IDs and components for the user
     */
    private List<PermissionsList> fetchPermissions(Long userId) {
        List<Long> menuIds = (userId != null) ? permissionRepository.findMenuIdsByUserId(userId) : Collections.emptyList();
        List<Object[]> results = (userId != null) ? permissionRepository.findComponentIdsAndActionsByUserId(userId) : Collections.emptyList();

        List<ComponentsList> componentsList = buildComponentsList(results);

        return Collections.singletonList(new PermissionsList(menuIds, componentsList));
    }

    /**
     * Builds a list of ComponentsList objects from raw results, ensuring each row is valid and contains
     * the necessary data (component ID and action references).
     *
     * @param results a list of Object arrays, where each array contains a component ID and comma-separated actions
     * @return a list of ComponentsList objects with component IDs and action lists
     */
    List<ComponentsList> buildComponentsList(List<Object[]> results) {
        List<ComponentsList> componentsList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                    Long componentId = ((Number) row[0]).longValue();
                    String actionReferences = (String) row[1];

                    List<Long> actions = Arrays.stream(actionReferences.split(",")).map(Long::parseLong).toList();

                    componentsList.add(new ComponentsList(componentId, actions, new ArrayList<>()));
                }
            }
        }

        return componentsList;
    }

    private SendEmailRequest mapToSendEmailRequest(EnterpriseUsers enterpriseUsers, ApproveEnterpriseUserRequest approveEnterpriseUserRequest) {
        SendEmailRequestBody requestBody = new SendEmailRequestBody();
        requestBody.setSubject("User Approved");
        requestBody.setFromName("Admin");
        requestBody.setToList(Collections.singletonList(enterpriseUsers.getEmail()));
        requestBody.setTemplateId("enterprise-user-approve");

        // Placeholder values for email template
        List<PlaceholderList> placeholderList = new ArrayList<>();
        placeholderList.add(new PlaceholderList("firstName", enterpriseUsers.getFirstName()));
        placeholderList.add(new PlaceholderList("lastName", enterpriseUsers.getLastName()));

        requestBody.setPlaceholderList(placeholderList);

        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setRequestId(approveEnterpriseUserRequest.getRequestHeader().getRequestId());
        requestHeader.setTimestamp(LocalDateTime.now().toString());
        requestHeader.setChannel(approveEnterpriseUserRequest.getRequestHeader().getChannel());

        return new SendEmailRequest(requestBody, requestHeader);
    }
}