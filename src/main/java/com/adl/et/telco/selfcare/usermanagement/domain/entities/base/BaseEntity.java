/*
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 *
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 *
 * ADL retains all title to and intellectual property rights in these
 * materials.
 *
 */
package com.adl.et.telco.selfcare.usermanagement.domain.entities.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

import static com.adl.et.telco.selfcare.usermanagement.application.util.Constant.SYSTEM_USER;

/**
 * This is the Base entity class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/23
 */
@MappedSuperclass
@Setter
@Getter
@Audited
public class BaseEntity {
    @CreationTimestamp
    @Column(name = "CreatedDate", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "LastUpdatedDate", nullable = false)
    private LocalDateTime lastUpdatedDate;

    @Column(name = "RecordExpiryDate")
    private LocalDateTime recordExpiryDate;

    @Column(name = "CreatedBy", columnDefinition = "VARCHAR(255) DEFAULT 'SystemUser'")
    private String createdBy = SYSTEM_USER;

    @Column(name = "UpdatedBy", columnDefinition = "VARCHAR(255) DEFAULT 'SystemUser'")
    private String updatedBy = SYSTEM_USER;

    @PreUpdate
    public void preUpdate() {
        if (updatedBy == null) {
            updatedBy = SYSTEM_USER;
        }
    }

}
