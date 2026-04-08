package com.dsu.hope_bank_app_middleware.utils;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.exception.CustomAPIException;
import org.springframework.http.HttpStatus;

public final class T24EnvironmentResolver {

    private T24EnvironmentResolver() {
    }

    public static T24EnvironmentConfig resolve(DsuMobApp dsuMobApp, String environment) {
        String env = environment == null ? "TURAME" : environment.trim();
        if ("CONGO".equalsIgnoreCase(env)) {
            return new T24EnvironmentConfig(
                    dsuMobApp.getT24_congo_base_url(),
                    dsuMobApp.getT24_congo_sender_reference(),
                    dsuMobApp.getT24_congo_service_source(),
                    dsuMobApp.getT24_congo_token(),
                    dsuMobApp.getT24_congo_token_password()
            );
        }
        if ("TURAME".equalsIgnoreCase(env) || env.isEmpty()) {
            return new T24EnvironmentConfig(
                    dsuMobApp.getT24_base_url(),
                    dsuMobApp.getT24_sender_reference(),
                    dsuMobApp.getT24_service_source(),
                    dsuMobApp.getT24_token(),
                    dsuMobApp.getT24_token_password()
            );
        }
        throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Unsupported environment: " + environment);
    }
}
