package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.dto.AccountDTO;
import com.dsu.hope_bank_app_middleware.dto.SingleAccountDTO;
import com.dsu.hope_bank_app_middleware.request.*;
import com.dsu.hope_bank_app_middleware.response.*;
import com.dsu.hope_bank_app_middleware.service.AccountService;
import com.dsu.hope_bank_app_middleware.utils.T24EnvironmentConfig;
import com.dsu.hope_bank_app_middleware.utils.T24EnvironmentResolver;
import com.dsu.hope_bank_app_middleware.utils.T24RequestBuilder;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AccountServiceImpl implements AccountService {

    static {
        SSLUtil.disableSslVerification();
    }

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    private final TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Override
    public AccountResponse getAccountInformation(AccountRequest accountRequest) {
        System.out.println("AccountRequest: "+accountRequest);
        accountRequest.setTxn_type(dsuMobApp.getAccount_information_txn_type());
        String uniqueRef = UUID.randomUUID().toString();

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("txn_type", accountRequest.getTxn_type());
        if (accountRequest.getAccountNumber() != null && !accountRequest.getAccountNumber().trim().isEmpty()) {
            parameters.put("accountNumber", accountRequest.getAccountNumber().trim());
        }
        if (accountRequest.getCustomerid() != null && !accountRequest.getCustomerid().trim().isEmpty()) {
            parameters.put("Customerid", accountRequest.getCustomerid().trim());
        }
        if (accountRequest.getLegalId() != null && !accountRequest.getLegalId().trim().isEmpty()) {
            parameters.put("legalId", accountRequest.getLegalId().trim());
        }
        parameters.put("unique_txn_ref", uniqueRef);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("action", "TRANSACTION");
        requestBody.put("parameters", parameters);
        requestBody.put("sequence", uniqueRef);

        T24EnvironmentConfig t24Config = T24EnvironmentResolver.resolve(dsuMobApp, accountRequest.getEnvironment());
        T24RequestBuilder.T24Request<Map<String, Object>> t24Request = T24RequestBuilder.build(
                t24Config.getUrl(),
                t24Config.getSenderReference(),
                t24Config.getServiceSource(),
                t24Config.getToken(),
                t24Config.getTokenPassword(),
                requestBody
        );

        logger.log(Level.INFO, "T24 account URL: {0}", t24Request.getUrl());
        try {
            logger.log(Level.INFO, "T24 account payload JSON: {0}", new ObjectMapper().writeValueAsString(requestBody));
        } catch (Exception e) {
            logger.log(Level.INFO, "T24 account payload fallback: {0}", requestBody);
        }
        ResponseEntity<String> t24Response = restTemplate.exchange(
                t24Request.getUrl(),
                HttpMethod.POST,
                t24Request.getEntity(),
                String.class
        );


        logger.log(Level.INFO, t24Response.getBody());

        // Parse and map the T24 response to AccountResponse
        return mapToAccountResponse(t24Response.getBody());
    }

    @Override
    public AccountBalanceResponse.Result getAccountBalance(AccountBalanceRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        // Create parameters object
        AccountBalanceParameters parameters = new AccountBalanceParameters(
                dsuMobApp.getAccount_balance_txn_type(),
                request.getAccount(),
                uniqueRef
        );

        logger.log(Level.INFO, "Account Balance request body {0}", parameters);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "TRANSACTION");
        requestBody.put("parameters", parameters);
        requestBody.put("sequence", uniqueRef);

        logger.log(Level.INFO, "Request Body: {0}", requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("Sender-Reference", dsuMobApp.getT24_sender_reference());
        headers.set("Service-Source", dsuMobApp.getT24_service_source());
        headers.set("Token", dsuMobApp.getT24_token());
        headers.set("Token-Password", dsuMobApp.getT24_token_password());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Make API call
        ResponseEntity<AccountBalanceResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, AccountBalanceResponse.class
        );

        AccountBalanceResponse accountBalanceResponse = response.getBody();

        logger.log(Level.INFO, "Response for balance {0}", accountBalanceResponse);
        assert accountBalanceResponse != null;
        return accountBalanceResponse.getResponseMessage().getResult();
    }

    @Override
    public List<MiniStatementResponse.Transaction> getMiniStatement(MiniStatementRequest request) {
        System.out.println("MiniStatement request: "+request);
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        String uniqueRef = uuidGenerator.generate().toString();

        // Create parameters object
        MiniStatementParameters parameters = new MiniStatementParameters(
                request.getAccount(),
                request.getNumberOfTransactions(),
                uniqueRef,
                dsuMobApp.getMini_statement_txn_type()
        );

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "TRANSACTION");
        requestBody.put("parameters", parameters);
        requestBody.put("sequence", uniqueRef);

        logger.log(Level.INFO, "Request Body: {0}", requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Sender-Reference", dsuMobApp.getT24_sender_reference());
        headers.set("Service-Source", dsuMobApp.getT24_service_source());
        headers.set("Token", dsuMobApp.getT24_token());
        headers.set("Token-Password", dsuMobApp.getT24_token_password());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<MiniStatementResponse> response = restTemplate.postForEntity(t24BaseUrl, entity, MiniStatementResponse.class);
        logger.log(Level.INFO, "Response: {0}", response);
//        List<MiniStatementResponse.Transaction> transactions = response.getBody().getResponseMessage().getResult().getTxn();
        assert response.getBody() != null;
        logger.log(Level.INFO, "Response: {0}", response.getBody().getResponseMessage().getResult().getTxn());
        return response.getBody().getResponseMessage().getResult().getTxn();
    }

    @Override
    public GenericResponse getSingleAccountInformation(GenericRequest genericRequest) {

        System.out.println("Generic request: "+genericRequest);
        // Map AccountRequest to AccountDTO
        SingleAccountDTO singleAccountDTO = mapToSingleAccountDTO(genericRequest);
        System.out.println("SingleAccountDTO: "+singleAccountDTO);

        String t24_base_url = dsuMobApp.getT24_base_url();

        HttpHeaders headers = new HttpHeaders();

//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json");
        headers.set("Sender-Reference", dsuMobApp.getT24_sender_reference());
        headers.set("Service-Source", dsuMobApp.getT24_service_source());
        headers.set("Token", dsuMobApp.getT24_token());
        headers.set("Token-Password", dsuMobApp.getT24_token_password());
        HttpEntity<SingleAccountDTO> entity = new HttpEntity<>(singleAccountDTO, headers);
        ResponseEntity<String> t24Response = restTemplate.exchange(t24_base_url, HttpMethod.POST, entity, String.class);


        logger.log(Level.INFO, t24Response.getBody());

        // Parse and map the T24 response to AccountResponse
        return mapToGenericResponse(t24Response.getBody(), genericRequest);
    }

    @Override
    public List<GenericResponse> getCurrencyList() {
        List<GenericResponse> currencyList = new ArrayList<>();

        currencyList.add(GenericResponse.builder().id("CFA").name("CFA").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("RWF").name("RWF").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("USD").name("USD").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("EUR").name("EUR").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("KES").name("KES").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("UGX").name("UGX").retCode("00").build());
        currencyList.add(GenericResponse.builder().id("cfx").name("cfx").retCode("00").build());
        // Add more currencies as needed

        return currencyList;
    }


    private AccountDTO mapToAccountDTO(AccountRequest accountRequest) {
        AccountDTO dto = new AccountDTO();
        AccountDTO.Parameters params = new AccountDTO.Parameters();
        params.setTxn_type(accountRequest.getTxn_type());
        params.setAccountNumber(accountRequest.getAccountNumber());
        params.setCustomerid(accountRequest.getCustomerid());
        params.setLegalId(accountRequest.getLegalId());
        params.setUnique_txn_ref(UUID.randomUUID().toString());

        dto.setParameters(params);
        dto.setSequence(params.getUnique_txn_ref());
        return dto;
    }

    private SingleAccountDTO mapToSingleAccountDTO(GenericRequest genericRequest) {
        SingleAccountDTO dto = new SingleAccountDTO();
        SingleAccountDTO.Parameters params = new SingleAccountDTO.Parameters();
//        params.setTxnType(accountRequest.getTxnType());
        params.setAccountNumber(genericRequest.getRequestId());
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

    private GenericResponse mapToGenericResponse(String t24Response, GenericRequest genericRequest) {
        // Parse the response using ObjectMapper or another JSON library
        // Map necessary fields to AccountResponse
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(t24Response);
            JsonNode responseMessage = rootNode.path("responseMessage").path("result");

            String RetCode = responseMessage.path("ret_code").asText();

            String RealID = "";

            if (RetCode.equalsIgnoreCase("0"))  {
                RealID = genericRequest.getRequestId();
            } else {
                RealID = "";
            }

            GenericResponse response = new GenericResponse();
            response.setId(RealID);
            response.setName(responseMessage.path("customerName").asText());
            response.setRetCode(responseMessage.path("ret_code").asText());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing T24 response", e);
        }
    }

}
