package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavigationsResponse {
    private String id;
    @JsonProperty("associate_bank_name")
    private String associateBankName;
    @JsonProperty("associate_bank_status")
    private Status associateBankStatus;
    @JsonProperty("associate_bank_added_date")
    private Date associateBankAddedDate;
    @JsonProperty("associate_bank_updated_date")
    private Date associateBankUpdatedDate;

    @JsonProperty("main_menus")
    List<MainMenuResponse> mainMenus;
}
