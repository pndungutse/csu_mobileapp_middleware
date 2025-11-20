package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.request.*;
import com.dsu.hope_bank_app_middleware.response.LoanBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.LoanRepaymentResponse;
import com.dsu.hope_bank_app_middleware.service.LoanService;
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
public class LoanServiceImpl implements LoanService {
    static {
        SSLUtil.disableSslVerification();
    }

    private static final Logger logger = Logger.getLogger(LoanServiceImpl.class.getName());

    private final TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Override
    public LoanBalanceResponse.Result getLoanInfo(LoanInfoRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        LoanInfoParameters parameters = new LoanInfoParameters(
                request.getLoan(),
                uniqueRef,
                dsuMobApp.getLoan_balance_txn_type()
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
        ResponseEntity<LoanBalanceResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, LoanBalanceResponse.class
        );

        LoanBalanceResponse loanBalanceResponse = response.getBody();
        assert loanBalanceResponse != null;
        return loanBalanceResponse.getResponseMessage().getResult();
    }

    @Override
    public LoanRepaymentResponse.Result payLoanWithMomo(LoanRepaymentMomoRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Loan repayment with account request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        LoanRepaymentMomoParameters parameters = new LoanRepaymentMomoParameters(
                dsuMobApp.getLoan_repayment_momo_txn_type(),
                request.getLoan_account(),
                request.getAmount(),
                request.getTransaction_details(),
                uniqueRef
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
        ResponseEntity<LoanRepaymentResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, LoanRepaymentResponse.class
        );

        LoanRepaymentResponse loanRepaymentResponse = response.getBody();

        logger.log(Level.INFO, "Response: {0}", loanRepaymentResponse);
        assert loanRepaymentResponse != null;
        return loanRepaymentResponse.getResponseMessage().getResult();
    }

    @Override
    public LoanRepaymentResponse.Result payLoanWithAccount(LoanRepaymentAccountRequest request) {
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Loan repayment with account request: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

        LoanRepaymentAccountParameters parameters = new LoanRepaymentAccountParameters(
                dsuMobApp.getLoan_repayment_txn_type(),
                request.getLoan_account(),
                request.getAccount(),
                request.getTransaction_details(),
                request.getAmount(),
                uniqueRef
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
        ResponseEntity<LoanRepaymentResponse> response = restTemplate.exchange(
                t24BaseUrl, HttpMethod.POST, entity, LoanRepaymentResponse.class
        );

        LoanRepaymentResponse loanRepaymentResponse = response.getBody();

        logger.log(Level.INFO, "Response: {0}", loanRepaymentResponse);
        assert loanRepaymentResponse != null;
        return loanRepaymentResponse.getResponseMessage().getResult();
    }
}
