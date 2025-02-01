package com.dsu.hope_bank_app_middleware.navigations.response;

import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.FormElementType;
import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
import com.dsu.hope_bank_app_middleware.navigations.enumeration.ValueType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormElementResponse {
    @JsonProperty("id")
    private String id;
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
    @JsonProperty("sub_menu_item_name")
    private String subMenuItemName;
    @JsonProperty("form_element_added_date")
    private Date formElementAddedDate;
    @JsonProperty("form_element_updated_date")
    private Date formElementUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
