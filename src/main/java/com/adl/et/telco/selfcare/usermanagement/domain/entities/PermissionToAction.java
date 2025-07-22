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

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

/**
 * This is the entity class for RoleToPermission Entity
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/06
 */
@Entity
@Table(name = "permission_to_action")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class PermissionToAction {

    @EmbeddedId
    private CompositePermissionAction id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permissionId", referencedColumnName = "id")
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actionId")
    @JoinColumn(name = "actionId", referencedColumnName = "id")
    private Action action;
}