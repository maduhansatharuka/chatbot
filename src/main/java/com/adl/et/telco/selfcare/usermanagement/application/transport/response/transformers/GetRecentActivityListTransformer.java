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
package com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers;

import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.recentActivityList.GetRecentActivityListResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the GetRecentActivityListTransformer class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.03.10
 */
@Component
public class GetRecentActivityListTransformer implements ResponseEntityInterface {

    /**
     * Transforms a GetFaqListResponse object into a Map representation.
     *
     * @param entity - the object to be transformed, expected to be of type GetRecentActivityListResponse
     * @return Map<String, Object> - a map containing the "responseBody" and "responseHeader" extracted from the response
     */
    @Override
    public Map<String, Object> transform(Object entity) {

        GetRecentActivityListResponse response = (GetRecentActivityListResponse) entity;
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("responseBody", response.getResponseBody());
        mapping.put("responseHeader", response.getResponseHeader());

        return mapping;
    }
}
