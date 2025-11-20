package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.request.SelfRegistrationParameters;
import com.dsu.hope_bank_app_middleware.request.SelfRegistrationRequest;
import com.dsu.hope_bank_app_middleware.response.SelfRegistrationResponse;
import com.dsu.hope_bank_app_middleware.service.SelfRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SelfRegistrationServiceImpl implements SelfRegistrationService {
    static {
        SSLUtil.disableSslVerification();
    }

    private static final Logger logger = Logger.getLogger(SelfRegistrationServiceImpl.class.getName());

    private final TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Override
    public SelfRegistrationResponse.Result makeSelfRegistration(SelfRegistrationRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 Base URL: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Self registration request: {0}", request);

        String uniqueRef = UUID.randomUUID().toString();  // Generate unique transaction reference

        // Create parameters object
        SelfRegistrationParameters parameters = new SelfRegistrationParameters(
                request.getPhoneNumber(),
                request.getAccountNumber(),
                request.getLegalId(),
                request.getChannel(),
                request.getIsRegistered(),
                dsuMobApp.getSelf_registration_txn_type(),
                uniqueRef
        );

        // Create request body
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "TRANSACTION");
        requestBody.put("parameters", objectMapper.convertValue(parameters, Map.class)); // Correct Serialization
        requestBody.put("sequence", uniqueRef);

        logger.log(Level.INFO, "Request Body: {0}", requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Sender-Reference", dsuMobApp.getT24_sender_reference());
        headers.set("Service-Source", dsuMobApp.getT24_service_source());
        headers.set("Token", dsuMobApp.getT24_token());
        headers.set("Token-Password", dsuMobApp.getT24_token_password());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Make API call
        ResponseEntity<SelfRegistrationResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, SelfRegistrationResponse.class
        );

        SelfRegistrationResponse selfRegistrationResponse = response.getBody();

        logger.log(Level.INFO, "Response from self registration: {0}", selfRegistrationResponse);
        assert selfRegistrationResponse != null;
        return selfRegistrationResponse.getResponseMessage().getResult();
    }
}
