package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API envelope: {@code retCode} / {@code message} plus typed {@code data} from downstream services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericDataResponse<T> {
    private String retCode;
    private String message;
    private T data;
}
