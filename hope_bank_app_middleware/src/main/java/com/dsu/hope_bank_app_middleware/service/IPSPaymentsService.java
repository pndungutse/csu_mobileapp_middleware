package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.GenericRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSPayQrRequest;
import com.dsu.hope_bank_app_middleware.request.ipsRequest.IPSTransferRequest;
import com.dsu.hope_bank_app_middleware.response.GenericResponse;
import com.dsu.hope_bank_app_middleware.response.TransferResponse;

public interface IPSPaymentsService {
    GenericResponse getIpsAccountInformation(GenericRequest genericRequest);

    TransferResponse.Result processTransferIpsOther(IPSTransferRequest ipsTransferRequest);

    TransferResponse.Result processTransferIpsPayQr(IPSPayQrRequest ipsPayQrRequest);

    GenericResponse getIpsQrInformation(GenericRequest genericRequest);
}
