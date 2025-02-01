package com.dsu.hope_bank_app_middleware.navigations.entity;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
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
@Document(collection = "main_menus")
public class MainMenu {
    @Id
    private String id;
    private String menuItemName;
    private String menuItemDesc;
    private Status menuItemStatus;
    private String icon;
    private Date menuItemAddedDate;
    private Date menuItemUpdatedDate;

    @DBRef
    private AssociateBank bank;
}
