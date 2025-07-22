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

import com.adl.et.telco.selfcare.usermanagement.domain.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

/**
 * This is the entity class for Component Entity
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/06
 */
@Entity
@Table(name = "component")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class Component extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "referenceId", length = 20, nullable = false)
    private Long referenceId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @Column(name = "menuId", length = 20, nullable = false)
    private Long menuId;
}