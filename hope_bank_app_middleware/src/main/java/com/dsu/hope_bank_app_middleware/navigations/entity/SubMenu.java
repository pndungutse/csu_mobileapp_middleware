package com.dsu.hope_bank_app_middleware.navigations.entity;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.SubMenuCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

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
    @JsonProperty("sub_menu_item_category")
    private SubMenuCategory subMenuCategory;
    @JsonProperty("sub_menu_item_added_date")
    private Date subMenuItemAddedDate;
    @JsonProperty("sub_menu_item_updated_date")
    private Date subMenuItemUpdatedDate;

    @DBRef
    private MainMenu mainMenu;
}
