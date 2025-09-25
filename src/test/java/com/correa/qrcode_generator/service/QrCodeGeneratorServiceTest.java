package com.correa.qrcode_generator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.correa.qrcode_generator.core.StorageInterface;
import com.correa.qrcode_generator.dto.QrCodeResponse;

@ExtendWith(MockitoExtension.class)
class QrCodeGeneratorServiceTest {

    @Mock
    private StorageInterface storage;

    @InjectMocks
    private QrCodeGeneratorService qrCodeGeneratorService;

    @Test
    void generate_shouldCreateQrCodeAndStoreIt() throws Exception {
        // Given
        String text = "test-data";
        String expectedUrl = "http://storage.com/qrcode.png";
        when(storage.store(any(byte[].class), anyString(), anyString())).thenReturn(expectedUrl);

        // When
        QrCodeResponse response = qrCodeGeneratorService.generate(text);

        // Then
        assertNotNull(response);
        assertEquals(expectedUrl, response.url());

        // Verify that storage.store was called with the correct parameters
        ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> formatCaptor = ArgumentCaptor.forClass(String.class);

        verify(storage).store(dataCaptor.capture(), fileNameCaptor.capture(), formatCaptor.capture());

        assertEquals("image/png", formatCaptor.getValue());
        assertNotNull(dataCaptor.getValue());
        assertEquals(true, dataCaptor.getValue().length > 0);
        assertEquals(true, fileNameCaptor.getValue().startsWith("qrcode-"));
        assertEquals(true, fileNameCaptor.getValue().endsWith(".png"));
    }
}
