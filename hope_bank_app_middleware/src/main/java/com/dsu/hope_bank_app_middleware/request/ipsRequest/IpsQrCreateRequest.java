package com.dsu.hope_bank_app_middleware.request.ipsRequest;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client request for IPS QR creation. Only a few fields are required;
 * header/extension defaults are filled by the service.
 * <ul>
 *   <li>{@code STAT} – static / free amount</li>
 *   <li>{@code DYNM} – dynamic / fixed amount ({@code sum} required)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpsQrCreateRequest {

    /** {@code STAT} or {@code DYNM} */
    @JsonProperty("qr_type")
    @JsonAlias({"qrType"})
    private String qrType;

    @JsonProperty("creditor_name")
    @JsonAlias({"creditorName"})
    private String creditorName;

    @JsonProperty("creditor_account")
    @JsonAlias({"creditorAccount"})
    private String creditorAccount;

    /** Value for creditorAgent, e.g. TURABIBI */
    @JsonProperty("member_id")
    @JsonAlias({"memberId", "sender_bic", "senderBic"})
    private String memberId;

    /**
     * Creditor agent JSON key. Defaults to {@code memberId}.
     * Use {@code bic} when sending a BIC instead of member id.
     */
    @JsonProperty("agent_id_type")
    @JsonAlias({"agentIdType"})
    private String agentIdType;

    private String currency;

    /** Required when {@code qr_type} is {@code DYNM}. */
    private String sum;

    @JsonProperty("ttl_length")
    @JsonAlias({"ttlLength"})
    private Integer ttlLength;

    @JsonProperty("ttl_units")
    @JsonAlias({"ttlUnits"})
    private String ttlUnits;

    /** Optional QR image size in pixels (default 300). */
    private Integer size;
}
