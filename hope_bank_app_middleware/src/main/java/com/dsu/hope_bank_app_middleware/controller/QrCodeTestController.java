package com.dsu.hope_bank_app_middleware.controller;

import com.dsu.hope_bank_app_middleware.request.QrCodeImageRequest;
import com.dsu.hope_bank_app_middleware.response.QrCodeGenerateResult;
import com.dsu.hope_bank_app_middleware.service.QrCodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test endpoints for generating a scannable QR code image from a URL.
 * Each generation also saves a PNG under the configured PC folder (date-time filename).
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/test/qr")
public class QrCodeTestController {

    private static final String SAMPLE_QR_URL =
            "https://qr.brb.bi/1/m/BRB/TUR28826657004741348c2ba6c1a50e23a1";

    private final QrCodeService qrCodeService;

    public QrCodeTestController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    /**
     * Returns a PNG QR image for the sample URL and saves it to disk.
     * Open in browser: GET /api/v1/test/qr/sample
     */
    @GetMapping(value = "/sample", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateSampleQr(
            @RequestParam(value = "size", required = false, defaultValue = "300") int size) {
        QrCodeGenerateResult result = qrCodeService.generateAndSaveQrPng(SAMPLE_QR_URL, size);
        return pngResponse(result);
    }

    /**
     * Same as /sample but returns JSON with the saved file path (easy to locate on disk).
     * GET /api/v1/test/qr/sample/info
     */
    @GetMapping("/sample/info")
    public ResponseEntity<Map<String, Object>> generateSampleQrInfo(
            @RequestParam(value = "size", required = false, defaultValue = "300") int size) {
        QrCodeGenerateResult result = qrCodeService.generateAndSaveQrPng(SAMPLE_QR_URL, size);
        return ResponseEntity.ok(toInfoMap(result, SAMPLE_QR_URL));
    }

    /**
     * Returns a PNG QR image for a custom URL and saves it to disk.
     * Body: { "qr_url": "https://...", "size": 300 }
     */
    @PostMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrImage(@RequestBody QrCodeImageRequest request) {
        String url = request != null ? request.getQrUrl() : null;
        int size = request != null && request.getSize() != null ? request.getSize() : 300;
        QrCodeGenerateResult result = qrCodeService.generateAndSaveQrPng(url, size);
        return pngResponse(result);
    }

    /**
     * Generates QR, saves to disk, returns JSON with file location.
     * Body: { "qr_url": "https://...", "size": 300 }
     */
    @PostMapping("/image/info")
    public ResponseEntity<Map<String, Object>> generateQrImageInfo(@RequestBody QrCodeImageRequest request) {
        String url = request != null ? request.getQrUrl() : null;
        int size = request != null && request.getSize() != null ? request.getSize() : 300;
        QrCodeGenerateResult result = qrCodeService.generateAndSaveQrPng(url, size);
        return ResponseEntity.ok(toInfoMap(result, url));
    }

    private ResponseEntity<byte[]> pngResponse(QrCodeGenerateResult result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(result.getPngBytes().length);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + result.getFileName() + "\"");
        headers.set("X-QR-File-Name", result.getFileName());
        headers.set("X-QR-Saved-Path", result.getSavedFilePath());
        return ResponseEntity.ok().headers(headers).body(result.getPngBytes());
    }

    private Map<String, Object> toInfoMap(QrCodeGenerateResult result, String qrUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("retCode", "00");
        body.put("message", "QR code generated and saved");
        body.put("qrUrl", qrUrl);
        body.put("fileName", result.getFileName());
        body.put("savedFilePath", result.getSavedFilePath());
        return body;
    }
}
