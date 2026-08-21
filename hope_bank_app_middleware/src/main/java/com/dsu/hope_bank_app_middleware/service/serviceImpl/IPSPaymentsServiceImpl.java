package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.entity.BeneficiaryBank;
import com.dsu.hope_bank_app_middleware.entity.IpsQrCode;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.repository.BeneficiaryBankRepository;
import com.dsu.hope_bank_app_middleware.repository.IpsQrCodeRepository;
import com.dsu.hope_bank_app_middleware.repository.MessageFlowRepository;
import com.dsu.hope_bank_app_middleware.request.AccountBalanceRequest;
import com.dsu.hope_bank_app_middleware.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.*;
import com.dsu.hope_bank_app_middleware.response.AccountBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.AccountResponse;
import com.dsu.hope_bank_app_middleware.response.GenericDataResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsNameLookupResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrCreateResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrReadResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrStartOfPaymentResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.RequestToPayResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.RtpParsedTransaction;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.RtpPendingTransactionsResponse;
import com.dsu.hope_bank_app_middleware.response.QrCodeGenerateResult;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.IPSPaymentsService;
import com.dsu.hope_bank_app_middleware.service.QrCodeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class IPSPaymentsServiceImpl implements IPSPaymentsService {
    static {
        SSLUtil.disableSslVerification();
    }

    @Autowired
    private AccountServiceImpl accountService;

    private static final Logger logger = Logger.getLogger(IPSPaymentsServiceImpl.class.getName());

    private final TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DsuMobApp dsuMobApp;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private BeneficiaryBankRepository beneficiaryBankRepository;

    @Autowired
    private IpsQrCodeRepository ipsQrCodeRepository;

    @Autowired
    private MessageFlowRepository messageFlowRepository;

    @Override
    public GenericResponse getIpsAccountInformation(GenericRequest genericRequest) {
        logger.log(Level.SEVERE, "Request for getting ips account info: {0}", genericRequest);

        String url = dsuMobApp.getIps_name_lookup_url();
        if (url == null || url.isEmpty()) {
            return null;
        }

        logger.log(Level.SEVERE, "Url to get ips account info: {0}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.log(Level.SEVERE, "Headers on ips account info: {0}", headers);

        IpsNameLookupRequest ipsNameLookupRequest = new IpsNameLookupRequest("MOBILE", genericRequest.getRequestId());

        logger.log(Level.SEVERE, "IpsNameLookupRequest: {0}", ipsNameLookupRequest);

        HttpEntity<IpsNameLookupRequest> entity = new HttpEntity<>(ipsNameLookupRequest, headers);

        try {
            ResponseEntity<IpsNameLookupResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    IpsNameLookupResponse.class
            );
            logger.log(Level.SEVERE, "Response : {0}", response);
            IpsNameLookupResponse body = response.getBody();

            logger.log(Level.SEVERE, "Response body: {0}", body);

            assert body != null;
            return mapToGenericResponse(body, genericRequest);
        } catch (Exception e) {
            // log e and return empty or rethrow as needed
            return null;
        }
    }

    @Override
    public TransferResponse.Result processTransferIpsOther(IPSTransferRequest ipsTransferRequest) {
        String purpPrtry = "001";
        logger.log(Level.INFO, "Full request from endpoint {0}", ipsTransferRequest);
        AccountBalanceRequest accountBalanceRequest = new AccountBalanceRequest();
        accountBalanceRequest.setAccount(ipsTransferRequest.getCustomer_account());
        AccountBalanceResponse.Result balanceResult = accountService.getAccountBalance(accountBalanceRequest);

        // Parse amounts for comparison
        double transferAmount;
        double availableBalance;
        try {
            transferAmount = Double.parseDouble(ipsTransferRequest.getAmount());
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
        logger.log(Level.INFO, "Account Info request for IPS: {0}", ipsTransferRequest);

        String uniqueRef = uuidGenerator.generate().toString();

        AccountRequest accountRequest = new AccountRequest("AccountInformation", ipsTransferRequest.getCustomer_account(), "", "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

        GenericRequest genericRequest = GenericRequest.builder()
                .requestId(ipsTransferRequest.getPhone_number())
                .build();
        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);

        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);
        if (genericResponse == null) {
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("IPS account lookup failed");
            return errorResult;
        }
        if (genericResponse.getRetCode() != null
                && genericResponse.getRetCode().equalsIgnoreCase("TURABIBI")
                && genericResponse.getOtherInfo() != null
                && genericResponse.getOtherInfo().equalsIgnoreCase("ACCT")) {
            purpPrtry = "005";
        }
        if (genericResponse.getOtherInfo() != null && genericResponse.getOtherInfo().equalsIgnoreCase("WLLT")) {
            purpPrtry = "004";
        }

        String isCreditorSwift = genericResponse.getIsDebtorSwift();
        if (isEmpty(isCreditorSwift)) {
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Could not determine isCreditorSwift from IPS name lookup (expected servicer.bic or servicer.memberId)");
            return errorResult;
        }

        IPSTransferRequest parameters = new IPSTransferRequest(
                ipsTransferRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                ipsTransferRequest.getAmount(),
                genericResponse.getRetCode(),
                genericResponse.getName(),
                genericResponse.getId(),
                purpPrtry,
                isCreditorSwift,
                uniqueRef,
                dsuMobApp.getBank_ips()
        );

        logger.log(Level.INFO, "Parameters on IPS: {0}", parameters);

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
    public TransferResponse.Result processTransferIpsPayQr(IPSPayQrRequest ipsPayQrRequest) {

        ipsPayQrRequest.setQr_reference(ipsPayQrRequest.getQr_reference());
        logger.log(Level.INFO, "Full request from endpoint of IPS PAY QR {0}", ipsPayQrRequest);
        AccountBalanceRequest accountBalanceRequest = new AccountBalanceRequest();
        accountBalanceRequest.setAccount(ipsPayQrRequest.getCustomer_account());
        AccountBalanceResponse.Result balanceResult = accountService.getAccountBalance(accountBalanceRequest);

        // Parse amounts for comparison
        double transferAmount;
        double availableBalance;
        try {
            transferAmount = Double.parseDouble(ipsPayQrRequest.getAmount());
            availableBalance = Double.parseDouble(balanceResult.getAvailable_balance());
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error parsing amount or balance: {0}", e.getMessage());
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Invalid amount or balance format");
            return errorResult;
        }

        // Check if transfer amount exceeds available balance
//        if (transferAmount > availableBalance) {
//            logger.log(Level.WARNING, "Insufficient balance. Available: {0}, Requested: {1}",
//                    new Object[]{availableBalance, transferAmount});
//            TransferResponse.Result errorResult = new TransferResponse.Result();
//            errorResult.setRet_code("400");
//            errorResult.setRet_message("Insufficient balance. Available balance: " + availableBalance);
//
//            return errorResult;
//        }
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request for IPS PAY QR: {0}", ipsPayQrRequest);

        String uniqueRef = uuidGenerator.generate().toString();

        AccountRequest accountRequest = new AccountRequest("AccountInformation", ipsPayQrRequest.getCustomer_account(), "", "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

//        GenericRequest genericRequest = GenericRequest.builder()
//                .requestId(ipsPayQrRequest.getQr_reference())
//                .build();
//        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);
//
//        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
//        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);



//        GenericRequest genericRequest = GenericRequest.builder().requestId(ipsPayQrRequest.getQr_reference()).build();
//        GenericDataResponse genericDataResponse = getIpsQrCodeInfo(genericRequest);
//        logger.log(Level.INFO, "GenericDataResponse for qr lookup info: {0}", genericDataResponse);

//        call qr lookup information
        GenericRequest genericRequest = GenericRequest.builder().requestId(ipsPayQrRequest.getQr_reference()).build();
        GenericDataResponse<IpsQrReadResponse> genericDataResponse = getIpsQrCodeInfo(genericRequest);
        logger.log(Level.INFO, "GenericDataResponse for qr lookup info: {0}", genericDataResponse);

        IpsQrStartOfPaymentRequest ipsQrStartOfPaymentRequest = new IpsQrStartOfPaymentRequest();
        ipsQrStartOfPaymentRequest.setUetr(genericDataResponse.getData().getUetr());
        ipsQrStartOfPaymentRequest.setAmountType(genericDataResponse.getData().getAmountType());
//        ipsQrStartOfPaymentRequest.setSum(genericDataResponse.getData().getSum());
        ipsQrStartOfPaymentRequest.setSum(ipsPayQrRequest.getAmount());
//        call start of payment qr
//        IpsQrStartOfPaymentRequest ipsQrStartOfPaymentRequest = IpsQrStartOfPaymentRequest.builder().uetr(genericDataResponse.getData().getUetr()).sum(genericDataResponse.getData().getSum()).amountType(genericDataResponse.getData().getAmountType()).build();
        logger.log(Level.INFO, "Ips Qr Start Of Payment Request : {0}", ipsQrStartOfPaymentRequest);

        GenericDataResponse<IpsQrStartOfPaymentResponse> ipsQrStartOfPaymentResponseGenericDataResponse = startIpsQrStartOfPayment(ipsQrStartOfPaymentRequest);
        logger.log(Level.INFO, "Ips Qr Start Of Payment Response : {0}", ipsQrStartOfPaymentResponseGenericDataResponse);

        String amountType = ipsQrStartOfPaymentResponseGenericDataResponse.getData().getAmountType();
        String postingAmount = "";
        if (amountType.equalsIgnoreCase("Fixed")) {
            postingAmount = ipsQrStartOfPaymentResponseGenericDataResponse.getData().getSum();
        } else {
            postingAmount = ipsPayQrRequest.getAmount();
        }

        CreditorAgentXml creditorAgent = parseCreditorAgentXml(genericDataResponse.getData().getXmlCreditorBic());
        if (creditorAgent == null) {
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Could not determine isCreditorSwift from QR xmlCreditorBic (expected BICFI or MmbId)");
            return errorResult;
        }

        IPSPayQrRequest parameters = new IPSPayQrRequest(
                ipsPayQrRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                postingAmount,
                creditorAgent.agentId,
                genericDataResponse.getData().getCreditorName(),
                genericDataResponse.getData().getCreditorAccount(),
                genericDataResponse.getData().getUetr(),
                "",
                creditorAgent.isCreditorSwift,
                uniqueRef,
                dsuMobApp.getBank_ips_pay_qr()
        );

        logger.log(Level.INFO, "Parameters on IPS: {0}", parameters);

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
    public TransferResponse.Result processTransferIpsPayQrFixedDynamic(IPSPayQrRequest ipsPayQrRequest) {
        ipsPayQrRequest.setQr_reference(ipsPayQrRequest.getQr_reference());
        logger.log(Level.INFO, "Full request from endpoint of IPS PAY QR {0}", ipsPayQrRequest);
        AccountBalanceRequest accountBalanceRequest = new AccountBalanceRequest();
        accountBalanceRequest.setAccount(ipsPayQrRequest.getCustomer_account());
        AccountBalanceResponse.Result balanceResult = accountService.getAccountBalance(accountBalanceRequest);

        // Parse amounts for comparison
        double transferAmount;
        double availableBalance;
        try {
            transferAmount = Double.parseDouble(ipsPayQrRequest.getAmount());
            availableBalance = Double.parseDouble(balanceResult.getAvailable_balance());
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Error parsing amount or balance: {0}", e.getMessage());
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Invalid amount or balance format");
            return errorResult;
        }

        // Check if transfer amount exceeds available balance
//        if (transferAmount > availableBalance) {
//            logger.log(Level.WARNING, "Insufficient balance. Available: {0}, Requested: {1}",
//                    new Object[]{availableBalance, transferAmount});
//            TransferResponse.Result errorResult = new TransferResponse.Result();
//            errorResult.setRet_code("400");
//            errorResult.setRet_message("Insufficient balance. Available balance: " + availableBalance);
//
//            return errorResult;
//        }
        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);
        logger.log(Level.INFO, "Account Info request for IPS PAY QR: {0}", ipsPayQrRequest);

        String uniqueRef = uuidGenerator.generate().toString();

        AccountRequest accountRequest = new AccountRequest("AccountInformation", ipsPayQrRequest.getCustomer_account(), "", "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

//        GenericRequest genericRequest = GenericRequest.builder()
//                .requestId(ipsPayQrRequest.getQr_reference())
//                .build();
//        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);
//
//        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
//        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);



//        GenericRequest genericRequest = GenericRequest.builder().requestId(ipsPayQrRequest.getQr_reference()).build();
//        GenericDataResponse genericDataResponse = getIpsQrCodeInfo(genericRequest);
//        logger.log(Level.INFO, "GenericDataResponse for qr lookup info: {0}", genericDataResponse);

//        call qr lookup information
        GenericRequest genericRequest = GenericRequest.builder().requestId(ipsPayQrRequest.getQr_reference()).build();
        GenericDataResponse<IpsQrReadResponse> genericDataResponse = getIpsQrCodeInfo(genericRequest);
        logger.log(Level.INFO, "GenericDataResponse for qr lookup info: {0}", genericDataResponse);

//        IpsQrStartOfPaymentRequest ipsQrStartOfPaymentRequest = new IpsQrStartOfPaymentRequest();
//        ipsQrStartOfPaymentRequest.setUetr(genericDataResponse.getData().getUetr());
//        ipsQrStartOfPaymentRequest.setAmountType(genericDataResponse.getData().getAmountType());
////        ipsQrStartOfPaymentRequest.setSum(genericDataResponse.getData().getSum());
//        ipsQrStartOfPaymentRequest.setSum(ipsPayQrRequest.getAmount());
////        call start of payment qr
////        IpsQrStartOfPaymentRequest ipsQrStartOfPaymentRequest = IpsQrStartOfPaymentRequest.builder().uetr(genericDataResponse.getData().getUetr()).sum(genericDataResponse.getData().getSum()).amountType(genericDataResponse.getData().getAmountType()).build();
//        logger.log(Level.INFO, "Ips Qr Start Of Payment Request : {0}", ipsQrStartOfPaymentRequest);
//
//        GenericDataResponse<IpsQrStartOfPaymentResponse> ipsQrStartOfPaymentResponseGenericDataResponse = startIpsQrStartOfPayment(ipsQrStartOfPaymentRequest);
//        logger.log(Level.INFO, "Ips Qr Start Of Payment Response : {0}", ipsQrStartOfPaymentResponseGenericDataResponse);

//        String amountType = ipsQrStartOfPaymentResponseGenericDataResponse.getData().getAmountType();
        String amountType = genericDataResponse.getData().getAmountType();
        String postingAmount = "";
        if (amountType.equalsIgnoreCase("Fixed")) {
            postingAmount = genericDataResponse.getData().getSum();
            String requestedAmount = ipsPayQrRequest.getAmount();
            try {
                double fixedSum = Double.parseDouble(postingAmount);
                double requestSum = Double.parseDouble(requestedAmount);
                if (Double.compare(fixedSum, requestSum) != 0) {
                    logger.log(Level.WARNING,
                            "Fixed QR amount mismatch. QR sum: {0}, Requested: {1}",
                            new Object[]{postingAmount, requestedAmount});
                    TransferResponse.Result errorResult = new TransferResponse.Result();
                    errorResult.setRet_code("400");
                    errorResult.setRet_message(
                            "Amount does not match fixed QR amount.");
                    return errorResult;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Error comparing fixed QR amount: {0}", e.getMessage());
                TransferResponse.Result errorResult = new TransferResponse.Result();
                errorResult.setRet_code("400");
                errorResult.setRet_message("Invalid fixed QR amount or request amount format");
                return errorResult;
            }
        } else {
            postingAmount = ipsPayQrRequest.getAmount();
        }

        CreditorAgentXml creditorAgent = parseCreditorAgentXml(genericDataResponse.getData().getXmlCreditorBic());
        if (creditorAgent == null) {
            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("400");
            errorResult.setRet_message("Could not determine isCreditorSwift from QR xmlCreditorBic (expected BICFI or MmbId)");
            return errorResult;
        }

        IPSPayQrRequest parameters = new IPSPayQrRequest(
                ipsPayQrRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                postingAmount,
                creditorAgent.agentId,
                genericDataResponse.getData().getCreditorName(),
                genericDataResponse.getData().getCreditorAccount(),
                genericDataResponse.getData().getUetr(),
                "",
                creditorAgent.isCreditorSwift,
                uniqueRef,
                dsuMobApp.getBank_ips_pay_qr()
        );

        logger.log(Level.INFO, "Parameters on IPS: {0}", parameters);

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
    public GenericResponse getIpsQrInformation(GenericRequest genericRequest) {
        logger.log(Level.SEVERE, "Request for getting ips qr info: {0}", genericRequest);
        String url = dsuMobApp.getIps_name_lookup_url();
        if (url == null || url.isEmpty()) {
            return null;
        }

        logger.log(Level.SEVERE, "Url to get ips qr info: {0}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);



        logger.log(Level.SEVERE, "Headers on ips qr info: {0}", headers);

        IpsNameLookupRequest ipsNameLookupRequest = new IpsNameLookupRequest("MOBILE", genericRequest.getRequestId());

        logger.log(Level.SEVERE, "IpsNameLookupRequest: {0}", ipsNameLookupRequest);

        HttpEntity<IpsNameLookupRequest> entity = new HttpEntity<>(ipsNameLookupRequest, headers);

        try {
            ResponseEntity<IpsNameLookupResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    IpsNameLookupResponse.class
            );
            logger.log(Level.SEVERE, "Response : {0}", response);
            IpsNameLookupResponse body = response.getBody();

            logger.log(Level.SEVERE, "Response body: {0}", body);

            assert body != null;
            return mapToGenericResponse(body, genericRequest);
        } catch (Exception e) {
            // log e and return empty or rethrow as needed
            return null;
        }
    }

    @Override
    public GenericDataResponse<IpsQrReadResponse> getIpsQrCodeInfo(GenericRequest genericRequest) {
        logger.log(Level.INFO, "Request for IPS QR read: {0}", genericRequest);

        String qrUrl = genericRequest != null && genericRequest.getRequestId() != null
                ? genericRequest.getRequestId().trim()
                : null;
        if (qrUrl == null || qrUrl.isEmpty()) {
            return GenericDataResponse.<IpsQrReadResponse>builder()
                    .retCode("400")
                    .message("request_id (QR URL) is required")
                    .data(null)
                    .build();
        }

        String readUrl = dsuMobApp.getIps_qr_read_url();
        if (readUrl == null || readUrl.trim().isEmpty()) {
            return GenericDataResponse.<IpsQrReadResponse>builder()
                    .retCode("500")
                    .message("IPS QR read URL is not configured (dsumobapp.ips_qr_read_url)")
                    .data(null)
                    .build();
        }

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("qrCodeUrl", qrUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<IpsQrReadResponse> response = restTemplate.exchange(
                    readUrl,
                    HttpMethod.POST,
                    entity,
                    IpsQrReadResponse.class
            );

            IpsQrReadResponse body = response.getBody();
            logger.log(Level.INFO, "IPS QR response body: {0}", body);
            logger.log(Level.INFO, "IPS QR read HTTP status: {0}", response.getStatusCode());

            return GenericDataResponse.<IpsQrReadResponse>builder()
                    .retCode("00")
                    .message("OK")
                    .data(body)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "IPS QR read failed: {0}", e.getMessage());
            return GenericDataResponse.<IpsQrReadResponse>builder()
                    .retCode("500")
                    .message(e.getMessage() != null ? e.getMessage() : "IPS QR read failed")
                    .data(null)
                    .build();
        }
    }

    @Override
    public GenericDataResponse<TransferResponse.Result> createIpsQrCode(IpsQrCreateRequest request) {
        logger.log(Level.INFO, "IPS QR create request: {0}", request);

        if (request == null
                || isEmpty(request.getQrType())
//                || isEmpty(request.getCreditorName())
                || isEmpty(request.getCreditorAccount())
                || isEmpty(request.getMemberId())) {
            return qrCreateError("400", "qr_type, creditor_name, creditor_account, and member_id are required");
        }

        String createdByAccount = request.getCreditorAccount().trim();
        String createdByCustomerNumber = resolveCustomerNumberForAccount(createdByAccount);
        String createdByCustomerName = resolveCustomerNameForAccount(createdByAccount);
        request.setCreditorName(createdByCustomerName);
        String qrType = request.getQrType().trim().toUpperCase();
        boolean isDynamic = "DYNM".equals(qrType);
        boolean isStatic = "STAT".equals(qrType);
        if (!isDynamic && !isStatic) {
            return qrCreateError("400", "qr_type must be STAT or DYNM");
        }
        if (isDynamic && isEmpty(request.getSum())) {
            return qrCreateError("400", "sum is required when qr_type is DYNM");
        }

        String createUrl = dsuMobApp.getIps_qr_create_url();
        if (createUrl == null || createUrl.trim().isEmpty()) {
            return qrCreateError("500", "IPS QR create URL is not configured (dsumobapp.ips_qr_create_url)");
        }

        String currency = isEmpty(request.getCurrency()) ? "BIF" : request.getCurrency().trim();
        String agentIdType = isEmpty(request.getAgentIdType()) ? "memberId" : request.getAgentIdType().trim();
        int imageSize = request.getSize() != null ? request.getSize() : 300;

        Map<String, Object> gatewayBody = buildIpsQrCreatePayload(request, qrType, isDynamic, currency, agentIdType, createdByCustomerName);
        logger.log(Level.INFO, "IPS QR create gateway payload: {0}", gatewayBody);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(gatewayBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    createUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String rawBody = response.getBody();
            logger.log(Level.INFO, "IPS QR create HTTP status: {0}", response.getStatusCode());
            logger.log(Level.INFO, "IPS QR create raw response: {0}", rawBody);

            if (rawBody == null || rawBody.trim().isEmpty()) {
                return qrCreateError("500", "IPS QR create returned empty body");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            IpsQrCreateResponse gatewayMapped = new IpsQrCreateResponse();
            try {
                root = mapper.readTree(rawBody);
                IpsQrCreateResponse mapped = mapper.treeToValue(root, IpsQrCreateResponse.class);
                if (mapped != null) {
                    gatewayMapped = mapped;
                }
            } catch (Exception parseEx) {
                String maybeUrl = rawBody.trim().replace("\"", "");
                if (maybeUrl.startsWith("http")) {
                    gatewayMapped.setQrAsText(maybeUrl);
                    gatewayMapped.setQrCodeUrl(maybeUrl);
                } else {
                    return qrCreateError("500", "IPS QR create returned non-JSON body: " + truncate(rawBody, 300));
                }
            }

            if (isEmpty(gatewayMapped.getQrCodeUrl()) && !isEmpty(gatewayMapped.getQrAsText())) {
                gatewayMapped.setQrCodeUrl(gatewayMapped.getQrAsText().trim());
            }
            if (isEmpty(gatewayMapped.getUetr()) && !isEmpty(gatewayMapped.getQrExtensionUUID())) {
                gatewayMapped.setUetr(gatewayMapped.getQrExtensionUUID());
            }
            if (isEmpty(gatewayMapped.getExtractedUuid()) && !isEmpty(gatewayMapped.getQrHeaderUUID())) {
                gatewayMapped.setExtractedUuid(gatewayMapped.getQrHeaderUUID());
            }

            String qrCodeUrl = !isEmpty(gatewayMapped.getQrCodeUrl())
                    ? gatewayMapped.getQrCodeUrl().trim()
                    : (root != null ? findQrUrlInJson(root) : null);

            if (isEmpty(qrCodeUrl)) {
                return qrCreateError("500",
                        "IPS QR create response did not include qrAsText/qrCodeUrl. Raw: " + truncate(rawBody, 300));
            }

            String fileBase = request.getCreditorName()
                    + "-" + request.getCreditorAccount()
                    + "-" + request.getMemberId()
                    + "-" + qrType;

            // Prefer gateway-provided PNG (qrImageBase64 / qrAsImage); fall back to local generation only if missing
            String gatewayImageBase64 = resolveGatewayQrImageBase64(gatewayMapped, root);
            QrCodeGenerateResult imageResult;
            if (!isEmpty(gatewayImageBase64)) {
                imageResult = qrCodeService.saveBase64Png(gatewayImageBase64, fileBase);
                logger.log(Level.INFO, "Saved QR image from gateway base64");
            } else {
                logger.log(Level.WARNING, "Gateway did not return QR image; generating locally from URL");
                imageResult = qrCodeService.generateAndSaveQrPng(qrCodeUrl, imageSize, fileBase);
            }

            String ourReference = !isEmpty(gatewayMapped.getQrHeaderUUID())
                    ? gatewayMapped.getQrHeaderUUID()
                    : gatewayMapped.getExtractedUuid();

            TransferResponse.Result result = new TransferResponse.Result();
            result.setRet_code("00");
            result.setRet_message("QR code created successfully");
            result.setOur_reference(ourReference);
            result.setAmount(isDynamic ? request.getSum() : null);
            result.setFee_amount(null);
            result.setAccount_balance(null);

            result.setQrCodeUrl(qrCodeUrl);
            result.setQrType(qrType);
            result.setCreditorName(request.getCreditorName());
            result.setCreditorAccount(request.getCreditorAccount());
            result.setMemberId(request.getMemberId());
            result.setCurrency(currency);
            result.setUetr(
                    !isEmpty(gatewayMapped.getQrExtensionUUID())
                            ? gatewayMapped.getQrExtensionUUID()
                            : gatewayMapped.getUetr());
            result.setQrHeaderUUID(gatewayMapped.getQrHeaderUUID());
            result.setFileName(imageResult.getFileName());
            result.setSavedFilePath(imageResult.getSavedFilePath());
            if (!isEmpty(gatewayImageBase64)) {
                result.setQrImageBase64(stripDataUriPrefix(gatewayImageBase64));
            }

            IpsQrCode savedQr = saveCreatedIpsQrCode(
                    request,
                    qrType,
                    currency,
                    agentIdType,
                    isDynamic,
                    qrCodeUrl,
                    gatewayMapped,
                    ourReference,
                    imageResult,
                    result.getQrImageBase64(),
                    createdByAccount,
                    createdByCustomerNumber
            );
            logger.log(Level.INFO, "IPS QR code saved to DB with id: {0}", savedQr != null ? savedQr.getId() : null);

            return GenericDataResponse.<TransferResponse.Result>builder()
                    .retCode(ourReference != null ? ourReference : "00")
                    .message("QR code created successfully")
                    .data(result)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "IPS QR create failed: {0}", e.getMessage());
            return qrCreateError("500", e.getMessage() != null ? e.getMessage() : "IPS QR create failed");
        }
    }

    @Override
    public GenericDataResponse<List<IpsQrCode>> getCreatedIpsQrCodes(GenericRequest request) {
        logger.log(Level.INFO, "Get created IPS QR codes request: {0}", request);

        if (request == null || isEmpty(request.getRequestId())) {
            return GenericDataResponse.<List<IpsQrCode>>builder()
                    .retCode("400")
                    .message("request_id (customer number) is required")
                    .data(null)
                    .build();
        }

        String customerNumber = request.getRequestId().trim();
        List<IpsQrCode> qrCodes = ipsQrCodeRepository
                .findByCreatedByCustomerNumberAndStatusOrderByCreatedDateDesc(customerNumber, Status.ACTIVE);

        return GenericDataResponse.<List<IpsQrCode>>builder()
                .retCode("00")
                .message(qrCodes.isEmpty()
                        ? "No QR codes found for customer number: " + customerNumber
                        : "QR codes retrieved successfully")
                .data(qrCodes)
                .build();
    }

    /**
     * Prefer top-level qrImageBase64 / data URI, then nested qrCreate.body.qrAsImage.
     */
    private String resolveGatewayQrImageBase64(IpsQrCreateResponse mapped, JsonNode root) {
        if (mapped != null && !isEmpty(mapped.getQrImageBase64())) {
            return mapped.getQrImageBase64().trim();
        }
        if (mapped != null && !isEmpty(mapped.getQrImageDataUri())) {
            return mapped.getQrImageDataUri().trim();
        }
        if (mapped != null && mapped.getQrCreate() != null && !isEmpty(mapped.getQrCreate().getBody())) {
            try {
                JsonNode bodyNode = new ObjectMapper().readTree(mapped.getQrCreate().getBody());
                if (bodyNode.hasNonNull("qrAsImage")) {
                    return bodyNode.get("qrAsImage").asText();
                }
                if (bodyNode.hasNonNull("qrImageBase64")) {
                    return bodyNode.get("qrImageBase64").asText();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not parse qrCreate.body for image: {0}", e.getMessage());
            }
        }
        if (root != null) {
            String found = findQrImageBase64InJson(root);
            if (!isEmpty(found)) {
                return found;
            }
        }
        return null;
    }

    private String findQrImageBase64InJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            String[] keys = {"qrImageBase64", "qrAsImage", "qrImageDataUri"};
            for (String key : keys) {
                if (node.hasNonNull(key)) {
                    String value = node.get(key).asText();
                    if (!isEmpty(value) && (value.startsWith("iVBOR") || value.startsWith("data:image"))) {
                        return value;
                    }
                    // Long base64 without checking prefix
                    if (!isEmpty(value) && value.length() > 100) {
                        return value;
                    }
                }
            }
            java.util.Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode child = entry.getValue();
                if (child != null && child.isTextual()
                        && ("body".equals(entry.getKey()))
                        && child.asText().trim().startsWith("{")) {
                    try {
                        String nested = findQrImageBase64InJson(new ObjectMapper().readTree(child.asText()));
                        if (!isEmpty(nested)) {
                            return nested;
                        }
                    } catch (Exception ignored) {
                        // continue
                    }
                }
                String found = findQrImageBase64InJson(child);
                if (!isEmpty(found)) {
                    return found;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                String found = findQrImageBase64InJson(child);
                if (!isEmpty(found)) {
                    return found;
                }
            }
        }
        return null;
    }

    private String stripDataUriPrefix(String base64OrDataUri) {
        if (base64OrDataUri == null) {
            return null;
        }
        String value = base64OrDataUri.trim();
        int comma = value.indexOf(',');
        if (value.startsWith("data:") && comma > 0) {
            return value.substring(comma + 1);
        }
        return value;
    }

    private GenericDataResponse<TransferResponse.Result> qrCreateError(String code, String message) {
        TransferResponse.Result errorResult = new TransferResponse.Result();
        errorResult.setRet_code(code);
        errorResult.setRet_message(message);
        return GenericDataResponse.<TransferResponse.Result>builder()
                .retCode(code)
                .message(message)
                .data(errorResult)
                .build();
    }

    private Map<String, Object> buildIpsQrCreatePayload(
            IpsQrCreateRequest request,
            String qrType,
            boolean isDynamic,
            String currency,
            String agentIdType,
            String createdByCustomerName
    ) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("qrType", qrType);
        header.put("amountType", isDynamic ? "Fixed" : "Free");
        header.put("currency", currency);
        header.put("pmtContext", "m");
        header.put("isoVer", 1);

        Map<String, Object> creditorAccount = new LinkedHashMap<>();
        creditorAccount.put("other", request.getCreditorAccount().trim());

        Map<String, Object> creditorAgent = new LinkedHashMap<>();
        creditorAgent.put(agentIdType, request.getMemberId().trim());

        Map<String, Object> extension = new LinkedHashMap<>();
        if (isDynamic) {
            Map<String, Object> ttl = new LinkedHashMap<>();
            ttl.put("length", request.getTtlLength() != null ? request.getTtlLength() : 15);
            ttl.put("units", isEmpty(request.getTtlUnits()) ? "mm" : request.getTtlUnits().trim());
            extension.put("ttl", ttl);

            Map<String, Object> amount = new LinkedHashMap<>();
            amount.put("sum", request.getSum().trim());
            amount.put("currency", currency);
            extension.put("amount", amount);
        }
//        extension.put("creditorName", request.getCreditorName().trim());
        extension.put("creditorName", createdByCustomerName);
        extension.put("creditorAccount", creditorAccount);
        extension.put("creditorAgent", creditorAgent);
        extension.put("e2e", "NOTPROVIDED");
        extension.put("bankOpCode", "PMNI");
        extension.put("ttc", "007");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("header", header);
        payload.put("extension", extension);
        return payload;
    }

    /**
     * Recursively searches gateway JSON for a QR URL under common field names.
     */
    private String findQrUrlInJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            String text = node.asText().trim();
            if (looksLikeQrUrl(text)) {
                return text;
            }
            // Nested JSON string (e.g. qrCreate.body)
            if (text.startsWith("{") || text.startsWith("[")) {
                try {
                    return findQrUrlInJson(new ObjectMapper().readTree(text));
                } catch (Exception ignored) {
                    return null;
                }
            }
            return null;
        }
        if (node.isObject()) {
            String[] preferredKeys = {
                    "qrAsText", "qrCodeUrl", "qr_code_url", "qrUrl", "qr_url", "qrcodeUrl", "QRCodeUrl", "url"
            };
            for (String key : preferredKeys) {
                if (node.has(key)) {
                    String found = findQrUrlInJson(node.get(key));
                    if (!isEmpty(found)) {
                        return found;
                    }
                }
            }
            java.util.Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String found = findQrUrlInJson(entry.getValue());
                if (!isEmpty(found)) {
                    return found;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                String found = findQrUrlInJson(child);
                if (!isEmpty(found)) {
                    return found;
                }
            }
        }
        return null;
    }

    private boolean looksLikeQrUrl(String value) {
        if (value == null) {
            return false;
        }
        String v = value.trim();
        return v.startsWith("http://") || v.startsWith("https://");
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max) + "...";
    }

    private String resolveCreatedQrUrl(IpsQrCreateResponse body) {
        if (body.getQrCodeUrl() != null && !body.getQrCodeUrl().trim().isEmpty()) {
            return body.getQrCodeUrl().trim();
        }
        // Some gateways only put the URL inside qrCreate.body JSON
        if (body.getQrCreate() != null && body.getQrCreate().getBody() != null) {
            try {
                JsonNode node = new ObjectMapper().readTree(body.getQrCreate().getBody());
                String found = findQrUrlInJson(node);
                if (!isEmpty(found)) {
                    return found;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not parse qrCreate.body for qrCodeUrl: {0}", e.getMessage());
            }
        }
        if (body.getQrCreate() != null && body.getQrCreate().getUrl() != null
                && looksLikeQrUrl(body.getQrCreate().getUrl())) {
            return body.getQrCreate().getUrl().trim();
        }
        return null;
    }

    @Override
    public GenericDataResponse<IpsQrStartOfPaymentResponse> startIpsQrStartOfPayment(IpsQrStartOfPaymentRequest request) {
        logger.log(Level.INFO, "IPS QR start-of-payment request: {0}", request);

        if (request == null
                || isEmpty(request.getUetr())
                || isEmpty(request.getAmountType())
                || isEmpty(request.getSum())) {
            return GenericDataResponse.<IpsQrStartOfPaymentResponse>builder()
                    .retCode("400")
                    .message("uetr, amountType, and sum are required")
                    .data(null)
                    .build();
        }

//        IpsQrStartOfPaymentResponse.StartOfPaymentMeta meta = new IpsQrStartOfPaymentResponse.StartOfPaymentMeta();
//        meta.setStatusCode(200);
//        meta.setBody("{\"documentToken\":\"dummy.document.token\",\"expires_in\":10800}");
//        meta.setContentType("application/json");
//        meta.setRequestId("66b7067d-f9ae-4cba-80fe-c6c84444003b");
//        meta.setUrl("https://example.com/dummy/ips/qr/start-of-payment");
//
//        IpsQrStartOfPaymentResponse dummy = new IpsQrStartOfPaymentResponse();
//        dummy.setUetr(request.getUetr());
//        dummy.setAmountType(request.getAmountType());
//        dummy.setSum(request.getSum());
//        dummy.setCurrency("BIF");
//        dummy.setDocumentToken("dummy.jwt.replace-with-real-token-from-gateway.hgdvshgavshgbd.whdgvhgsa");
//        dummy.setStartOfPayment(meta);
//
//        GenericDataResponse<IpsQrStartOfPaymentResponse> response = GenericDataResponse.<IpsQrStartOfPaymentResponse>builder()
//                .retCode("00")
//                .message("OK (dummy data — replace with gateway call when ready)")
//                .data(dummy)
//                .build();
//
//        logger.log(Level.INFO, "IPS QR start-of-payment (dummy) response: {0}", response);



        String url = dsuMobApp.getIps_qr_start_of_payment_url();
        if (url == null || url.trim().isEmpty()) {
            return GenericDataResponse.<IpsQrStartOfPaymentResponse>builder()
                    .retCode("500")
                    .message("IPS start-of-payment URL is not configured (dsumobapp.ips_qr_start_of_payment_url)")
                    .data(null)
                    .build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IpsQrStartOfPaymentRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<IpsQrStartOfPaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    IpsQrStartOfPaymentResponse.class
            );
            IpsQrStartOfPaymentResponse body = response.getBody();
            logger.log(Level.INFO, "IPS QR start-of-payment HTTP status: {0}", response.getStatusCode());

            return GenericDataResponse.<IpsQrStartOfPaymentResponse>builder()
                    .retCode("00")
                    .message("OK")
                    .data(body)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "IPS QR start-of-payment failed: {0}", e.getMessage());
            return GenericDataResponse.<IpsQrStartOfPaymentResponse>builder()
                    .retCode("500")
                    .message(e.getMessage() != null ? e.getMessage() : "IPS QR start-of-payment failed")
                    .data(null)
                    .build();
        }
    }

    @Override
    public GenericDataResponse<RequestToPayResponse> ipsRequestToPay(IpsRequestToPayRequest request) {
        logger.log(Level.INFO, "IPS Request to pay request: {0}", request);

        String t24BaseUrl = dsuMobApp.getT24_base_url();
        logger.log(Level.INFO, "T24 base Url: {0}", t24BaseUrl);

        String uniqueRef = uuidGenerator.generate().toString();

        AccountRequest accountRequest = new AccountRequest("AccountInformation", request.getCust_account(), "", "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

        GenericRequest genericRequest = GenericRequest.builder()
                .requestId(request.getReceiver_account())
                .build();
        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);

        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);

//        String receiverName = genericResponse.getName();
        String beneficiaryCode = genericResponse.getRetCode();
//        if (isEmpty(receiverName)) {
//            return GenericDataResponse.<RequestToPayResponse>builder()
//                    .retCode("400")
//                    .message("Receiver name could not be resolved from IPS account lookup")
//                    .data(null)
//                    .build();
//        }
//        if (isEmpty(beneficiaryCode)) {
//            return GenericDataResponse.<RequestToPayResponse>builder()
//                    .retCode("400")
//                    .message("Beneficiary code could not be resolved from IPS account lookup")
//                    .data(null)
//                    .build();
//        }

//        BeneficiaryBank beneficiaryBank = beneficiaryBankRepository.findByBeneficiaryCode(beneficiaryCode.trim())
//                .orElse(null);
//        if (beneficiaryBank == null) {
//            return GenericDataResponse.<RequestToPayResponse>builder()
//                    .retCode("404")
//                    .message("Beneficiary bank not found for code: " + beneficiaryCode)
//                    .data(null)
//                    .build();
//        }

        String isDebtorSwift = genericResponse.getIsDebtorSwift();
        if (isEmpty(isDebtorSwift)) {
            return GenericDataResponse.<RequestToPayResponse>builder()
                    .retCode("400")
                    .message("Could not determine isDebtorSwift from IPS name lookup (expected servicer.bic or servicer.memberId)")
                    .data(null)
                    .build();
        }

        String uetr = UUID.randomUUID().toString();
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        java.time.format.DateTimeFormatter isoOffsetFormatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String createDate = now.format(isoOffsetFormatter);
        String timePlus = now.plusMinutes(45).format(isoOffsetFormatter);

//        String beneficiaryCoded = isDebtorSwift.equalsIgnoreCase("true") ? "true" : "false";

//        IpsRequestToPayRequest parameters = new IpsRequestToPayRequest(
//                uetr,
//                request.getTxn_amount(),
//                "BIF",
//                accountResponse.getCustomerName(),
//                request.getCust_account(),
//                genericResponse.getName(),
//                genericResponse.getId(),
//                beneficiaryCode,
//                isDebtorSwift,
//                createDate,
//                timePlus,
//                dsuMobApp.getBank_ips_request_to_pay()
//        );
        IpsRequestToPayRequest parameters = new IpsRequestToPayRequest(
                uetr,
                request.getTxn_amount(),
                "BIF",
                genericResponse.getName(),
                genericResponse.getId(),
                accountResponse.getCustomerName(),
                request.getCust_account(),
                beneficiaryCode,
                isDebtorSwift,
                createDate,
                timePlus,
                dsuMobApp.getBank_ips_request_to_pay()
        );

        logger.log(Level.INFO, "Parameters on IPS: {0}", parameters);

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
//        ResponseEntity<TransferResponse> response = restTemplate.exchange(
//                t24BaseUrl, HttpMethod.POST, entity, TransferResponse.class
//        );
////
//        logger.log(Level.INFO, "Response: {0}", response);
//
//        TransferResponse.Result transferResponseResult = response.getBody().getResponseMessage().getResult();
//
//        assert response.getBody() != null;
//        return response.getBody().getResponseMessage().getResult();








        try {
            ResponseEntity<RequestToPayResponse> response1 = restTemplate.exchange(
                    t24BaseUrl, HttpMethod.POST, entity, RequestToPayResponse.class
            );
            RequestToPayResponse body1 = response1.getBody();
            logger.log(Level.INFO, "IPS Request to pay HTTP status: {0}", response1.getStatusCode());

            assert body1 != null;
            return GenericDataResponse.<RequestToPayResponse>builder()
                    .retCode(body1.getResponseMessage().getTrackingId())
                    .message(body1.getResponseMessage().getMessage())
                    .data(body1)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "IPS QR start-of-payment failed: {0}", e.getMessage());
            return GenericDataResponse.<RequestToPayResponse>builder()
                    .retCode("500")
                    .message(e.getMessage() != null ? e.getMessage() : "IPS QR start-of-payment failed")
                    .data(null)
                    .build();
        }
    }

    @Override
    public GenericDataResponse getRequestToPayTransactions(GenericRequest request) {
        logger.log(Level.INFO, "IPS request to pay transactions request: {0}", request);

        String rtpPendingUrl = dsuMobApp.getIps_get_all_request_to_pay_txns();
        if (rtpPendingUrl == null || rtpPendingUrl.trim().isEmpty()) {
            return GenericDataResponse.builder()
                    .retCode("500")
                    .message("RTP pending transactions URL is not configured (dsumobapp.ips_get_all_request_to_pay_txns)")
                    .data(null)
                    .build();
        }

        logger.log(Level.INFO, "RTP pending transactions URL: {0}", rtpPendingUrl);

        AccountRequest accountRequest = new AccountRequest(dsuMobApp.getAccount_information_txn_type(), "", request.getRequestId(), "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<RtpPendingTransactionsResponse> response = restTemplate.exchange(
                    rtpPendingUrl, HttpMethod.GET, entity, RtpPendingTransactionsResponse.class
            );
            RtpPendingTransactionsResponse body = response.getBody();
            logger.log(Level.INFO, "RTP pending transactions HTTP status: {0}", response.getStatusCode());

            if (body == null || body.getItems() == null) {
                return GenericDataResponse.builder()
                        .retCode("500")
                        .message("Empty response from RTP pending transactions service")
                        .data(null)
                        .build();
            }

            Set<String> accountIds = accountResponse.getAccounts() != null
                    ? accountResponse.getAccounts().stream()
                        .map(AccountResponse.AccountDetails::getAccountId)
                        .filter(id -> id != null && !id.isEmpty())
                        .collect(Collectors.toSet())
                    : java.util.Collections.emptySet();

            logger.log(Level.INFO, "Customer account IDs to filter: {0}", accountIds);

            List<RtpParsedTransaction> parsed = body.getItems().stream()
                    .filter(item -> item.getProcessedMessage() != null)
                    .map(this::parseRtpTransaction)
                    .filter(txn -> belongsToCustomerAccounts(txn, accountIds))
                    .filter(this::isPendingStartOfPayment)
                    .filter(this::isWithinCreationAndExpiryRange)
                    .filter(this::isNotAlreadySentToIps)
                    .collect(Collectors.toList());

            logger.log(Level.INFO, "Filtered {0} of {1} RTP transactions for customer: {2}",
                    new Object[]{parsed.size(), body.getItems().size(), request.getRequestId()});

            return GenericDataResponse.builder()
                    .retCode("200")
                    .message("Request to pay transactions retrieved successfully")
                    .data(parsed)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "RTP pending transactions failed: {0}", e.getMessage());
            return GenericDataResponse.builder()
                    .retCode("500")
                    .message(e.getMessage() != null ? e.getMessage() : "RTP pending transactions failed")
                    .data(null)
                    .build();
        }
    }

    @Override
    public TransferResponse.Result ipsTransferRequestConfirm(IPSTransferConfirmRequest request) {
        logger.log(Level.INFO, "IpsTransferRequestConfirm request {0}", request);
        AccountBalanceRequest accountBalanceRequest = new AccountBalanceRequest();
        accountBalanceRequest.setAccount(request.getDebtor_account());
        AccountBalanceResponse.Result balanceResult = accountService.getAccountBalance(accountBalanceRequest);

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
        logger.log(Level.INFO, "Account Info request for IPS: {0}", request);

        String uniqueRef = uuidGenerator.generate().toString();

//        AccountRequest accountRequest = new AccountRequest("AccountInformation", ipsTransferRequest.getCustomer_account(), "", "");
//        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
//        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
//        logger.log(Level.INFO, "Account Response: {0}", accountResponse);
//
//        GenericRequest genericRequest = GenericRequest.builder()
//                .requestId(ipsTransferRequest.getPhone_number())
//                .build();
//        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);
//
//        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
//        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);

        IPSTransferConfirmRequestParams parameters = new IPSTransferConfirmRequestParams(
                request.getDebtor_account(),
                request.getDebtor_name(),
                request.getAmount(),
                request.getCreditor_agent(),
                request.getDebtor_name(),
                request.getCreditor_account(),
                request.getUetr(),
                uniqueRef,
                dsuMobApp.getIps_request_to_pay_confirm()
        );

        logger.log(Level.INFO, "Parameters on IPS RTP confirm: {0}", parameters);

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

    private RtpParsedTransaction parseRtpTransaction(RtpPendingTransactionsResponse.RtpTransaction item) {
        RtpParsedTransaction.RtpParsedTransactionBuilder builder = RtpParsedTransaction.builder()
                .id(item.getId())
                .uetr(item.getUetr())
                .msgId(item.getMsgId())
                .messageType(item.getMessageType())
                .rtpFlowState(item.getRtpFlowState())
                .receivedAt(formatReadableDate(item.getReceivedAt()))
                .trackingId(item.getTrackingId());

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode processedJson = mapper.readTree(item.getProcessedMessage());
            String xmlDocument = processedJson.path("document").asText();

            if (xmlDocument != null && !xmlDocument.isEmpty()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                DocumentBuilder docBuilder = factory.newDocumentBuilder();
                Document doc = docBuilder.parse(new InputSource(new StringReader(xmlDocument)));

                builder.debtorName(getTagValue(doc, "Dbtr", "Nm"))
                        .debtorAccount(getNestedTagValue(doc, "DbtrAcct"))
                        .debtorAgent(getAgentValue(doc, "DbtrAgt"))
                        .creditorName(getTagValue(doc, "Cdtr", "Nm"))
                        .creditorAccount(getNestedTagValue(doc, "CdtrAcct"))
                        .creditorAgent(getCdtrAgentValue(doc))
                        .initiatingParty(getElementText(doc, "AnyBIC"));

                org.w3c.dom.NodeList instdAmtNodes = doc.getElementsByTagName("InstdAmt");
                if (instdAmtNodes.getLength() > 0) {
                    org.w3c.dom.Element amtEl = (org.w3c.dom.Element) instdAmtNodes.item(0);
                    builder.amount(amtEl.getTextContent().trim());
                    builder.currency(amtEl.getAttribute("Ccy"));
                }

                String creationIso = getElementText(doc, "CreDtTm");
                String requestedExecutionIso = getDateTimeFromParent(doc, "ReqdExctnDt");
                String expiryIso = getDateTimeFromParent(doc, "XpryDt");

                builder.creationInstant(parseIsoToInstant(creationIso))
                        .expiryInstant(parseIsoToInstant(expiryIso))
                        .creationDate(formatReadableDate(creationIso))
                        .requestedExecutionDate(formatReadableDate(requestedExecutionIso))
                        .expiryDate(formatReadableDate(expiryIso));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse processedMessage for item {0}: {1}",
                    new Object[]{item.getId(), e.getMessage()});
        }

        return builder.build();
    }

    /**
     * Reads DtTm (preferred) or Dt from a parent element such as XpryDt / ReqdExctnDt.
     */
    private String getDateTimeFromParent(Document doc, String parentTag) {
        org.w3c.dom.NodeList parents = doc.getElementsByTagName(parentTag);
        if (parents.getLength() == 0) {
            return null;
        }
        org.w3c.dom.Element parent = (org.w3c.dom.Element) parents.item(0);
        org.w3c.dom.NodeList dtTmNodes = parent.getElementsByTagName("DtTm");
        if (dtTmNodes.getLength() > 0) {
            String value = dtTmNodes.item(0).getTextContent();
            return value != null ? value.trim() : null;
        }
        org.w3c.dom.NodeList dtNodes = parent.getElementsByTagName("Dt");
        if (dtNodes.getLength() > 0) {
            String value = dtNodes.item(0).getTextContent();
            return value != null ? value.trim() : null;
        }
        String text = parent.getTextContent();
        return text != null ? text.trim() : null;
    }

    private String getTagValue(Document doc, String parentTag, String childTag) {
        org.w3c.dom.NodeList parents = doc.getElementsByTagName(parentTag);
        for (int i = 0; i < parents.getLength(); i++) {
            org.w3c.dom.Element parent = (org.w3c.dom.Element) parents.item(i);
            org.w3c.dom.NodeList children = parent.getElementsByTagName(childTag);
            if (children.getLength() > 0) {
                return children.item(0).getTextContent().trim();
            }
        }
        return null;
    }

    private String getNestedTagValue(Document doc, String acctTag) {
        org.w3c.dom.NodeList acctNodes = doc.getElementsByTagName(acctTag);
        if (acctNodes.getLength() > 0) {
            org.w3c.dom.Element acctEl = (org.w3c.dom.Element) acctNodes.item(0);
            org.w3c.dom.NodeList idNodes = acctEl.getElementsByTagName("Id");
            for (int i = 0; i < idNodes.getLength(); i++) {
                org.w3c.dom.NodeList othrNodes = ((org.w3c.dom.Element) idNodes.item(i)).getElementsByTagName("Othr");
                if (othrNodes.getLength() > 0) {
                    org.w3c.dom.NodeList innerIdNodes = ((org.w3c.dom.Element) othrNodes.item(0)).getElementsByTagName("Id");
                    if (innerIdNodes.getLength() > 0) {
                        return innerIdNodes.item(0).getTextContent().trim();
                    }
                }
            }
        }
        return null;
    }

    private String getAgentValue(Document doc, String agentTag) {
        org.w3c.dom.NodeList nodes = doc.getElementsByTagName(agentTag);
        if (nodes.getLength() > 0) {
            org.w3c.dom.Element el = (org.w3c.dom.Element) nodes.item(0);
            org.w3c.dom.NodeList mmbId = el.getElementsByTagName("MmbId");
            if (mmbId.getLength() > 0) {
                return mmbId.item(0).getTextContent().trim();
            }
            org.w3c.dom.NodeList bicfi = el.getElementsByTagName("BICFI");
            if (bicfi.getLength() > 0) {
                return bicfi.item(0).getTextContent().trim();
            }
        }
        return null;
    }

    private String getCdtrAgentValue(Document doc) {
        org.w3c.dom.NodeList nodes = doc.getElementsByTagName("CdtrAgt");
        if (nodes.getLength() > 0) {
            org.w3c.dom.Element el = (org.w3c.dom.Element) nodes.item(0);
            org.w3c.dom.NodeList bicfi = el.getElementsByTagName("BICFI");
            if (bicfi.getLength() > 0) {
                return bicfi.item(0).getTextContent().trim();
            }
            org.w3c.dom.NodeList mmbId = el.getElementsByTagName("MmbId");
            if (mmbId.getLength() > 0) {
                return mmbId.item(0).getTextContent().trim();
            }
        }
        return null;
    }

    private String getElementText(Document doc, String tagName) {
        org.w3c.dom.NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    /**
     * Keeps RTPs where one of the customer accounts is the debtor (customer must pay)
     * or the creditor (customer requested the payment).
     */
    private boolean belongsToCustomerAccounts(RtpParsedTransaction txn, Set<String> accountIds) {
        if (txn == null || accountIds == null || accountIds.isEmpty()) {
            return false;
        }

        boolean matched = matchesAccount(txn.getDebtorAccount(), accountIds)
                || matchesAccount(txn.getCreditorAccount(), accountIds);

        if (!matched) {
            logger.log(Level.FINE, "RTP txn {0} excluded: debtor={1}, creditor={2} not in {3}",
                    new Object[]{txn.getId(), txn.getDebtorAccount(), txn.getCreditorAccount(), accountIds});
        }

        return matched;
    }

    private boolean matchesAccount(String account, Set<String> accountIds) {
        if (isEmpty(account)) {
            return false;
        }
        String value = account.trim();
        return accountIds.stream().anyMatch(id -> id != null && id.trim().equalsIgnoreCase(value));
    }

    /**
     * Drops RTPs that already have a T24→IPS message_flow with the same msgId and uetr.
     */
    private boolean isNotAlreadySentToIps(RtpParsedTransaction txn) {
        if (txn == null) {
            return false;
        }
        boolean alreadySent = messageFlowRepository.existsSentToIps(txn.getUetr(), txn.getMsgId());
        if (alreadySent) {
            logger.log(Level.INFO, "RTP txn {0} excluded: T24_TO_IPS message_flow exists for msgId={1} uetr={2}",
                    new Object[]{txn.getId(), txn.getMsgId(), txn.getUetr()});
            return false;
        }
        return true;
    }

    private boolean isPendingStartOfPayment(RtpParsedTransaction txn) {
        return txn != null
                && txn.getRtpFlowState() != null
                && "PENDING_T24_START_OF_PAYMENT".equalsIgnoreCase(txn.getRtpFlowState().trim());
    }

    /**
     * Keeps RTPs where current time is on/after CreDtTm and on/before XpryDt/DtTm (ISO instants).
     * Missing creation is ignored; missing/unparseable expiry excludes the item.
     */
    private boolean isWithinCreationAndExpiryRange(RtpParsedTransaction txn) {
        if (txn == null) {
            return false;
        }

        java.time.Instant now = java.time.Instant.now();
        java.time.Instant creationInstant = txn.getCreationInstant();
        java.time.Instant expiryInstant = txn.getExpiryInstant();

        if (expiryInstant == null) {
            logger.log(Level.WARNING, "RTP txn {0} excluded: missing/unparseable expiry_date={1}",
                    new Object[]{txn.getId(), txn.getExpiryDate()});
            return false;
        }

        if (creationInstant != null && now.isBefore(creationInstant)) {
            return false;
        }

        return !now.isAfter(expiryInstant);
    }

    private java.time.Instant parseIsoToInstant(String isoDate) {
        if (isEmpty(isoDate)) {
            return null;
        }
        String value = isoDate.trim();
        try {
            return java.time.OffsetDateTime.parse(value).toInstant();
        } catch (Exception e1) {
            try {
                return java.time.LocalDateTime.parse(value)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant();
            } catch (Exception e2) {
                try {
                    return java.time.LocalDate.parse(value)
                            .atStartOfDay(java.time.ZoneId.systemDefault())
                            .toInstant();
                } catch (Exception e3) {
                    logger.log(Level.WARNING, "Could not parse ISO date for filtering: {0}", value);
                    return null;
                }
            }
        }
    }

    private String formatReadableDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return null;
        try {
            java.time.temporal.TemporalAccessor parsed;
            if (isoDate.contains("+") || isoDate.endsWith("Z")) {
                parsed = java.time.OffsetDateTime.parse(isoDate);
            } else {
                parsed = java.time.LocalDateTime.parse(isoDate);
            }
            return java.time.format.DateTimeFormatter
                    .ofPattern("dd MMM yyyy, hh:mm a", java.util.Locale.ENGLISH)
                    .format(parsed);
        } catch (Exception e) {
            logger.log(Level.FINE, "Could not parse date: {0}", isoDate);
            return isoDate;
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String resolveCustomerNumberForAccount(String accountNumber) {
        if (isEmpty(accountNumber)) {
            return null;
        }
        try {
            AccountRequest accountRequest = new AccountRequest("AccountInformation", accountNumber.trim(), "", "");
            AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
            if (accountResponse != null && !isEmpty(accountResponse.getCustomerNumber())) {
                return accountResponse.getCustomerNumber().trim();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not resolve customer number for account {0}: {1}",
                    new Object[]{accountNumber, e.getMessage()});
        }
        return null;
    }

    /**
     * QR read returns creditor agent as {@code <BICFI>} (commercial bank) or {@code <MmbId>} (MFI).
     */
    private static final class CreditorAgentXml {
        private final String agentId;
        private final String isCreditorSwift;

        private CreditorAgentXml(String agentId, String isCreditorSwift) {
            this.agentId = agentId;
            this.isCreditorSwift = isCreditorSwift;
        }
    }

    private CreditorAgentXml parseCreditorAgentXml(String xmlCreditorBic) {
        if (isEmpty(xmlCreditorBic)) {
            return null;
        }
        try {
            String xml = xmlCreditorBic
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            org.w3c.dom.NodeList bicfi = document.getElementsByTagName("BICFI");
            if (bicfi.getLength() > 0 && !isEmpty(bicfi.item(0).getTextContent())) {
                String agentId = bicfi.item(0).getTextContent().trim();
                logger.log(Level.INFO, "QR creditor agent BICFI={0}, isCreditorSwift=true", agentId);
                return new CreditorAgentXml(agentId, "true");
            }

            org.w3c.dom.NodeList mmbId = document.getElementsByTagName("MmbId");
            if (mmbId.getLength() > 0 && !isEmpty(mmbId.item(0).getTextContent())) {
                String agentId = mmbId.item(0).getTextContent().trim();
                logger.log(Level.INFO, "QR creditor agent MmbId={0}, isCreditorSwift=false", agentId);
                return new CreditorAgentXml(agentId, "false");
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.log(Level.SEVERE, "Error parsing Creditor agent XML", e);
        }
        return null;
    }

    private String resolveCustomerNameForAccount(String accountNumber) {
        if (isEmpty(accountNumber)) {
            return null;
        }
        try {
            AccountRequest accountRequest = new AccountRequest("AccountInformation", accountNumber.trim(), "", "");
            AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
            if (accountResponse != null && !isEmpty(accountResponse.getCustomerNumber())) {
                return accountResponse.getCustomerName().trim();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not resolve customer number for account {0}: {1}",
                    new Object[]{accountNumber, e.getMessage()});
        }
        return null;
    }

    private IpsQrCode saveCreatedIpsQrCode(
            IpsQrCreateRequest request,
            String qrType,
            String currency,
            String agentIdType,
            boolean isDynamic,
            String qrCodeUrl,
            IpsQrCreateResponse gatewayMapped,
            String ourReference,
            QrCodeGenerateResult imageResult,
            String qrImageBase64,
            String createdByAccount,
            String createdByCustomerNumber
    ) {
        Date now = new Date();
        String uetr = !isEmpty(gatewayMapped.getQrExtensionUUID())
                ? gatewayMapped.getQrExtensionUUID()
                : gatewayMapped.getUetr();

        IpsQrCode entity = IpsQrCode.builder()
                .qrType(qrType)
                .creditorName(request.getCreditorName())
                .creditorAccount(request.getCreditorAccount())
                .memberId(request.getMemberId())
                .agentIdType(agentIdType)
                .currency(currency)
                .amount(isDynamic ? request.getSum() : null)
                .qrCodeUrl(qrCodeUrl)
                .uetr(uetr)
                .qrHeaderUUID(gatewayMapped.getQrHeaderUUID())
                .ourReference(ourReference)
                .fileName(imageResult != null ? imageResult.getFileName() : null)
                .savedFilePath(imageResult != null ? imageResult.getSavedFilePath() : null)
                .qrImageBase64(qrImageBase64)
                .ttlLength(request.getTtlLength())
                .ttlUnits(request.getTtlUnits())
                .createdByAccount(createdByAccount)
                .createdByCustomerNumber(createdByCustomerNumber)
                .status(Status.ACTIVE)
                .createdDate(now)
                .updatedDate(now)
                .build();

        return ipsQrCodeRepository.save(entity);
    }

    private GenericResponse mapToGenericResponse(IpsNameLookupResponse ipsNameLookupResponse, GenericRequest genericRequest) {
            GenericResponse response = new GenericResponse();
            IpsNameLookupResponse.Servicer servicer = ipsNameLookupResponse.getServicer();
            String agentId = servicer != null ? servicer.resolveAgentId() : null;
            Boolean debtorSwift = servicer != null ? servicer.isDebtorSwift() : null;

            response.setId(ipsNameLookupResponse.getId().getOther());
            response.setName(ipsNameLookupResponse.getName() + " " + ipsNameLookupResponse.getSurname()
                    + (agentId != null ? " (" + agentId + ")" : ""));
            response.setRetCode(agentId);
            response.setOtherInfo(ipsNameLookupResponse.getType());
            if (debtorSwift != null) {
                response.setIsDebtorSwift(debtorSwift.toString());
            }

            return response;
    }

}
