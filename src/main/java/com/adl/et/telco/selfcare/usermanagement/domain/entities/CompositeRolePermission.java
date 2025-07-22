/*
 * Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 * All Rights Reserved.
 *<p></p>
 * These material are unpublished, proprietary, confidential source
 * code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 * SECRET of ADL.
 *<p></p>
 * ADL retains all title to and intellectual property rights in these
 * materials.
 *
 */
package com.adl.et.telco.selfcare.usermanagement.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.envers.Audited;

import java.io.Serializable;

/**
 * This is the Composite Key class for the RolePermission
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/06
 */
@Embeddable
@Audited
public class CompositeRolePermission implements Serializable {

    @Column(name = "roleId")
    private Long roleId;

    @Column(name = "permissionId")
    private Long permissionId;

}