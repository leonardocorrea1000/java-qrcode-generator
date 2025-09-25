package com.correa.qrcode_generator.infra;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

@ExtendWith(MockitoExtension.class)
class GoogleCloudStorageAdapterTest {

    @Mock
    private Storage mockStorage;

    private GoogleCloudStorageAdapter storageAdapter;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        // Use the test-only constructor to inject the mock Storage
        storageAdapter = new GoogleCloudStorageAdapter(BUCKET_NAME, mockStorage);
    }

    @Test
    void store_shouldUploadFileAndReturnPublicUrl() throws IOException {
        // Given
        byte[] data = "test-data".getBytes();
        String originalFileName = "my-file.txt";
        String format = "text/plain";

        // When
        String publicUrl = storageAdapter.store(data, originalFileName, format);

        // Then
        ArgumentCaptor<BlobInfo> blobInfoCaptor = ArgumentCaptor.forClass(BlobInfo.class);
        ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);

        verify(mockStorage).createFrom(blobInfoCaptor.capture(), inputStreamCaptor.capture());

        BlobInfo capturedBlobInfo = blobInfoCaptor.getValue();
        assertEquals(BUCKET_NAME, capturedBlobInfo.getBucket());
        assertEquals(format, capturedBlobInfo.getContentType());
        assertTrue(capturedBlobInfo.getName().contains("_" + originalFileName));

        // Check the returned URL format
        String expectedUrlPattern = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, capturedBlobInfo.getName());
        assertEquals(expectedUrlPattern, publicUrl);
    }
}
