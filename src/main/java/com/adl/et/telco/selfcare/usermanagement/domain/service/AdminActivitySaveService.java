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
import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequest;
import com.adl.et.telco.selfcare.usermanagement.application.dto.adminActivity.CreateActivityRequestBody;
import com.adl.et.telco.selfcare.usermanagement.application.util.ActivityStatus;
import com.adl.et.telco.selfcare.usermanagement.domain.boundry.RecentActivityRepository;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import com.adl.et.telco.selfcare.usermanagement.external.service.ExternalAPIService;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.SAVE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.FLOW_TYPE;
import static com.adl.et.telco.selfcare.usermanagement.domain.util.logs.LogConstants.METHOD_OUT_LOG;

/**
 * This is the AdminActivitySaveService class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Service
public class AdminActivitySaveService {
    private static final Logger logger = LoggingUtils.getLogger(AdminActivitySaveService.class.getName());

    @Autowired
    RecentActivityRepository recentActivityRepository;
    @Value("${url.config.createActivity}")
    private String url;
    @Autowired
    private ExternalAPIService externalAPIService;

    /**
     * Asynchronously saves an administrative activity log.
     *
     * @param requestHeader The request header containing user-related details.
     * @param activity      The type of activity being logged.
     * @param details       Additional details about the activity.
     * @param status        The status of the activity (e.g., SUCCESS, FAILED).
     */
    @Async("asyncExecutor")
    public void adminActivitySave(RequestHeader requestHeader, String activity, String details, Status status) {
        try {
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
            ResponseEntity<HashMap> response = externalAPIService.callForAllExternalAPIS(HttpMethod.POST, url, createActivityRequest);
        }
        catch(Exception e){
            logger.error(METHOD_OUT_LOG, MDC.get(FLOW_TYPE), SAVE_ADMIN_ACTIVITY_ERROR_IN_SERVER_EXCEPTION_OCCURRED, e);
        }
    }

}
