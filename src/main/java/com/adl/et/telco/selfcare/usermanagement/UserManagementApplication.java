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
package com.adl.et.telco.selfcare.usermanagement;

import ch.qos.logback.classic.Logger;
import com.adl.et.telco.dte.plugin.logging.services.LoggingUtils;
import com.adl.et.telco.dte.plugin.pluginenabler.annotations.EnableDtePlugins;
import io.prometheus.client.CollectorRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the main spring boot application class for User Management Service.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2024/10/18
 */
@SpringBootApplication
@DependsOn({"dteLoggingUtils"})
@Import({PrometheusScrapeEndpoint.class, CollectorRegistry.class})
@EnableDtePlugins
public class UserManagementApplication {
    private static final Logger logger = LoggingUtils.getLogger(UserManagementApplication.class.getName());

    /**
     * Main entry point of the application.
     * Sets the host address and runs the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        try {
            setHostAddress();
        } catch (UnknownHostException e) {
            logger.info("Error setting host address : {}", e.getMessage());

        }
        SpringApplication.run(UserManagementApplication.class, args);
    }

    /**
     * Sets the host address system property to the local host's IP address.
     *
     * @throws UnknownHostException if the local host cannot be resolved
     */
    private static void setHostAddress() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        String hostAddress = ip.getHostAddress();
        System.setProperty("host.address", hostAddress);
    }

    /**
     * Configures the WebMvcEndpointHandlerMapping for the application.
     *
     * @param webEndpointsSupplier        supplies web endpoints
     * @param servletEndpointsSupplier    supplies servlet endpoints
     * @param controllerEndpointsSupplier supplies controller endpoints
     * @param endpointMediaTypes          media types for endpoints
     * @param corsProperties              CORS properties for the endpoints
     * @param webEndpointProperties       properties for web endpoints
     * @param environment                 the application environment
     * @return configured WebMvcEndpointHandlerMapping
     */
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping);
    }

    /**
     * Determines whether to register links mapping based on web endpoint properties.
     *
     * @param webEndpointProperties the properties for web endpoints
     * @param environment           the application environment
     * @param basePath              the base path for endpoints
     * @return true if links mapping should be registered; false otherwise
     */
    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }

}
