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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the entity class for User Token in Forgot Password Email Sending.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 202/03/04
 */
@Entity
@Table(name = "user_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "userId", length = 50, nullable = false)
    private Long userId;

    @Column(name = "email", length = 100, nullable = false)
    @Size(min = 1, max = 100)
    private String email;

    @Column(name = "uniqueId", length = 200, nullable = false)
    private String uniqueId;

    @Column(name = "status", length = 30)
    @Enumerated(EnumType.STRING)
    private TokenStatus status;
}