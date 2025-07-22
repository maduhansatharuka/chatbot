package com.adl.et.telco.selfcare.usermanagement.external.service;

import com.adl.et.telco.selfcare.usermanagement.application.dto.RequestHeader;
import com.adl.et.telco.selfcare.usermanagement.domain.exception.DomainException;
import com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations.GetAppConfigRequest;
import com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations.GetAppConfigRequestBody;
import com.adl.et.telco.selfcare.usermanagement.external.dto.getApplicationConfigurations.GetAppConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.adl.et.telco.selfcare.usermanagement.application.util.DomainErrorCode.GET_APPLICATION_CONFIGURATIONS_ERROR;
import static com.adl.et.telco.selfcare.usermanagement.application.util.HandleErrorLogs.logError;

/**
 * The GetApplicationConfigurations class helps to retrieve application configurations from an external API.
 *
 * @author Hasini Hatharasinghe
 * @version 1.0
 * @since 2025/03/18
 */
@Service
public class ApplicationConfigurationsService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${url.for.get.application.configurations}")
    private String getApplicationConfigurationsUrl;

    /**
     * This method includes the business logic to retrieve application configurations.
     *
     * @param configurationType - The balance transfer request containing the request header.
     * @return - GetAppConfigResponse The response containing the application configurations.
     * @throws DomainException - If an error occurs during the API call.
     */
    public GetAppConfigResponse getConfigurations(String configurationType, RequestHeader requestHeader) throws DomainException {
        try {
            GetAppConfigRequest getAppConfigRequest = new GetAppConfigRequest();
            getAppConfigRequest.setRequestHeader(requestHeader);
            getAppConfigRequest.setRequestBody(new GetAppConfigRequestBody(Collections.singletonList(configurationType)));

            // Create the HTTP entity with the request body and headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<GetAppConfigRequest> requestEntity = new HttpEntity<>(getAppConfigRequest, headers);

            // Make the API call
            ResponseEntity<GetAppConfigResponse> responseEntity = restTemplate.exchange(getApplicationConfigurationsUrl, HttpMethod.POST, requestEntity, GetAppConfigResponse.class);

            // Return the response body
            return responseEntity.getBody();
        } catch (Exception e) {
            logError(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
            if (e instanceof DomainException) {
                throw (DomainException) e;
            }
            throw new DomainException(GET_APPLICATION_CONFIGURATIONS_ERROR.getDescription(), GET_APPLICATION_CONFIGURATIONS_ERROR.getCode());
        }
    }
}