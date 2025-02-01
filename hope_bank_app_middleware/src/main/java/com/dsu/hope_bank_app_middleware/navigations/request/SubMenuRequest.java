package com.dsu.hope_bank_app_middleware.navigations.request;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.SubMenuCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubMenuRequest {
    @JsonProperty("sub_menu_item_name")
    private String subMenuItemName;
    @JsonProperty("sub_menu_item_desc")
    private String subMenuItemDesc;
    @JsonProperty("sub_menu_item_status")
    private Status subMenuItemStatus;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;

}
