package com.dsu.hope_bank_app_middleware.navigations.entity;

import com.dsu.hope_bank_app_middleware.navigations.enumeration.FormElementType;
import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.ValueType;
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
@Document(collection = "form_elements")
public class FormElement {
    @Id
    private String id;
    private String formElementName;
    private String formElementDescription;
    private String formElementLabel;
    private ValueType formElementValueType;
    private FormElementType formElementType;
    private String formElementPlaceHolder;
    private Status formElementStatus;
    private Date formElementAddedDate;
    private Date formElementUpdatedDate;

    @DBRef
    private SubMenu subMenu;
}
