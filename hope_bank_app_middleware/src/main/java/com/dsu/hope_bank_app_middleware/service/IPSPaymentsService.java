package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.*;
import com.dsu.hope_bank_app_middleware.response.GenericDataResponse;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrReadResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.IpsQrStartOfPaymentResponse;
import com.dsu.hope_bank_app_middleware.response.IPSResponse.RequestToPayResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;
import com.dsu.hope_bank_app_middleware.entity.IpsQrCode;

import java.util.List;

public interface IPSPaymentsService {
    GenericResponse getIpsAccountInformation(GenericRequest genericRequest);

    TransferResponse.Result processTransferIpsOther(IPSTransferRequest ipsTransferRequest);

    TransferResponse.Result processTransferIpsPayQr(IPSPayQrRequest ipsPayQrRequest);

    TransferResponse.Result processTransferIpsPayQrFixedDynamic(IPSPayQrRequest ipsPayQrRequest);

    GenericResponse getIpsQrInformation(GenericRequest genericRequest);

    GenericDataResponse<IpsQrReadResponse> getIpsQrCodeInfo(GenericRequest genericRequest);

    GenericDataResponse<TransferResponse.Result> createIpsQrCode(IpsQrCreateRequest request);

    GenericDataResponse<List<IpsQrCode>> getCreatedIpsQrCodes(GenericRequest request);

    GenericDataResponse<IpsQrStartOfPaymentResponse> startIpsQrStartOfPayment(IpsQrStartOfPaymentRequest request);

    GenericDataResponse<RequestToPayResponse> ipsRequestToPay(IpsRequestToPayRequest request);

    GenericDataResponse getRequestToPayTransactions(GenericRequest request);

    TransferResponse.Result ipsTransferRequestConfirm(IPSTransferConfirmRequest request);


}
