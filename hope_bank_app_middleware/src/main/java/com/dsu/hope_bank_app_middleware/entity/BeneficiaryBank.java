package com.dsu.hope_bank_app_middleware.entity;

import com.dsu.hope_bank_app_middleware.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "beneficiary_bank")
public class BeneficiaryBank {
    @Id
    private String Id;
    private String beneficiaryCode;
    private String beneficiaryName;
    private Status beneficiaryStatus;
    private Date beneficiaryAddedDate;
    private Date beneficiaryUpdatedDate;
}
