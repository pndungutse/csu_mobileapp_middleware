package com.dsu.hope_bank_app_middleware.bank_services.entity;

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
@Document(collection = "bank_services")
public class BankService {
    @Id
    private String id;
    private String serviceName;
    private String serviceIcon;
    private Date dateAdded;
    private Date dateUpdate;
}
