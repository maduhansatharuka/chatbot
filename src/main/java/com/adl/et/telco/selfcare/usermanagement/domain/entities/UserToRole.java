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
 * This is the entity class for UserToRole Entity
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/06
 */
@Entity
@Table(name = "user_to_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class UserToRole {

    @EmbeddedId
    private CompositeUserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private EnterpriseUsers user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "roleId", referencedColumnName = "id")
    private Role role;
}