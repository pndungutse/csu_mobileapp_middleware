package com.dsu.hope_bank_app_middleware.entity.navigations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "icons")
public class IconItem {
    @Id
    private String id;
    private String iconName;
    private String icon;
    private Date iconAddedDate;
    private Date iconUpdatedDate;
}
