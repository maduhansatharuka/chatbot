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
package com.adl.et.telco.selfcare.usermanagement.external.dto.sendemail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the AttachmentList class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentList {
    @NotNull(message = "File name cannot be null")
    @Size(min = 1, max = 100, message = "File name must be between 1 and 100 characters")
    private String fileName;

    @NotNull(message = "File type cannot be null")
    @Size(min = 1, max = 50, message = "File type must be between 1 and 50 characters")
    private String fileType;

    @NotNull(message = "Base64 data cannot be null")
    private String base64Data;
}