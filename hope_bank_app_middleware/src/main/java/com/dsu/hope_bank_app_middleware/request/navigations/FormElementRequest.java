package com.dsu.hope_bank_app_middleware.request.navigations;

import com.dsu.hope_bank_app_middleware.enumeration.FormElementSelectItem;
import com.dsu.hope_bank_app_middleware.enumeration.FormElementType;
import com.dsu.hope_bank_app_middleware.enumeration.Status;
import com.dsu.hope_bank_app_middleware.enumeration.ValueType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormElementRequest {
    @JsonProperty("form_element_name")
    private String formElementName;
    @JsonProperty("form_element_desc")
    private String formElementDescription;
    @JsonProperty("form_element_label")
    private String formElementLabel;
    @JsonProperty("form_element_value_type")
    private ValueType formElementValueType;
    @JsonProperty("form_element_type")
    private FormElementType formElementType;
    @JsonProperty("form_element_place_holder")
    private String formElementPlaceHolder;
    @JsonProperty("form_element_status")
    private Status formElementStatus;
    @JsonProperty("form_element_field_no")
    private Integer formElementFieldNo;
    @JsonProperty("form_element_select_item")
    private FormElementSelectItem formElementSelectItem;
    @JsonProperty("form_element_fetch_detail")
    private Boolean formElementFetchDetail;
    @JsonProperty("form_element_no_input_field")
    private Boolean formElementNoInputField;
    @JsonProperty("form_element_fetch_info_url")
    private String formElementFetchInfoUrl;
    @JsonProperty("form_element_default_value")
    private String formElementDefaultValue;
}
