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

package com.adl.et.telco.selfcare.usermanagement.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * This is the configuration file for Swagger.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/16
 */
@Configuration
public class SwaggerConfig {

    @Value("${base-url.context}")
    private String context;

    @Value("${app.host}")
    private String host;

    /**
     * Creates and returns an OpenAPI bean with application information.
     *
     * @return The configured OpenAPI instance.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(apiV3Info());
        Server server = new Server();
        server.setUrl(host);
        openAPI.setServers(Collections.singletonList(server));
        return openAPI;
    }

    /**
     * Provides information about the API, such as title, description, version, and license.
     *
     * @return An Info object with API metadata.
     */
    private Info apiV3Info() {
        return new Info().title("Business-Template").description("Welcome to MIFE REST API Integration Platform").version("1.0").license(new License().name("Apache 2.0").url("http://springdoc.org"));
    }

}
