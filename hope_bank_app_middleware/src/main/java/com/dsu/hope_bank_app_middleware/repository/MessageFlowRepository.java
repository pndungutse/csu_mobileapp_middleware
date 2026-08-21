package com.dsu.hope_bank_app_middleware.repository;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.entity.MessageFlow;
import com.mongodb.client.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Reads {@code message_flows} from the IPS T24 gateway Mongo database
 * ({@code ips_t24_gateway} by default), not the middleware app database.
 */
@Repository
public class MessageFlowRepository {
    private static final Logger logger = Logger.getLogger(MessageFlowRepository.class.getName());
    private static final String DEFAULT_DB = "ips_t24_gateway";
    private static final String T24_TO_IPS = "T24_TO_IPS";

    private final MongoTemplate ipsGatewayMongoTemplate;

    public MessageFlowRepository(MongoClient mongoClient, DsuMobApp dsuMobApp) {
        String dbName = dsuMobApp.getIps_t24_gateway_db();
        if (dbName == null || dbName.trim().isEmpty()) {
            dbName = DEFAULT_DB;
        }
        this.ipsGatewayMongoTemplate = new MongoTemplate(mongoClient, dbName.trim());
    }

    /**
     * True when a T24→IPS flow exists with the same msgId and a matching uetr
     * (document field or embedded in raw/processed message XML).
     */
    public boolean existsSentToIps(String uetr, String msgId) {
        if (isBlank(uetr) || isBlank(msgId)) {
            return false;
        }

        String trimmedUetr = uetr.trim();
        String trimmedMsgId = msgId.trim();
        String uetrRegex = Pattern.quote(trimmedUetr);
        String msgIdRegex = "^" + Pattern.quote(trimmedMsgId) + "$";

        Criteria msgIdMatch = new Criteria().orOperator(
                Criteria.where("msgId").regex(msgIdRegex, "i"),
                Criteria.where("trackingId").regex(msgIdRegex, "i")
        );
        Criteria uetrMatch = new Criteria().orOperator(
                Criteria.where("uetr").regex("^" + uetrRegex + "$", "i"),
                Criteria.where("rawMessage").regex(uetrRegex, "i"),
                Criteria.where("processedMessage").regex(uetrRegex, "i")
        );

        Query query = new Query(new Criteria().andOperator(
                Criteria.where("direction").regex("^" + T24_TO_IPS + "$", "i"),
                msgIdMatch,
                uetrMatch
        ));
        query.limit(1);

        try {
            return ipsGatewayMongoTemplate.exists(query, MessageFlow.class);
        } catch (Exception e) {
            logger.log(Level.WARNING,
                    "Could not query ips_t24_gateway.message_flows for msgId={0} uetr={1}: {2}",
                    new Object[]{trimmedMsgId, trimmedUetr, e.getMessage()});
            return false;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
