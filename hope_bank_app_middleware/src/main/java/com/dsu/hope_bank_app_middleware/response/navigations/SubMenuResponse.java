package com.dsu.hope_bank_app_middleware.navigations.response;

import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.entity.FormElement;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.SubMenuCategory;
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

    @JsonProperty("form_elements")
    private List<FormElementResponse> formElements;
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;
    @JsonProperty("main_menu_item_name")
    private String mainMenuItemName;
    @JsonProperty("sub_menu_item_added_date")
    private Date subMenuItemAddedDate;
    @JsonProperty("sub_menu_item_updated_date")
    private Date subMenuItemUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
