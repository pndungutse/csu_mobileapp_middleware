package com.dsu.hope_bank_app_middleware.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "message_flows")
public class MessageFlow {
    @Id
    private String id;
    private String msgId;
    private String endToEndId;
    private String trackingId;
    private String uetr;
    private String messageType;
    private String direction;
    private String status;
    private String rawMessage;
    private String processedMessage;
}
