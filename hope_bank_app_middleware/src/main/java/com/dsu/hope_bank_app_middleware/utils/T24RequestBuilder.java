package com.dsu.hope_bank_app_middleware.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public final class T24RequestBuilder {

    private T24RequestBuilder() {
    }

    public static <T> T24Request<T> build(
            String url,
            String senderReference,
            String serviceSource,
            String token,
            String tokenPassword,
            T payload
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("Sender-Reference", senderReference);
        headers.set("Service-Source", serviceSource);
        headers.set("Token", token);
        headers.set("Token-Password", tokenPassword);
        return new T24Request<>(url, new HttpEntity<>(payload, headers));
    }

    public static final class T24Request<T> {
        private final String url;
        private final HttpEntity<T> entity;

        public T24Request(String url, HttpEntity<T> entity) {
            this.url = url;
            this.entity = entity;
        }

        public String getUrl() {
            return url;
        }

        public HttpEntity<T> getEntity() {
            return entity;
        }
    }
}
