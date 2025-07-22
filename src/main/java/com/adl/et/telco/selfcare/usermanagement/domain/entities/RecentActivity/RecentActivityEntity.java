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
package com.adl.et.telco.selfcare.usermanagement.domain.entities.RecentActivity;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.base.BaseEntity;
import com.adl.et.telco.selfcare.usermanagement.domain.util.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This is the RecentActivityEntity class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Entity
@Table(name = "recent_activity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityEntity extends BaseEntity {
    @Id
    @Column(name ="ActivityId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;
    @Size(max = 100, message = "The 'Activity' field must not exceed 100 characters")
    @Column(name = "Activity", nullable = false)
    private String activity;
    @Size(max = 20, message = "The 'UserName' field must not exceed 200 characters.")
    @Column(name = "UserName", nullable = false)
    private String userName;
    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Size(max = 100, message = "The 'Details' field must not exceed 100 characters")
    @Column(name = "Details", nullable = false)
    private String details;
    @Column(name = "ActivityDate")
    private LocalDateTime activityDate;
    @Column(name = "accountId", length = 30)
    @Size(min = 5, max = 30)
    private String accountId;

}
