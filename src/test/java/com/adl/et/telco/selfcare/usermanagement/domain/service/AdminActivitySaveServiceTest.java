package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.ActivityStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.RecentActivityRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import com.adl.et.telco.selfcare.usermanagement.external.service.ExternalAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminActivitySaveServiceTest {

    @InjectMocks
    private AdminActivitySaveService adminActivitySaveService;

    @Mock
    private RecentActivityRepository recentActivityRepository;

    @Mock
    private ExternalAPIService externalAPIService;

    @Value("${url.config.createActivity}")
    private String url;

    private RequestHeader requestHeader;
    private String activity;
    private String details;
    private Status status;

    @BeforeEach
    void setUp() {
        requestHeader = new RequestHeader("42", "Timestamp", "Channel", "42", "42", "Msisdn", "Primary Msisdn", "42", "Device Model", "Device Type");
        activity = "Test Activity";
        details = "Test Details";
        status = Status.SUCCESS;
    }

    @Test
    void testAdminActivitySave_Success() throws DomainException {
        CreateActivityRequestBody createActivityRequestBody = new CreateActivityRequestBody(
                requestHeader.getUserId(),
                activity,
                details,
                status,
                LocalDateTime.now()
        );
        CreateActivityRequest createActivityRequest = new CreateActivityRequest(
                requestHeader,
                createActivityRequestBody
        );

        when(externalAPIService.callForAllExternalAPIS(eq(HttpMethod.POST), eq(url), any(CreateActivityRequest.class)))
                .thenReturn(ResponseEntity.ok(new HashMap<>()));

        adminActivitySaveService.adminActivitySave(requestHeader, activity, details, status);

        verify(externalAPIService, times(1)).callForAllExternalAPIS(eq(HttpMethod.POST), eq(url), any(CreateActivityRequest.class));
    }

    @Test
    void testAdminActivitySave_Exception() throws DomainException {
        doThrow(new RuntimeException("Exception occurred")).when(externalAPIService)
                .callForAllExternalAPIS(eq(HttpMethod.POST), eq(url), any(CreateActivityRequest.class));

        adminActivitySaveService.adminActivitySave(requestHeader, activity, details, status);

        verify(externalAPIService, times(1)).callForAllExternalAPIS(eq(HttpMethod.POST), eq(url), any(CreateActivityRequest.class));
    }
}