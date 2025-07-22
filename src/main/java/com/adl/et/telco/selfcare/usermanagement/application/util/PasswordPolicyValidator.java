/*
 *
 *  Copyrights 2024 Axiata Digital Labs Pvt Ltd.
 *  All Rights Reserved.
 *
 *  These material are unpublished, proprietary, confidential source
 *  code of Axiata Digital Labs Pvt Ltd (ADL) and constitute a TRADE
 *  SECRET of ADL.
 *
 *  ADL retains all title to and intellectual property rights in these
 *  materials.
 *
 */
package com.adl.et.telco.selfcare.usermanagement.application.util;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations.ConfigListDTO;
import com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations.GetAppConfigResponse;
import com.adl.et.telco.selfcare.usermanagement.external.service.ApplicationConfigurationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.*;

/**
 * Utility class for validating password policies. Ensures that passwords
 * adhere to security standards such as length, character requirements,
 * and avoidance of common patterns and user-related information.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/04
 */
@Component
public class PasswordPolicyValidator {

    private static ApplicationConfigurationsService applicationConfigurationsService;

    @Autowired
    public PasswordPolicyValidator(ApplicationConfigurationsService applicationConfigurationsService) {
        PasswordPolicyValidator.applicationConfigurationsService = applicationConfigurationsService;
    }

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile("[!@#$%^&*()]");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 12;

    /**
     * Validates a given password against security policies, such as minimum length,
     * character types, common passwords, repeated characters, and user-related info.
     *
     * @param password  the password to validate
     * @param username  the user's username to check against
     * @param firstName the user's first name to check against
     * @param lastName  the user's last name to check against
     * @param email     the user's email address to check against
     * @throws DomainException if the password fails any validation rule
     */
    public void validate(String password, String username, String firstName, String lastName, String email, RequestHeader requestHeader) throws DomainException {

        //Call Application Configurations API to get Configurations to validate OTP length
        GetAppConfigResponse getAppConfigResponse = applicationConfigurationsService.getConfigurations("PASSWORD_POLICY", requestHeader);

        validatePasswordLength(password);
        validateCharacterRequirements(password);
        validateAgainstCommonPasswords(password, getAppConfigResponse);
        validateRepeatedCharacters(password);
        validateAgainstUserInformation(password, username, firstName, lastName, email);
    }

    /**
     * This method used to validate the Password length
     *
     * @param password
     * @throws DomainException
     */
    private static void validatePasswordLength(String password) throws DomainException {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new DomainException(PASSWORD_LENGTH_VALIDATE.getDescription(), PASSWORD_LENGTH_VALIDATE.getCode());
        }
    }

    /**
     * This method used to validate the character requirements of the Password
     *
     * @param password
     * @throws DomainException
     */
    private static void validateCharacterRequirements(String password) throws DomainException {
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            throw new DomainException(PASSWORD_VALIDATION_FOR_UPPERCASE_LETTERS.getDescription(), PASSWORD_VALIDATION_FOR_UPPERCASE_LETTERS.getCode());
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            throw new DomainException(PASSWORD_VALIDATION_FOR_LOWERCASE_LETTERS.getDescription(), PASSWORD_VALIDATION_FOR_LOWERCASE_LETTERS.getCode());
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            throw new DomainException(PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_DIGIT.getDescription(), PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_DIGIT.getCode());
        }
        if (!SPECIAL_CHARACTER_PATTERN.matcher(password).find()) {
            throw new DomainException(PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_SPECIAL_CHARACTER.getDescription(), PASSWORD_VALIDATION_FOR_AT_LEAST_ONE_SPECIAL_CHARACTER.getCode());
        }
    }

    /**
     * This method is used to validate the password input by the user against the common passwords
     *
     * @param password
     * @throws DomainException
     */
    private static void validateAgainstCommonPasswords(String password, GetAppConfigResponse getAppConfigResponse) throws DomainException {

        String commonPasswordsStr = getAppConfigResponse.getResponseBody().getConfigList()
                .stream()
                .filter(config -> "COMMON_PASSWORDS".equals(config.getConfigKey())) // Compare with string literal
                .map(ConfigListDTO::getConfigValue)
                .findFirst()
                .orElse(null);

        if (commonPasswordsStr != null) {
            // Convert the comma-separated string into a Set for efficient lookup
            Set<String> commonPasswords = Arrays.stream(commonPasswordsStr.split("\\s*,\\s*")) // Split by comma and trim spaces
                    .map(String::trim)
                    .map(String::toLowerCase) // Convert all common passwords to lowercase
                    .collect(Collectors.toSet());

            // Convert user password to lowercase for case-insensitive comparison
            String passwordLower = password.toLowerCase();

            for (String common : commonPasswords) {
                if (passwordLower.contains(common)) { // Check if password contains a common password
                    throw new DomainException(COMMON_PASSWORD_ERROR.getDescription(), COMMON_PASSWORD_ERROR.getCode());
                }
            }
        }
    }

    /**
     * This method is to validate the password for the maximum of 3 repeated characters
     *
     * @param password
     * @throws DomainException
     */
    private static void validateRepeatedCharacters(String password) throws DomainException {
        if (containsRepeatedCharacters(password)) {
            throw new DomainException(REPEATED_CHARACTERS_IN_PASSWORD_ERROR.getDescription(), REPEATED_CHARACTERS_IN_PASSWORD_ERROR.getCode());
        }
    }

    /**
     * This method is to validate the password against the provided user information
     *
     * @param password
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @throws DomainException
     */
    private static void validateAgainstUserInformation(String password, String username, String firstName, String lastName, String email) throws DomainException {
        if (containsUserInformation(password, username, firstName, lastName, email)) {
            throw new DomainException(USER_INFORMATION_IN_PASSWORD_ERROR.getDescription(), USER_INFORMATION_IN_PASSWORD_ERROR.getCode());
        }
    }

    /**
     * Checks if the password contains any repeated characters in sequence.
     *
     * @param password the password to check
     * @return true if repeated characters are found; false otherwise
     */
    private static boolean containsRepeatedCharacters(String password) {
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == chars[i + 1] && chars[i + 1] == chars[i + 2]) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the password contains any part of user-related information (username, first name, etc.).
     *
     * @param password  the password to check
     * @param username  the user's username
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email
     * @return true if any part of user information is found in the password; false otherwise
     */
    private static boolean containsUserInformation(String password, String username, String firstName, String lastName, String email) {
        String lowerPassword = password.toLowerCase();

        return containsSubstringInPassword(lowerPassword, username.toLowerCase()) || containsSubstringInPassword(lowerPassword, firstName.toLowerCase()) || containsSubstringInPassword(lowerPassword, lastName.toLowerCase()) || containsSubstringInPassword(lowerPassword, email.split("@")[0].toLowerCase());
    }

    /**
     * Checks if the password contains any substring (of length 3 or more) from the provided info.
     *
     * @param password the password to check
     * @param info     the user-related information substring to check against
     * @return true if a matching substring is found; false otherwise
     */
    private static boolean containsSubstringInPassword(String password, String info) {
        // Check all possible substrings of length 3 or more
        for (int i = 0; i <= info.length() - 3; i++) {
            String substring = info.substring(i, i + 3);
            if (password.contains(substring)) {
                return true;
            }
        }
        return false;
    }
}