package com.dsu.hope_bank_app_middleware.navigations.request;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainMenuRequest {
    @JsonProperty("menu_item_name")
    private String menuItemName;
    @JsonProperty("menu_item_desc")
    private String menuItemDesc;
    @JsonProperty("menu_item_status")
    private Status menuItemStatus;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("associate_bank_name")
    private String associateBankName;
}
