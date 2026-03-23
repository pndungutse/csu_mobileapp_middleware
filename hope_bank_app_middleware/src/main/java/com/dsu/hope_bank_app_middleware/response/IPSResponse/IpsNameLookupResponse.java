package com.dsu.hope_bank_app_middleware.response.IPSResponse;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Data
    public static class Servicer {
        @JsonProperty("bic")
        @JsonAlias({"memberId"})
        private String bic;
    }

    @Data
    public static class Address {
        private String country;
        private String city;
        private String stateProvinceRegion;
        private String address;
    }
}
