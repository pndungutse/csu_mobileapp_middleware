package com.dsu.hope_bank_app_middleware.request.navigations;

import com.dsu.hope_bank_app_middleware.enumeration.*;
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
    // SubMenu information
    @JsonProperty("associate_bank")
    private String associateBank;
    @JsonProperty("sub_menu_item_name")
    private String subMenuItemName;
    @JsonProperty("sub_menu_item_desc")
    private String subMenuItemDesc;
    @JsonProperty("sub_menu_item_status")
    private Status subMenuItemStatus;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("sub_menu_action_url")
    private String subMenuActionUrl;
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;
    @JsonProperty("sub_menu_belong_to_menu")
    private MainMenuBelong subMenuBelongToMenu;
    @JsonProperty("sub_menu_custom_implementation")
    private SubMenuCustomerImplementation subMenuCustomImplementation;
    @JsonProperty("sub_menu_custom_implementation_url")
    private String customImplementationUrl;
    @JsonProperty("sub_menu_custom_implementation_method")
    private CustomImplementationMethod customImplementationMethod;
    @JsonProperty("sub_menu_custom_implementation_list_tags_display")
    private String customImplementationListTagsDisplay;
    @JsonProperty("sub_menu_display_order")
    private Integer subMenuDisplayOrder;
    @JsonProperty("sub_menu_wait_response")
    private String serviceWaitResponse;

}
