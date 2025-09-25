package com.correa.qrcode_generator.core;

import java.io.IOException;

public interface StorageInterface {
    String store(byte[] data, String fileName, String format) throws IOException;
    
}
