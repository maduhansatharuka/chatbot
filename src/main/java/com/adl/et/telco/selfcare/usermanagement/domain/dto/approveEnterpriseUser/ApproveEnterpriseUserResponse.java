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
package com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser;

import com.adl.et.telco.selfcare.usermanagement.domain.dto.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the ApproveEnterpriseUserResponse class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.02.26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveEnterpriseUserResponse {
    private ApproveEnterpriseUserResponseBody responseBody;
    private ResponseHeader responseHeader;
}
