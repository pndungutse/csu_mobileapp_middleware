package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.config.SSLUtil;
import com.dsu.hope_bank_app_middleware.request.AccountBalanceRequest;
import com.dsu.hope_bank_app_middleware.request.AccountRequest;
import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSPayQrRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSTransferRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IpsNameLookupRequest;
import com.dsu.hope_bank_app_middleware.response.AccountBalanceResponse;
import com.dsu.hope_bank_app_middleware.response.AccountResponse;
import com.dsu.hope_bank_app_middleware.response.GenericDataResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsNameLookupResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrReadResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.service.IPSPaymentsService;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        ipsPayQrRequest.setQr_reference("25768919242");
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
        logger.log(Level.INFO, "Account Info request for IPS PAY QR: {0}", ipsPayQrRequest);

        String uniqueRef = uuidGenerator.generate().toString();

        AccountRequest accountRequest = new AccountRequest("AccountInformation", ipsPayQrRequest.getCustomer_account(), "", "");
        logger.log(Level.INFO, "Account Request on for getting customer info: {0}", accountRequest);
        AccountResponse accountResponse = accountService.getAccountInformation(accountRequest);
        logger.log(Level.INFO, "Account Response: {0}", accountResponse);

        GenericRequest genericRequest = GenericRequest.builder()
                .requestId(ipsPayQrRequest.getQr_reference())
                .build();
        GenericResponse genericResponse = getIpsAccountInformation(genericRequest);

        logger.log(Level.INFO, "Generic Request: {0}", genericRequest);
        logger.log(Level.INFO, "Generic Response: {0}", genericResponse);

        IPSPayQrRequest parameters = new IPSPayQrRequest(
                ipsPayQrRequest.getCustomer_account(),
                accountResponse.getCustomerName(),
                ipsPayQrRequest.getAmount(),
//                ipsTransferRequest.getTomember(),
                genericResponse.getRetCode(),
                genericResponse.getName(),
                genericResponse.getId(),
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
        logger.log(Level.INFO, "Request for IPS QR read (dummy): {0}", genericRequest);

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

        IpsQrReadResponse.QrLookup qrLookup = new IpsQrReadResponse.QrLookup();
        qrLookup.setStatusCode(200);
        qrLookup.setBody("{\"header\":{\"qrType\":\"STAT\",\"amountType\":\"Fixed\"}}");
        qrLookup.setContentType("application/json");
        qrLookup.setRequestId("00000000-0000-0000-0000-000000000000");
        qrLookup.setUrl("https://example.com/dummy-qr-lookup");

        IpsQrReadResponse dummy = new IpsQrReadResponse();
        dummy.setQrCodeUrl(qrUrl);
        dummy.setExtractedUuid("423a9db5-7518-4477-bab8-349f4dd1e8a7");
        dummy.setUetr("491d0dde-9d91-4dcd-b071-ee4651d7d4aa");
        dummy.setCreditorName("Dummy Creditor");
        dummy.setCreditorAccount("60660");
        dummy.setE2e("T20260327081513732248093");
        dummy.setAmountType("Fixed");
        dummy.setSum("1005");
        dummy.setCurrency("BIF");
        dummy.setXmlCreditorBic("<BICFI>BCBUBIBI</BICFI>");
        dummy.setQrLookup(qrLookup);

        return GenericDataResponse.<IpsQrReadResponse>builder()
                .retCode("00")
                .message("OK (dummy data — replace with gateway call when ready)")
                .data(dummy)
                .build();
    }

    private GenericResponse mapToGenericResponse(IpsNameLookupResponse ipsNameLookupResponse, GenericRequest genericRequest) {
            GenericResponse response = new GenericResponse();
            response.setId(ipsNameLookupResponse.getId().getOther());
            response.setName(ipsNameLookupResponse.getName()+ " " +ipsNameLookupResponse.getSurname()+" ("+ipsNameLookupResponse.getServicer().getBic()+")");
            response.setRetCode(ipsNameLookupResponse.getServicer().getBic());

            return response;
    }

}
