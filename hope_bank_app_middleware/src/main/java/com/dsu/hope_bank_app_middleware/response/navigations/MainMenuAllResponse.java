package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.entity.navigations.AssociateBank;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
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
public class MainMenuAllResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("menu_item_name")
    private String menuItemName;
    @JsonProperty("menu_item_desc")
    private String menuItemDesc;
    @JsonProperty("associated_bank")
    private String associatedBank;
    @JsonProperty("menu_item_status")
    private Status menuItemStatus;
    @JsonProperty("icon")
    private String icon;

//    @JsonProperty("sub_menus")
//    private List<SubMenuResponse> subMenus;

    @JsonProperty("associate_bank")
    private String associateBank;

    @JsonProperty("menu_item_added_date")
    private Date menuItemAddedDate;
    @JsonProperty("menu_item_updated_date")
    private Date menuItemUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
