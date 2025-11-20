package com.dsu.hope_bank_app_middleware.response.navigations;

import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
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
public class IconResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("icon_name")
    private String iconName;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("form_element_added_date")
    private Date formElementAddedDate;
    @JsonProperty("form_element_updated_date")
    private Date formElementUpdatedDate;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
