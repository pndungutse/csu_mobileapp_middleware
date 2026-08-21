package com.dsu.hope_bank_app_middleware.service;

import com.dsu.hope_bank_app_middleware.response.QrCodeGenerateResult;

public interface QrCodeService {

    /**
     * Generates a PNG QR code and saves it with an auto date-time filename.
     */
    QrCodeGenerateResult generateAndSaveQrPng(String content, int size);

    /**
     * Generates a PNG QR code and saves it using the given base filename
     * (without extension). Date-time is appended if not already present.
     */
    QrCodeGenerateResult generateAndSaveQrPng(String content, int size, String preferredFileNameBase);

    /**
     * Saves an already-provided PNG (e.g. gateway {@code qrImageBase64} / {@code qrAsImage})
     * using the given base filename.
     */
    QrCodeGenerateResult savePngBytes(byte[] pngBytes, String preferredFileNameBase);

    /**
     * Decodes base64 PNG (raw or data-URI) and saves it.
     */
    QrCodeGenerateResult saveBase64Png(String base64OrDataUri, String preferredFileNameBase);
}
