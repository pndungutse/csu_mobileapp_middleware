package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.request.AccountBalanceRequest;
import com.dsu.hope_bank_app_middleware.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSPayQrRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSTransferRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IpsNameLookupRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IpsQrStartOfPaymentRequest;
import com.dsu.hope_bank_app_middleware.response.AccountBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.AccountResponse;
import com.dsu.hope_bank_app_middleware.response.GenericDataResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsNameLookupResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrReadResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrStartOfPaymentResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.IPSPaymentsService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        IPSTransferRequest parameters = new IPSTransferRequest(
                ipsTransferRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                ipsTransferRequest.getAmount(),
//                ipsTransferRequest.getTomember(),
                genericResponse.getRetCode(),
                genericResponse.getName(),
                genericResponse.getId(),
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

        String xmlCreditorBic = genericDataResponse.getData().getXmlCreditorBic();

        String bic = "";

        try {
            // ✅ Unescape first (VERY IMPORTANT)
            String xml = xmlCreditorBic
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            bic = document.getDocumentElement().getTextContent();
            logger.log(Level.INFO, "Extracted BIC: {0}", bic);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.log(Level.SEVERE, "Error parsing Creditor BIC XML", e);

            TransferResponse.Result errorResult = new TransferResponse.Result();
            errorResult.setRet_code("500");
            errorResult.setRet_message("Failed to parse Creditor BIC");
            return errorResult;
        }

        IPSPayQrRequest parameters = new IPSPayQrRequest(
                ipsPayQrRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                postingAmount,
                bic,
                genericDataResponse.getData().getCreditorName(),
                genericDataResponse.getData().getCreditorAccount(),
                genericDataResponse.getData().getUetr(),
                "",
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

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private GenericResponse mapToGenericResponse(IpsNameLookupResponse ipsNameLookupResponse, GenericRequest genericRequest) {
            GenericResponse response = new GenericResponse();
            response.setId(ipsNameLookupResponse.getId().getOther());
            response.setName(ipsNameLookupResponse.getName()+ " " +ipsNameLookupResponse.getSurname()+" ("+ipsNameLookupResponse.getServicer().getBic()+")");
            response.setRetCode(ipsNameLookupResponse.getServicer().getBic());

            return response;
    }

}
