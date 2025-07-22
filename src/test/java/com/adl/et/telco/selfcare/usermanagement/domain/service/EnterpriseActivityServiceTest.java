package com.adl.et.telco.selfcare.usermanagement.domain.service;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RecentActivityList.GetRecentActivityListRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.RecentActivityList.GetRecentActivityListRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.RecentActivityRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.GetRecentActivityListResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.RecentActivityDetails;
import com.adl.et.telco.selfcare.usermanagement.domain.entities.RecentActivity.RecentActivityEntity;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnterpriseActivityServiceTest {

    @Mock
    private RecentActivityRepository recentActivityRepository;

    @InjectMocks
    private EnterpriseActivityService enterpriseActivityService;

    private RecentActivityDetails recentActivityDetails;
    private Page<RecentActivityDetails> recentActivityPage;
    private GetRecentActivityListRequest getRecentActivityListRequest;
    private GetRecentActivityListRequestBody getRecentActivityListRequestBody;

    @BeforeEach
    public void setUp() {
        recentActivityDetails = new RecentActivityDetails(1L, "Activity", "User", Status.SUCCESS, "Details", LocalDateTime.now());
        recentActivityPage = new PageImpl<>(Arrays.asList(recentActivityDetails));
        getRecentActivityListRequest = new GetRecentActivityListRequest();
        GetRecentActivityListRequestBody getRecentActivityListRequestBody = new GetRecentActivityListRequestBody();
        getRecentActivityListRequestBody.setAccountId("1");
        getRecentActivityListRequest.setRequestBody(getRecentActivityListRequestBody);
        RequestHeader header = new RequestHeader();
        header.setRequestId("testRequestId");
        getRecentActivityListRequest.setRequestHeader(header);
    }

    @Test
    public void testGetRecentActivityByPagination() throws DomainException {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "createdDate");

        when(recentActivityRepository.getRecentActivityList(any(LocalDateTime.class), any(String.class), any(Pageable.class)))
                .thenReturn(recentActivityPage);

        GetRecentActivityListResponse response = enterpriseActivityService.getRecentActivityByPagination(getRecentActivityListRequest, 10, 1, "createdDate", "asc");

        assertEquals(1, response.getResponseBody().getPagination().getTotalCount());
        assertEquals("testRequestId", response.getResponseHeader().getRequestId());
        assertEquals(DomainErrorCode.SUCCESS.getCode(), response.getResponseHeader().getCode());
    }

    @Test
    void testGetRecentActivityByPaginationThrowsException() {
        when(recentActivityRepository.getRecentActivityList(any(LocalDateTime.class), eq("1"), any(Pageable.class)))
                .thenThrow(new RuntimeException("Error occurred"));

        assertThrows(DomainException.class, () ->
                enterpriseActivityService.getRecentActivityByPagination(getRecentActivityListRequest, 10, 1, "activityId", "ASC"));

        verify(recentActivityRepository, times(1)).getRecentActivityList(any(LocalDateTime.class), eq("1"), any(Pageable.class));
    }


    @Test
    public void testSaveActivity() {
        RecentActivityEntity recentActivityEntity = new RecentActivityEntity(1L, "Login", "user1", Status.SUCCESS, "User logged in", LocalDateTime.now(), "account123");

        when(recentActivityRepository.save(any(RecentActivityEntity.class)))
                .thenReturn(recentActivityEntity);

        CompletableFuture<RecentActivityEntity> future = enterpriseActivityService.saveActivity("Login", "user1", Status.SUCCESS, "User logged in", LocalDateTime.now());

        assertNotNull(future);
        assertEquals(recentActivityEntity, future.join());
    }

    @Test
    void testSaveActivityThrowsException() {
        when(recentActivityRepository.save(any(RecentActivityEntity.class)))
                .thenThrow(new RuntimeException("Error occurred"));

        CompletableFuture<RecentActivityEntity> future = enterpriseActivityService.saveActivity("Login", "user1", Status.SUCCESS, "User logged in", LocalDateTime.now());

        assertNotNull(future);
        assertNull(future.join());

        verify(recentActivityRepository, times(1)).save(any(RecentActivityEntity.class));
    }
}