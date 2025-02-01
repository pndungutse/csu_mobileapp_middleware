package com.dsu.hope_bank_app_middleware.accounts.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.accounts.dto.AccountDTO;
import com.dsu.hope_bank_app_middleware.accounts.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.accounts.response.AccountResponse;
import com.dsu.hope_bank_app_middleware.accounts.service.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AccountServiceImpl implements AccountService {

    static {
        SSLUtil.disableSslVerification();
    }

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Override
    public AccountResponse getAccountInformation(AccountRequest accountRequest) {
        System.out.println("Account request: "+accountRequest);
        // Map AccountRequest to AccountDTO
        AccountDTO accountDTO = mapToAccountDTO(accountRequest);
        System.out.println("AccountDTO: "+accountDTO);



        // Call the T24 endpoint
        String t24EndpointUrl = "http://192.168.2.229:8680/webservices/dsu/t24/transaction";

        String t24_base_url = dsuMobApp.getT24_base_url();

        HttpHeaders headers = new HttpHeaders();

//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json");
        headers.set("Sender-Reference", dsuMobApp.getT24_sender_reference());
        headers.set("Service-Source", dsuMobApp.getT24_service_source());
        headers.set("Token", dsuMobApp.getT24_token());
        headers.set("Token-Password", dsuMobApp.getT24_token_password());
        HttpEntity<AccountDTO> entity = new HttpEntity<>(accountDTO, headers);
//        logPayload(accountDTO);
        ResponseEntity<String> t24Response = restTemplate.exchange(t24_base_url, HttpMethod.POST, entity, String.class);


        logger.log(Level.INFO, t24Response.getBody());

        // Parse and map the T24 response to AccountResponse
        return mapToAccountResponse(t24Response.getBody());
    }

    private AccountDTO mapToAccountDTO(AccountRequest accountRequest) {
        AccountDTO dto = new AccountDTO();
        AccountDTO.Parameters params = new AccountDTO.Parameters();
        params.setTxnType(accountRequest.getTxnType());
        params.setAccountNumber(accountRequest.getAccountNumber());
        params.setCustomerId(accountRequest.getCustomerId());
        params.setLegalId(accountRequest.getLegalId());
        params.setUniqueTxnRef(UUID.randomUUID().toString());

        dto.setParameters(params);
        dto.setSequence(params.getUniqueTxnRef());
        return dto;
    }

    private AccountResponse mapToAccountResponse(String t24Response) {
        // Parse the response using ObjectMapper or another JSON library
        // Map necessary fields to AccountResponse
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(t24Response);
            JsonNode responseMessage = rootNode.path("responseMessage").path("result");

            AccountResponse response = new AccountResponse();
            response.setUniqueReference(rootNode.path("uniqueReference").asText());
            response.setServiceStatus(rootNode.path("serviceStatus").asText());
            response.setCustomerName(responseMessage.path("customerName").asText());

            List<AccountResponse.AccountDetails> accounts = new ArrayList<>();
            for (JsonNode accountNode : responseMessage.path("accounts")) {
                AccountResponse.AccountDetails accountDetails = new AccountResponse.AccountDetails();
                accountDetails.setAccountId(accountNode.path("accountId").asText());
                accountDetails.setLinkedLoan(accountNode.path("linkedLoan").asText());
                accountDetails.setLoanStatus(accountNode.path("loanStatus").asText());
                accountDetails.setLoanProduct(accountNode.path("loanProduct").asText());
                accountDetails.setAccountCategory(accountNode.path("accountCategory").asText());
                accountDetails.setAccountTitle(accountNode.path("accountTitle").asText());
                accountDetails.setAccountCurrency(accountNode.path("accountCurrency").asText());
                accountDetails.setAccountStatus(accountNode.path("accountStatus").asText());
                accounts.add(accountDetails);
            }
            response.setAccounts(accounts);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing T24 response", e);
        }
    }

}
