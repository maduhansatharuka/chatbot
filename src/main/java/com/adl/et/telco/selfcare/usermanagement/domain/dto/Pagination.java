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
package com.adl.et.telco.selfcare.usermanagement.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the pagination class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Pagination {

    private int totalCount;
    private int pageNo;

}