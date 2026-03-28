package com.dsu.hope_bank_app_middleware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data

@Configuration
@ConfigurationProperties(prefix = "dsumobapp")
public class DsuMobApp {
    private String t24_base_url;
    private String t24_token;
    private String t24_sender_reference;
    private String t24_service_source;
    private String t24_token_password;

    private String self_registration_txn_type;
    private String mini_statement_txn_type;
    private String account_information_txn_type;
    private String internal_transfer_txn_type;
    private String loan_balance_txn_type;
    private String account_balance_txn_type;
    private String loan_repayment_txn_type;
    private String loan_repayment_momo_txn_type;
    private String airtime_top_up_txn_type;
    private String transfer_to_other_bank_txn_type;
    private String transfer_bank_to_wallet_txn_type;
    private String merchant_payment_txn_type;
    private String cardless_withdraw_txn_type;
    private String agent_withdraw_txn_type;
    private String gimac_bank_wallet;
    private String gimac_wallet_bank;
    private String bank_ips;
    private String bank_ips_pay_qr;
    private String ips_name_lookup_url;
    private String ips_qr_read_url;
    private String ussd_code;
    private String sms_api_key;
    private String sms_base_url;

}
