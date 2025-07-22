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
package com.adl.et.telco.selfcare.usermanagement.application.dto.getEnterpriseUserDetails;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the Get Enterprise User Details Request tBody class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEnterpriseUserDetailsRequestBody {
    @Min(1)
    private Long userId;

    @Size(min = 7, max = 15, message = "Mobile Number must be between 7 and 15 characters")
    private String mobileNumber;

    @Size(min = 3, max = 20, message = "UserName must be between 3 and 20 characters")
    private String userName;

    @Size(min = 6, max = 100, message = "Email must be between 6 and 100 characters")
    private String email;
}