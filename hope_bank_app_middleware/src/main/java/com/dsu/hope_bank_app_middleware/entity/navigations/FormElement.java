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
    private Integer formElementFieldNo;
    private FormElementSelectItem formElementSelectItem;
    private Boolean formElementFetchDetail;
    private Boolean formElementNoInputField;
    private String formElementFetchInfoUrl;
    private String formElementDefaultValue;
    private FormElementPopulateInfo formElementPopulateInfo;
    private FormElementAutofill formElementAutofill;
    private String formElementAutofillFieldName;
    private String formElementAutofillParentFieldName;
    private String formElementChangeReadOnlyFieldName;
    private String formElementChangeReadOnlyFieldValue;
    private Date formElementAddedDate;
    private Date formElementUpdatedDate;

    @DBRef
    private SubMenu subMenu;
}
