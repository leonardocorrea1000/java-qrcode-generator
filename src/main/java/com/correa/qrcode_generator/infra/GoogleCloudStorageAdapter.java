package com.correa.qrcode_generator.infra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.correa.qrcode_generator.core.StorageInterface;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Component
public class GoogleCloudStorageAdapter implements StorageInterface {

    private String bucketName;
    private final Storage storage;

    public GoogleCloudStorageAdapter(
            @Value("${gcs.bucket-name}") String bucketName,
            @Value("${gcs.credentials-path}") String credentialsPath) {
        try {
            this.bucketName = bucketName;
            StorageOptions.Builder builder = StorageOptions.newBuilder();

            if (!credentialsPath.isEmpty()) {
                ClassPathResource resource = new ClassPathResource(credentialsPath);
                try (InputStream serviceAccount = resource.getInputStream()) {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                    builder.setCredentials(credentials);
                }
            }
            this.storage = builder.build().getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Google Cloud Storage", e);
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
