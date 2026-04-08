package com.dsu.hope_bank_app_middleware.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    @JsonProperty("txn_type")
    private String txn_type;

    @JsonProperty("accountNumber")
    @JsonAlias({"account_number"})
    private String accountNumber;

    @JsonProperty("Customerid")
    @JsonAlias({"customerid", "customerId", "CustomerId"})
    private String Customerid;

    @JsonProperty("legalId")
    @JsonAlias({"legal_id"})
    private String legalId;
    private String environment;

    public AccountRequest(String txn_type, String accountNumber, String Customerid, String legalId) {
        this.txn_type = txn_type;
        this.accountNumber = accountNumber;
        this.Customerid = Customerid;
        this.legalId = legalId;
    }
}
