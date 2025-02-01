package com.dsu.hope_bank_app_middleware.exception;

import com.dsu.hope_bank_app_middleware.general_enumerations.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class SuccessResponse {
    private ResponseType type = ResponseType.success;
    private String description;
    private HttpStatus status;
}
