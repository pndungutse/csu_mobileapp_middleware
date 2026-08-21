package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import lombok.Data;

@Data
public class IpsNameLookupResponse {
    private IdHolder id;
    private String type;
    private String currency;
    private Servicer servicer;
    private String name;
    private String surname;   // this is what you need to return
    private Boolean isDefault;
    private Address address;
    private String documentType;
    private String documentNumber;

    @Data
    public static class IdHolder {
        private String other;
    }

    /**
     * IPS may return either {@code bic} (SWIFT bank) or {@code memberId} (non-SWIFT / MFI).
     * Keep them as separate fields so callers can set isDebtorSwift correctly.
     */
    @Data
    public static class Servicer {
        private String bic;
        private String memberId;

        /** Prefer bic when present; otherwise memberId. */
        public String resolveAgentId() {
            if (bic != null && !bic.trim().isEmpty()) {
                return bic.trim();
            }
            if (memberId != null && !memberId.trim().isEmpty()) {
                return memberId.trim();
            }
            return null;
        }

        /**
         * {@code true} when response used bic (SWIFT), {@code false} when it used memberId,
         * {@code null} when neither is present.
         */
        public Boolean isDebtorSwift() {
            if (bic != null && !bic.trim().isEmpty()) {
                return Boolean.TRUE;
            }
            if (memberId != null && !memberId.trim().isEmpty()) {
                return Boolean.FALSE;
            }
            return null;
        }
    }

    @Data
    public static class Address {
        private String country;
        private String city;
        private String stateProvinceRegion;
        private String address;
    }
}
