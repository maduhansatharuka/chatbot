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
package com.adl.et.telco.selfcare.usermanagement.application.transformer;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This is the response entity transformer class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.10.16
 */
@Component
public class ResponseEntityTransformer {
    /**
     * Transforms to map
     *
     * @param entity
     * @param transformer
     * @return
     */
    public Map transform(Object entity, ResponseEntityInterface transformer) {
        return transformer.transform(entity);
    }
}