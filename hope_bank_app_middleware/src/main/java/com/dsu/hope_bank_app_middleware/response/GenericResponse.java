package com.dsu.hope_bank_app_middleware.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponse {
    private String id;
    private String name;
    private String retCode;
    private String otherInfo;
    /** From IPS name lookup: "true" when servicer used bic, "false" when memberId. */
    private String isDebtorSwift;
}
