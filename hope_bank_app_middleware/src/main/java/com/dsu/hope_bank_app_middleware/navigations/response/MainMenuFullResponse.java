package com.dsu.hope_bank_app_middleware.navigations.response;

import com.dsu.hope_bank_app_middleware.exception.SuccessResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainMenuFullResponse {
    @JsonProperty("content")
    private List<MainMenuResponse> content;
    @JsonProperty("page_no")
    private int pageNo;
    @JsonProperty("page_size")
    private int pageSize;
    @JsonProperty("total_elements")
    private long totalElements;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("last")
    private boolean last;
    @JsonProperty("success_response")
    private SuccessResponse successResponse;
}
