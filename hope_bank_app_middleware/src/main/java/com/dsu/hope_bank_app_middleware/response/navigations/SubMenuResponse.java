package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.enumeration.MainMenuBelong;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.enumeration.SubMenuCategory;
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
public class SubMenuResponse {
    @JsonProperty("id")
    private String id;
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

    @JsonProperty("form_elements")
    private List<FormElementResponse> formElements;
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;
    @JsonProperty("sub_menu_belong_to_menu")
    private MainMenuBelong subMenuBelongToMenu;
    @JsonProperty("sub_menu_display_order")
    private Integer subMenuDisplayOrder;
    @JsonProperty("sub_menu_wait_response")
    private String serviceWaitResponse;
    @JsonProperty("sub_menu_associate_bank")
    private String subMenuAssociateBank;
    @JsonProperty("sub_menu_item_added_date")
    private Date subMenuItemAddedDate;
    @JsonProperty("sub_menu_item_updated_date")
    private Date subMenuItemUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
