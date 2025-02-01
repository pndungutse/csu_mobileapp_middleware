package com.dsu.hope_bank_app_middleware.navigations.entity;

import com.dsu.hope_bank_app_middleware.general_enumerations.Status;
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
@Document(collection = "associate_banks")
public class AssociateBank {
    @Id
    private String id;
    private String associateBankName;
    private Status associateBankStatus;
    private Date associateBankAddedDate;
    private Date associateBankUpdatedDate;
}
