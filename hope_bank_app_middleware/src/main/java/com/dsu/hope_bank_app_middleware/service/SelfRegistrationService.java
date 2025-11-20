package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.SelfRegistrationRequest;
import com.dsu.hope_bank_app_middleware.response.SelfRegistrationResponse;

public interface SelfRegistrationService {
    SelfRegistrationResponse.Result makeSelfRegistration(SelfRegistrationRequest request);
}
