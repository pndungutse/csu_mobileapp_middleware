package com.dsu.hope_bank_app_middleware.utils;

public class T24EnvironmentConfig {
    private final String url;
    private final String senderReference;
    private final String serviceSource;
    private final String token;
    private final String tokenPassword;

    public T24EnvironmentConfig(String url, String senderReference, String serviceSource, String token, String tokenPassword) {
        this.url = url;
        this.senderReference = senderReference;
        this.serviceSource = serviceSource;
        this.token = token;
        this.tokenPassword = tokenPassword;
    }

    public String getUrl() {
        return url;
    }

    public String getSenderReference() {
        return senderReference;
    }

    public String getServiceSource() {
        return serviceSource;
    }

    public String getToken() {
        return token;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }
}
