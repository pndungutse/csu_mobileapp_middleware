package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.request.navigations.IconRequest;
import com.dsu.hope_bank_app_middleware.response.navigations.IconResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IconService {
    ResponseEntity<IconResponse> createIcon(IconRequest request);

    ResponseEntity<IconResponse> getIconById(String id);

    ResponseEntity<IconResponse> updateIconById(String id, IconRequest request);

    ResponseEntity<List<IconResponse>> getAllIconItems();
}
