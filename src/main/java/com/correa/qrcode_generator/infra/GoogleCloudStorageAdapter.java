package com.correa.qrcode_generator.infra;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.correa.qrcode_generator.core.StorageInterface;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Component("googleCloudStorageAdapter")
@ConditionalOnProperty(name = "gcs.enabled", havingValue = "true", matchIfMissing = true)
public class GoogleCloudStorageAdapter implements StorageInterface {

    private final String bucketName;
    private final Storage storage;

    public GoogleCloudStorageAdapter(
            @Value("${gcs.bucket-name}") String bucketName,
            @Value("${gcs.project-id}") String projectId,
            @Value("${gcs.credentials-path}") String credentialsPath) throws IOException {
        this.bucketName = bucketName;
        this.storage = buildStorageService(projectId, credentialsPath);
    }

    GoogleCloudStorageAdapter(String bucketName, Storage storage) {
        this.bucketName = bucketName;
        this.storage = storage;
    }

    private Storage buildStorageService(String projectId, String credentialsPath) throws IOException {
        try {
            StorageOptions.Builder builder = StorageOptions.newBuilder().setProjectId(projectId);

            if (credentialsPath != null && !credentialsPath.isEmpty()) {
                try (InputStream serviceAccount = new FileInputStream(credentialsPath)) {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                    builder.setCredentials(credentials);
                }
            }
            return builder.build().getService();
        } catch (IOException e) {
            throw new IOException("Failed to initialize Google Cloud Storage. Check credentials path and permissions.", e);
        }
    }

    @Override
    public String store(byte[] data, String fileName, String format) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uniqueFileName)
                .setContentType(format)
                .build();

        try (InputStream content = new ByteArrayInputStream(data)) {
            storage.createFrom(blobInfo, content);
        }

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, uniqueFileName);
    }

}
