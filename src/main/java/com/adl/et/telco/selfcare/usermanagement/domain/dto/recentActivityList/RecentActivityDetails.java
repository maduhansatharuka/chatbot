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
package com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the RecentActivityDetails class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Data
@NoArgsConstructor
public class RecentActivityDetails {
    private Long activityId;

    private String activity;

    private String userName;

    private Status status;

    private String details;

    private String createdDate;

    public RecentActivityDetails(Long activityId, String activity, String userName, Status status, String details, LocalDateTime createdDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.activityId = activityId;
        this.activity = activity;
        this.userName = userName;
        this.status = status;
        this.details = details;
        this.createdDate = createdDate !=null ? createdDate.format(formatter) :null;
    }
}
