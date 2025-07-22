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

import java.util.Map;

/**
 * This is the interface for transformers.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024.10.16
 */
public interface ResponseEntityInterface {

    public Map<String, Object> transform(Object entity);

}
