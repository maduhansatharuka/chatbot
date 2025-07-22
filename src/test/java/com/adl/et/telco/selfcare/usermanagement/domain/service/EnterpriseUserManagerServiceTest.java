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
package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.dto.approveEnterpriseUser.ApproveEnterpriseUserRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.approveEnterpriseUser.ApproveEnterpriseUserRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserList.GetEnterpriseUserListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserList.GetEnterpriseUserListRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.ActivityStatus;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.EnterpriseUsersRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.PermissionRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser.ApproveEnterpriseUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.ComponentsList;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserDetails.GetEnterpriseUserDetailsResponseBody;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.GetEnterpriseUserListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.UserDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUserIdTypes;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.EnterpriseUsers;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.service.AdminActivitySaveService;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import com.adl.et.telco.selfcare.usermanagement.external.service.SendEmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.APPROVE_ENTERPRISE_USER_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.ENTERPRISE_USER_NOT_EXISTS_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus.APPROVED;
import static com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus.PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EnterpriseUserManagerServiceTest {

    @InjectMocks
    private EnterpriseUserManagerService enterpriseUserManagerService;

    @Mock
    private EnterpriseUsersRepository enterpriseUsersRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private AdminActivitySaveService adminActivitySaveService;

    private ApproveEnterpriseUserRequest approveEnterpriseUserRequest;

    @Mock
    private SendEmailService sendEmailService;

    @BeforeEach
    void setUp() {
        RequestHeader requestHeader = new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type");
        ApproveEnterpriseUserRequestBody requestBody = new ApproveEnterpriseUserRequestBody(1L, PENDING);
        approveEnterpriseUserRequest = new ApproveEnterpriseUserRequest();
        approveEnterpriseUserRequest.setRequestHeader(requestHeader);
        approveEnterpriseUserRequest.setRequestBody(requestBody);
    }

    @Test
    void testGetEnterpriseUserDetails() {
        when(enterpriseUsersRepository.getEnterpriseUserDetails(anyLong(), anyString(), anyString(), anyString(), any(UserStatus.class), any(LocalDateTime.class)))
                .thenReturn(new GetEnterpriseUserDetailsResponseBody());

        GetEnterpriseUserDetailsRequest request = new GetEnterpriseUserDetailsRequest();
        request.setRequestBody(new GetEnterpriseUserDetailsRequestBody(1L, "42", "janedoe", "jane.doe@example.org"));

        assertThrows(DomainException.class, () -> enterpriseUserManagerService.getEnterpriseUserDetails(request));
        verify(enterpriseUsersRepository).getEnterpriseUserDetails(eq(1L), eq("42"), eq("janedoe"), eq("jane.doe@example.org"), eq(APPROVED), any(LocalDateTime.class));
    }

    @Test
    void testGetEnterpriseUserDetails_withNullResponse() {
        when(enterpriseUsersRepository.getEnterpriseUserDetails(anyLong(), anyString(), anyString(), anyString(), any(UserStatus.class), any(LocalDateTime.class)))
                .thenReturn(null);

        GetEnterpriseUserDetailsRequest request = new GetEnterpriseUserDetailsRequest();
        request.setRequestBody(new GetEnterpriseUserDetailsRequestBody(1L, "42", "janedoe", "jane.doe@example.org"));

        assertThrows(DomainException.class, () -> enterpriseUserManagerService.getEnterpriseUserDetails(request));
        verify(enterpriseUsersRepository).getEnterpriseUserDetails(eq(1L), eq("42"), eq("janedoe"), eq("jane.doe@example.org"), eq(APPROVED), any(LocalDateTime.class));
    }

    @Test
    void testGetEnterpriseUserDetails_withValidPermissions() {
        List<Object[]> objectArrayList = new ArrayList<>();
        objectArrayList.add(new Object[]{"42", "42"});
        when(permissionRepository.findComponentIdsAndActionsByUserId(anyLong())).thenReturn(objectArrayList);
        when(permissionRepository.findMenuIdsByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(enterpriseUsersRepository.getEnterpriseUserDetails(anyLong(), anyString(), anyString(), anyString(), any(UserStatus.class), any(LocalDateTime.class)))
                .thenReturn(new GetEnterpriseUserDetailsResponseBody(1L, "Jane", "Doe", EnterpriseUserIdTypes.NIC, "42", "42", "jane.doe@example.org", "janedoe", PENDING, "42"));

        GetEnterpriseUserDetailsRequest request = new GetEnterpriseUserDetailsRequest();
        request.setRequestBody(new GetEnterpriseUserDetailsRequestBody(1L, "42", "janedoe", "jane.doe@example.org"));

        assertThrows(DomainException.class, () -> enterpriseUserManagerService.getEnterpriseUserDetails(request));
        verify(enterpriseUsersRepository).getEnterpriseUserDetails(eq(1L), eq("42"), eq("janedoe"), eq("jane.doe@example.org"), eq(APPROVED), any(LocalDateTime.class));
        verify(permissionRepository).findComponentIdsAndActionsByUserId(eq(1L));
        verify(permissionRepository).findMenuIdsByUserId(eq(1L));
    }

    @Test
    void testGetEnterpriseUserDetails_withInvalidPermissions() {
        List<Object[]> objectArrayList = new ArrayList<>();
        objectArrayList.add(new Object[]{null, null});
        when(permissionRepository.findComponentIdsAndActionsByUserId(anyLong())).thenReturn(objectArrayList);
        when(permissionRepository.findMenuIdsByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(enterpriseUsersRepository.getEnterpriseUserDetails(anyLong(), anyString(), anyString(), anyString(), any(UserStatus.class), any(LocalDateTime.class)))
                .thenReturn(new GetEnterpriseUserDetailsResponseBody(1L, "Jane", "Doe", EnterpriseUserIdTypes.NIC, "42", "42", "jane.doe@example.org", "janedoe", PENDING, "42"));

        GetEnterpriseUserDetailsRequest request = new GetEnterpriseUserDetailsRequest();
        request.setRequestBody(new GetEnterpriseUserDetailsRequestBody(1L, "42", "janedoe", "jane.doe@example.org"));

        assertThrows(DomainException.class, () -> enterpriseUserManagerService.getEnterpriseUserDetails(request));
        verify(enterpriseUsersRepository).getEnterpriseUserDetails(eq(1L), eq("42"), eq("janedoe"), eq("jane.doe@example.org"), eq(APPROVED), any(LocalDateTime.class));
        verify(permissionRepository).findComponentIdsAndActionsByUserId(eq(1L));
        verify(permissionRepository).findMenuIdsByUserId(eq(1L));
    }

    @Test
    void testBuildComponentsList_withValidActionReferences() {
        List<Object[]> objectArrayList = new ArrayList<>();
        objectArrayList.add(new Object[]{42L, "1,2,3"});
        objectArrayList.add(new Object[]{43L, "4,5"});
        when(permissionRepository.findComponentIdsAndActionsByUserId(anyLong())).thenReturn(objectArrayList);

        List<ComponentsList> componentsList = enterpriseUserManagerService.buildComponentsList(objectArrayList);

        assertNotNull(componentsList);
        assertEquals(2, componentsList.size());
        ComponentsList components1 = componentsList.get(0);
        assertEquals(Arrays.asList(1L, 2L, 3L), components1.getActions());

        ComponentsList components2 = componentsList.get(1);
        assertEquals(Arrays.asList(4L, 5L), components2.getActions());
    }

    @Test
    void testBuildComponentsList_withInvalidActionReferences() {
        List<Object[]> objectArrayList = new ArrayList<>();
        objectArrayList.add(new Object[]{42L, "invalid,action,reference"});
        when(permissionRepository.findComponentIdsAndActionsByUserId(anyLong())).thenReturn(objectArrayList);

        assertThrows(NumberFormatException.class, () -> {
            enterpriseUserManagerService.buildComponentsList(objectArrayList);
        });
    }

    @Test
    void testBuildComponentsList_withEmptyObjectArray() {
        List<Object[]> objectArrayList = new ArrayList<>();
        objectArrayList.add(new Object[]{null, null});
        when(permissionRepository.findComponentIdsAndActionsByUserId(anyLong())).thenReturn(objectArrayList);

        List<ComponentsList> componentsList = enterpriseUserManagerService.buildComponentsList(objectArrayList);

        assertNotNull(componentsList);
        assertTrue(componentsList.isEmpty());
    }

    @Test
    void testGetEnterpriseUserList() throws DomainException {
        GetEnterpriseUserListRequest request = new GetEnterpriseUserListRequest();
        request.setRequestBody(new GetEnterpriseUserListRequestBody("jane.doe@example.org", "janedoe", APPROVED));
        request.setRequestHeader(new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type"));

        Page<UserDetails> userDetailsPage = new PageImpl<>(Collections.singletonList(
                new UserDetails(1L, "Jane", "Doe", EnterpriseUserIdTypes.NIC, "42", "42", "jane.doe@example.org", "janedoe", "1L", LocalDateTime.now(), "admin", LocalDateTime.now(), "admin",""))
        );

        when(enterpriseUsersRepository.getEnterpriseUserList(anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(userDetailsPage);

        GetEnterpriseUserListResponse response = enterpriseUserManagerService.getEnterpriseUserList(request, 10, 1, "userId", "ASC");

        assertNotNull(response);
        assertEquals("200", response.getResponseHeader().getResponseCode());
        assertEquals("42", response.getResponseHeader().getRequestId());
        assertEquals("SUCCESS", response.getResponseHeader().getDesc());
        assertEquals("UMS1000", response.getResponseHeader().getCode());
        assertEquals(1, response.getResponseBody().getUserList().size());
        verify(enterpriseUsersRepository).getEnterpriseUserList(eq("jane.doe@example.org"), eq("janedoe"), eq(APPROVED), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testGetEnterpriseUserList_whenRepositoryThrowsException() {
        GetEnterpriseUserListRequest request = new GetEnterpriseUserListRequest();
        request.setRequestBody(new GetEnterpriseUserListRequestBody("jane.doe@example.org", "janedoe", APPROVED));
        request.setRequestHeader(new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type"));

        when(enterpriseUsersRepository.getEnterpriseUserList(anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        DomainException exception = assertThrows(DomainException.class, () -> {
            enterpriseUserManagerService.getEnterpriseUserList(request, 10, 1, "userId", "ASC");
        });

        assertEquals(DomainErrorCode.GET_ENTERPRISE_USER_LIST_ERROR.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.GET_ENTERPRISE_USER_LIST_ERROR.getDescription(), exception.getMessage());
        verify(enterpriseUsersRepository).getEnterpriseUserList(eq("jane.doe@example.org"), eq("janedoe"), eq(APPROVED), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testApproveEnterpriseUser_Success() throws DomainException, JsonProcessingException {
        EnterpriseUsers enterpriseUsers = new EnterpriseUsers();
        enterpriseUsers.setUserId(1L);
        enterpriseUsers.setEmail("test@example.com");
        enterpriseUsers.setFirstName("John");
        enterpriseUsers.setLastName("Doe");

        when(enterpriseUsersRepository.findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class))).thenReturn(enterpriseUsers);
        when(enterpriseUsersRepository.saveAndFlush(any(EnterpriseUsers.class))).thenReturn(enterpriseUsers);

        ApproveEnterpriseUserResponse response = enterpriseUserManagerService.approveEnterpriseUser(approveEnterpriseUserRequest);

        assertNotNull(response);
        assertEquals("200", response.getResponseHeader().getResponseCode());
        assertEquals("42", response.getResponseHeader().getRequestId());
        assertEquals(1L, response.getResponseBody().getUserId());

        verify(enterpriseUsersRepository).findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class));
        verify(enterpriseUsersRepository).saveAndFlush(any(EnterpriseUsers.class));
        verify(sendEmailService).sendEmail(any());
        verify(adminActivitySaveService).adminActivitySave(eq(approveEnterpriseUserRequest.getRequestHeader()), eq("Approve Enterprise User"), eq("Approve Enterprise User Success"), eq(Status.SUCCESS));
    }


    @Test
    void testApproveEnterpriseUser_UserNotExists() {
        ApproveEnterpriseUserRequest request = new ApproveEnterpriseUserRequest();
        request.setRequestBody(new ApproveEnterpriseUserRequestBody(1L, PENDING));
        request.setRequestHeader(new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type"));

        when(enterpriseUsersRepository.findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class)))
                .thenReturn(null);

        DomainException exception = assertThrows(DomainException.class, () -> {
            enterpriseUserManagerService.approveEnterpriseUser(request);
        });

        assertEquals(ENTERPRISE_USER_NOT_EXISTS_ERROR.getCode(), exception.getCode());
        assertEquals(ENTERPRISE_USER_NOT_EXISTS_ERROR.getDescription(), exception.getMessage());
        verify(enterpriseUsersRepository).findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class));
        verify(enterpriseUsersRepository, never()).saveAndFlush(any(EnterpriseUsers.class));
    }

    @Test
    void testApproveEnterpriseUser_RepositoryThrowsException() {
        ApproveEnterpriseUserRequest request = new ApproveEnterpriseUserRequest();
        request.setRequestBody(new ApproveEnterpriseUserRequestBody(1L, PENDING));
        request.setRequestHeader(new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type"));

        when(enterpriseUsersRepository.findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class)))
                .thenReturn(new EnterpriseUsers());
        when(enterpriseUsersRepository.saveAndFlush(any(EnterpriseUsers.class)))
                .thenThrow(new RuntimeException("Database error"));

        DomainException exception = assertThrows(DomainException.class, () -> {
            enterpriseUserManagerService.approveEnterpriseUser(request);
        });

        assertEquals(APPROVE_ENTERPRISE_USER_ERROR.getCode(), exception.getCode());
        assertEquals(APPROVE_ENTERPRISE_USER_ERROR.getDescription(), exception.getMessage());
        verify(enterpriseUsersRepository).findByUserIdAndStatus(eq(1L), eq(PENDING), any(LocalDateTime.class));
        verify(enterpriseUsersRepository).saveAndFlush(any(EnterpriseUsers.class));
    }
}