package com.correa.qrcode_generator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.correa.qrcode_generator.core.StorageInterface;
import com.correa.qrcode_generator.dto.QrCodeResponse;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QrCodeGeneratorService {
    private final StorageInterface storage;

    public QrCodeGeneratorService(StorageInterface storage) {
        this.storage = storage;
    }

    public QrCodeResponse generate(String text) throws WriterException, IOException {   
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
                
        String fileName = "qrcode-" + System.currentTimeMillis() + ".png";
        String url = storage.store(pngData, fileName, "image/png");
        return new QrCodeResponse(url);
    }

}
