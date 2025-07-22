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

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This is the EnterpriseUserRegistration Request Body class
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/02/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequestBody {
    @NotNull(message = "Subject cannot be null")
    @Size(min = 1, max = 100, message = "Subject must be between 1 and 100 characters")
    private String subject;

    @NotNull(message = "From name cannot be null")
    @Size(min = 1, max = 100, message = "From name must be between 1 and 100 characters")
    private String fromName;

    @Size(max = 200, message = "CC List must be between 0 and 200 characters")
    private List<String> ccList;

    @Size(max = 200, message = "To List must be between 1 and 200 characters")
    private List<String> toList;

    @NotNull(message = "Template Id cannot be null")
    @Size(min = 1, max = 50, message = "Template Id must be between 1 and 50 characters")
    private String templateId;

    @Valid
    private List<PlaceholderList> placeholderList;

    @Valid
    private List<AttachmentList> attachmentList;

    // This method will check if at least one of toList or ccList is provided and not empty. If both are null or empty, it will trigger a validation failure.
    @AssertTrue(message = "At least one recipient (To or CC) must be provided")
    public boolean isValidRecipientList() {
        return (toList != null && !toList.isEmpty()) || (ccList != null && !ccList.isEmpty());
    }
}