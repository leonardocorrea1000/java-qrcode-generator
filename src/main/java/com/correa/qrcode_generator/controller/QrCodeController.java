package com.correa.qrcode_generator.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.correa.qrcode_generator.dto.QrCodeRequest;
import com.correa.qrcode_generator.dto.QrCodeResponse;
import com.correa.qrcode_generator.service.QrCodeGeneratorService;
import com.google.zxing.WriterException;

@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {

    private final QrCodeGeneratorService qrCodeGeneratorService;

    public QrCodeController(QrCodeGeneratorService qrCodeGeneratorService) {
        this.qrCodeGeneratorService = qrCodeGeneratorService;
    }

    @PostMapping()
    public ResponseEntity<QrCodeResponse> generate(@RequestBody QrCodeRequest request) {
        // Lógica para generar el código QR
        try {
            QrCodeResponse response = this.qrCodeGeneratorService.generate(request.text());
            return ResponseEntity.ok(response);            
            
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(500).build();
        }

        

    }

   
    
}
