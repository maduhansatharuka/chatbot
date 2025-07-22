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
package com.adl.et.telco.selfcare.usermanagement.application.util;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * This util class is used to hash and verify the password using Argon2id algorithm.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/11/05
 */
@Component
public class PasswordUtil {

    private static final int SALT_LENGTH = 16; // 16 bytes salt
    private static final int HASH_LENGTH = 32; // 32 bytes hash
    private static final int ITERATIONS = 3; // T cost
    private static final int MEMORY = 65536; // Memory cost (64 MB)
    private static final int PARALLELISM = 2; // Number of parallel threads

    /**
     * Method to hash the password using Argon2
     *
     * @param password
     * @return
     */
    public String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new java.security.SecureRandom().nextBytes(salt); // Generate random salt

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM)
                .withSalt(salt);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash);

        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Method to verify password
     *
     * @param hashWithSalt
     * @param password
     * @return
     */
    public boolean verifyPassword(String hashWithSalt, String password) {
        String[] parts = hashWithSalt.split("\\$");
        if (parts.length != 2) return false;

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM)
                .withSalt(salt);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] computedHash = new byte[HASH_LENGTH];
        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), computedHash);

        return java.util.Arrays.equals(expectedHash, computedHash);
    }
}