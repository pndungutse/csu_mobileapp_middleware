package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.request.*;
import com.dsu.hope_bank_app_middleware.response.AccountBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.TransferService;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TransferServiceImpl implements TransferService {

    static {
        SSLUtil.disableSslVerification();
    }

    private static final Logger logger = Logger.getLogger(TransferServiceImpl.class.getName());

    private final TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Override
    public TransferResponse.Result processInternalTransfer(InternalTransferRequest request) {
        AccountBalanceRequest accountBalanceRequest = new AccountBalanceRequest();
        accountBalanceRequest.setAccount(request.getCustomerAccount());
        AccountBalanceResponse.Result balanceResult = accountServiceImpl.getAccountBalance(accountBalanceRequest);
        
        // Parse amounts for comparison
        double transferAmount;
        double availableBalance;
        try {
            transferAmount = Double.parseDouble(request.getAmount());
            availableBalance = Double.parseDouble(balanceResult.getAvailable_balance());
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error parsing amount or balance: {0}", e.getMessage());
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Invalid amount or balance format");
            return errorResult;
        }
        
        // Check if transfer amount exceeds available balance
        if (transferAmount > availableBalance) {
            logger.log(Level.WARNING, "Insufficient balance. Available: {0}, Requested: {1}", 
                new Object[]{availableBalance, transferAmount});
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Insufficient balance. Available balance: " + availableBalance);

            return errorResult;
        }
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        InternalTransferParameters parameters = new InternalTransferParameters(
                request.getCustomerAccount(),
                request.getReceiverAccount(),
                request.getNarration(),
                request.getAmount(),
                request.getAmount_ccy(),
                uniqueRef,
                dsuMobApp.getInternal_transfer_txn_type()
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

        // Make API call
        ResponseEntity<TransferResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, TransferResponse.class
        );

        logger.log(Level.INFO, "Response: {0}", response);

//        TransferResponse.Result transferResponseResult = response.getBody().getResponseMessage().getResult();
        
        assert response.getBody() != null;
        return response.getBody().getResponseMessage().getResult();
    }

    @Override
    public TransferResponse.Result processBankToWalletTransfer(BankToWalletTransferRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        BankToWalletTransferParameters parameters = new BankToWalletTransferParameters(
                request.getCustomerAccount(),
                request.getAmount(),
                request.getNarration(),
                request.getNetwork_operator(),
                request.getPhone_number(),
                request.getCcy(),
                request.getCountry(),
                uniqueRef,
                dsuMobApp.getTransfer_bank_to_wallet_txn_type()
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

        // Make API call
        ResponseEntity<TransferResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, TransferResponse.class
        );

        logger.log(Level.INFO, "Response: {0}", response);

//        TransferResponse.Result transferResponseResult = response.getBody().getResponseMessage().getResult();

        assert response.getBody() != null;
        return response.getBody().getResponseMessage().getResult();
    }

    @Override
    public TransferResponse.Result processBAirtimeTopUp(AirtimeTopUpRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        AirtimeTopUpParameters parameters = new AirtimeTopUpParameters(
                request.getCustomer_account(),
                request.getPhone_number(),
                request.getNarration(),
                request.getAmount(),
                uniqueRef,
                dsuMobApp.getAirtime_top_up_txn_type()
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

        // Make API call
        ResponseEntity<TransferResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, TransferResponse.class
        );

        logger.log(Level.INFO, "Response: {0}", response);

//        TransferResponse.Result transferResponseResult = response.getBody().getResponseMessage().getResult();

        assert response.getBody() != null;
        return response.getBody().getResponseMessage().getResult();
    }
}
