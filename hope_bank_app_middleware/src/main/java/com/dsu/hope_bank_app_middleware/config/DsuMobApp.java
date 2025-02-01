package com.dsu.hope_bank_app_middleware.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dsumobapp")
public class DsuMobApp {
    private String t24_base_url;
    private String t24_token;
    private String t24_sender_reference;
    private String t24_service_source;
    private String t24_token_password;


    public String getT24_base_url() {
        return t24_base_url;
    }

    public void setT24_base_url(String t24_base_url) {
        this.t24_base_url = t24_base_url;
    }

    public String getT24_token() {
        return t24_token;
    }

    public void setT24_token(String t24_token) {
        this.t24_token = t24_token;
    }

    public String getT24_sender_reference() {
        return t24_sender_reference;
    }

    public void setT24_sender_reference(String t24_sender_reference) {
        this.t24_sender_reference = t24_sender_reference;
    }

    public String getT24_service_source() {
        return t24_service_source;
    }

    public void setT24_service_source(String t24_service_source) {
        this.t24_service_source = t24_service_source;
    }

    public String getT24_token_password() {
        return t24_token_password;
    }

    public void setT24_token_password(String t24_token_password) {
        this.t24_token_password = t24_token_password;
    }
}
