package com.dsu.hope_bank_app_middleware.navigations.request;

import com.dsu.hope_bank_app_middleware.navigations.enumeration.FormElementType;
import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.ValueType;
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
}
