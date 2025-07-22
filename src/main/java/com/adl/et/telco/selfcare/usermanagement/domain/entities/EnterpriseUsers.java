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
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

/**
 * This is the entity class for Enterprise Users.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/18
 */
@Entity
@Table(name = "enterprise_user",
        indexes = {
                @Index(name = "idx_enterprise_user_status", columnList = "status"),
                @Index(name = "idx_enterprise_user_idNumber", columnList = "idNumber"),
                @Index(name = "idx_enterprise_user_accountId", columnList = "accountId")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_enterprise_user_userName", columnNames = "userName"),
                @UniqueConstraint(name = "uq_enterprise_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_enterprise_user_mobileNumber", columnNames = "mobileNumber")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class EnterpriseUsers extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Column(name = "firstName", length = 50, nullable = false)
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(name = "lastName", length = 50, nullable = false)
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(name = "idType", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private EnterpriseUserIdTypes idType;

    @Column(name = "idNumber", length = 30, nullable = false)
    @Size(min = 5, max = 30)
    private String idNumber;

    @Column(name = "mobileNumber", length = 15, nullable = false, unique = true)
    @Size(min = 7, max = 15)
    private String mobileNumber;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    @Size(min = 6, max = 100)
    private String email;

    @Column(name = "userName", length = 20, unique = true)
    @Size(min = 3, max = 20)
    private String userName;

    @Column(name = "password", length = 200, nullable = false)
    @Size(min = 8, max = 200)
    private String password;

    @Column(name = "passwordExpiryDate", length = 100, nullable = false)
    private LocalDateTime passwordExpiryDate;

    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "accountId", length = 30)
    @Size(min = 5, max = 30)
    private String accountId;

    @Column(name = "remarks", length = 100)
    private String remarks;

    @Column(name = "attempts", length = 10)
    private Integer attempts;

    @Column(name = "loginBlockedUntil", length = 100)
    private LocalDateTime loginBlockedUntil;
}