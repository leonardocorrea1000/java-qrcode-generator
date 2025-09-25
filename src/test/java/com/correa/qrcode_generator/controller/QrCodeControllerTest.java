package com.correa.qrcode_generator.controller;

import com.correa.qrcode_generator.dto.QrCodeResponse;
import com.correa.qrcode_generator.service.QrCodeGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QrCodeController.class)
class QrCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QrCodeGeneratorService qrCodeGeneratorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generate_shouldReturnOk_whenServiceSucceeds() throws Exception {
        // Given
        String textToEncode = "https://github.com/JoseCorrea";
        String expectedUrl = "https://storage.googleapis.com/bucket/qrcode.png";
        QrCodeResponse serviceResponse = new QrCodeResponse(expectedUrl);

        when(qrCodeGeneratorService.generate(textToEncode)).thenReturn(serviceResponse);

        String requestJson = "{\"text\":\"" + textToEncode + "\"}";

        // When & Then
        mockMvc.perform(post("/api/qrcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(expectedUrl));
    }

    @Test
    void generate_shouldReturnInternalServerError_whenServiceFails() throws Exception {
        // Given
        String textToEncode = "some text";
        when(qrCodeGeneratorService.generate(anyString())).thenThrow(new IOException("Storage failed"));

        String requestJson = "{\"text\":\"" + textToEncode + "\"}";

        // When & Then
        mockMvc.perform(post("/api/qrcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }
}
