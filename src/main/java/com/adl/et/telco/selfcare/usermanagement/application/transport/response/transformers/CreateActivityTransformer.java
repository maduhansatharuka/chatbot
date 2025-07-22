package com.adl.et.telco.selfcare.usermanagement.application.transport.response.transformers;

import com.adl.et.telco.selfcare.usermanagement.application.transformer.ResponseEntityInterface;
import com.adl.et.telco.selfcare.usermanagement.domain.dto.CreateActivity.CreateActivityResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreateActivityTransformer implements ResponseEntityInterface {

    public Map transform(Object entity) {

        CreateActivityResponse response = (CreateActivityResponse) entity;
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("responseBody", response.getResponseBody());
        mapping.put("responseHeader", response.getResponseHeader());
        return mapping;
    }
}
