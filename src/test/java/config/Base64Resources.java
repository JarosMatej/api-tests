package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Base64Resources {

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    private Base64Resources() {
    }

    public static String getDocData() {
        return load("/base64/docData.txt");
    }

    public static String getDocDataNegativePathMissingParams() {
        return load("/base64/docDataNegativePathMissingParams.txt");

    }

    public static String getSigBioData() {
        return load("/base64/sigBioData.txt");
    }

    public static String getSigImgBase64() {
        return load("/base64/sigImgBase64.txt");
    }

    public static String getDataUploadAttachmentInvalidData() {
        return load("/base64/dataUploadAttachmentInvalidData.txt");
    }

    public static String getDataUploadAttachment() {
        return load("/base64/dataUploadAttachment.txt");
    }

    public static String getDocumentsCreateContract() {
        return load("/base64/documentsCreateContract.txt");
    }

    private static String load(String path) {
        return CACHE.computeIfAbsent(path, p -> {
            try (InputStream is = Base64Resources.class.getResourceAsStream(p)) {
                if (is == null) {
                    throw new IOException("Resource not found: " + p);
                }
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ExceptionInInitializerError("Failed to load resource " + p + ": " + e.getMessage());
            }
        });
    }
}
