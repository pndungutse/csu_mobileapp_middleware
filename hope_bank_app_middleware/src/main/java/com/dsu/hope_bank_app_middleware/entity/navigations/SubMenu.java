package com.dsu.hope_bank_app_middleware.entity.navigations;

import com.dsu.hope_bank_app_middleware.enumeration.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sub_menus")
public class SubMenu {
    @Id
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
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;
    @JsonProperty("sub_menu_belong_to_menu")
    private MainMenuBelong subMenuBelongToMenu;
    @JsonProperty("sub_menu_display_order")
    private Integer subMenuDisplayOrder;
    @JsonProperty("sub_menu_custom_implementation")
    private SubMenuCustomerImplementation subMenuCustomImplementation;
    @JsonProperty("sub_menu_wait_response")
    private String serviceWaitResponse;
    @JsonProperty("sub_menu_item_added_date")
    private Date subMenuItemAddedDate;
    @JsonProperty("sub_menu_item_updated_date")
    private Date subMenuItemUpdatedDate;

    @DBRef
    private AssociateBank bank;
}
