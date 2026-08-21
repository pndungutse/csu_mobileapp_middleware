package com.dsu.hope_bank_app_middleware.entity;

import com.dsu.hope_bank_app_middleware.enumeration.Status;
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
@Document(collection = "ips_qr_code")
public class IpsQrCode {
    @Id
    private String id;

    private String qrType;
    private String creditorName;
    private String creditorAccount;
    private String memberId;
    private String agentIdType;
    private String currency;
    private String amount;
    private String qrCodeUrl;
    private String uetr;
    private String qrHeaderUUID;
    private String ourReference;
    private String fileName;
    private String savedFilePath;
    private String qrImageBase64;
    private Integer ttlLength;
    private String ttlUnits;

    /** Account that created this QR code. */
    private String createdByAccount;
    /** Customer number of the creator, when available. */
    private String createdByCustomerNumber;

    private Status status;
    private Date createdDate;
    private Date updatedDate;
}
