package com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers;

import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.approveEnterpriseUser.ApproveEnterpriseUserResponse;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.getEnterpriseUserList.GetEnterpriseUserListResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the GetEnterpriseUserListTransformer class.
 *
 * @author Maduhansa Tharuka
 * @version 1.0
 * @since 2025.02.25
 */
@Component
public class ApproveEnterpriseUserTransformer implements ResponseEntityInterface {

    public Map transform(Object entity) {

        ApproveEnterpriseUserResponse response = (ApproveEnterpriseUserResponse) entity;
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("responseBody", response.getResponseBody());
        mapping.put("responseHeader", response.getResponseHeader());
        return mapping;
    }
}
