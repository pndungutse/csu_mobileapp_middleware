package com.dsu.hope_bank_app_middleware.service.serviceImpl;

import com.dsu.hope_bank_app_middleware.config.DsuMobApp;
import com.dsu.hope_bank_app_middleware.response.QrCodeGenerateResult;
import com.dsu.hope_bank_app_middleware.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    private static final Logger logger = Logger.getLogger(QrCodeServiceImpl.class.getName());
    private static final DateTimeFormatter FILE_TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String DEFAULT_STORAGE_DIR = "qr-codes";

    @Autowired
    private DsuMobApp dsuMobApp;

    @Override
    public QrCodeGenerateResult generateAndSaveQrPng(String content, int size) {
        return generateAndSaveQrPng(content, size, null);
    }

    @Override
    public QrCodeGenerateResult generateAndSaveQrPng(String content, int size, String preferredFileNameBase) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("QR content/url is required");
        }
        if (size < 100) {
            size = 100;
        }
        if (size > 1000) {
            size = 1000;
        }

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content.trim(), BarcodeFormat.QR_CODE, size, size, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return savePngBytes(outputStream.toByteArray(), preferredFileNameBase);
        } catch (WriterException | IOException e) {
            logger.log(Level.SEVERE, "Failed to generate QR image: {0}", e.getMessage());
            throw new RuntimeException("Failed to generate or save QR code image", e);
        }
    }

    @Override
    public QrCodeGenerateResult saveBase64Png(String base64OrDataUri, String preferredFileNameBase) {
        if (base64OrDataUri == null || base64OrDataUri.trim().isEmpty()) {
            throw new IllegalArgumentException("QR image base64 is required");
        }
        String payload = base64OrDataUri.trim();
        int comma = payload.indexOf(',');
        if (payload.startsWith("data:") && comma > 0) {
            payload = payload.substring(comma + 1);
        }
        payload = payload.replaceAll("\\s", "");
        try {
            byte[] pngBytes = Base64.getDecoder().decode(payload);
            return savePngBytes(pngBytes, preferredFileNameBase);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid QR image base64 from gateway", e);
        }
    }

    @Override
    public QrCodeGenerateResult savePngBytes(byte[] pngBytes, String preferredFileNameBase) {
        if (pngBytes == null || pngBytes.length == 0) {
            throw new IllegalArgumentException("QR PNG bytes are required");
        }
        try {
            Path savedPath = saveToDisk(pngBytes, preferredFileNameBase);
            logger.log(Level.INFO, "QR PNG saved to: {0}", savedPath.toAbsolutePath());
            return QrCodeGenerateResult.builder()
                    .pngBytes(pngBytes)
                    .fileName(savedPath.getFileName().toString())
                    .savedFilePath(savedPath.toAbsolutePath().toString())
                    .build();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save QR image: {0}", e.getMessage());
            throw new RuntimeException("Failed to save QR code image", e);
        }
    }

    private Path saveToDisk(byte[] pngBytes, String preferredFileNameBase) throws IOException {
        Path folder = resolveStorageFolder();
        Files.createDirectories(folder);

        String timestamp = LocalDateTime.now().format(FILE_TS);
        String fileName;
        if (preferredFileNameBase != null && !preferredFileNameBase.trim().isEmpty()) {
            String base = sanitizeFileName(preferredFileNameBase.trim());
            if (!base.toLowerCase().endsWith(".png")) {
                fileName = base + "-" + timestamp + ".png";
            } else {
                fileName = base.substring(0, base.length() - 4) + "-" + timestamp + ".png";
            }
        } else {
            fileName = "qr_" + timestamp + ".png";
        }

        Path filePath = folder.resolve(fileName);
        int suffix = 1;
        while (Files.exists(filePath)) {
            String unique = fileName.substring(0, fileName.length() - 4) + "_" + suffix + ".png";
            filePath = folder.resolve(unique);
            suffix++;
        }

        Files.write(filePath, pngBytes);
        return filePath;
    }

    private String sanitizeFileName(String name) {
        String cleaned = name
                .replaceAll("[\\\\/:*?\"<>|]", "-")
                .replaceAll("\\s+", "_")
                .replaceAll("-+", "-")
                .replaceAll("_+", "_");
        if (cleaned.isEmpty()) {
            return "qr";
        }
        if (cleaned.length() > 120) {
            cleaned = cleaned.substring(0, 120);
        }
        return cleaned;
    }

    private Path resolveStorageFolder() {
        String configured = dsuMobApp.getQr_code_storage_path();
        Path folder = toOsPath(configured);
        return folder.toAbsolutePath().normalize();
    }

    private Path toOsPath(String configured) {
        String userHome = System.getProperty("user.home");

        if (configured == null || configured.trim().isEmpty()) {
            return Paths.get(userHome, DEFAULT_STORAGE_DIR);
        }

        String path = configured.trim()
                .replace('\\', '/')
                .replace("${user.home}", userHome.replace('\\', '/'));

        if (path.startsWith("~/") || path.equals("~")) {
            path = userHome.replace('\\', '/') + path.substring(1);
        }

        return Paths.get(path);
    }
}
