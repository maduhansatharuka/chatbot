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
package com.adl.et.telco.selfcare.usermanagement.application.dto.approveEnterpriseUser;

import com.adl.et.telco.selfcare.usermanagement.domain.entities.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the ApproveEnterpriseUserRequestBody class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.02.26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveEnterpriseUserRequestBody {

    @Valid
    @NotNull(message = "User Id must not be null")
    private Long userId;
    @Valid
    @NotNull(message = "User Status must not be null")
    private UserStatus status;
}
