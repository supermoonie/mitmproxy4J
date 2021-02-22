package com.github.supermoonie.proxy.fx.util;

import org.brotli.dec.BrotliInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author supermoonie
 * @since 2020/10/15
 */
public class BrUtil {

    public static byte[] decompress(byte[] data, boolean byByte) throws IOException {
        byte[] buffer = new byte[65536];
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (BrotliInputStream brotliInput = new BrotliInputStream(input)) {
            if (byByte) {
                byte[] oneByte = new byte[1];
                while (true) {
                    int next = brotliInput.read();
                    if (next == -1) {
                        break;
                    }
                    oneByte[0] = (byte) next;
                    output.write(oneByte, 0, 1);
                }
            } else {
                while (true) {
                    int len = brotliInput.read(buffer, 0, buffer.length);
                    if (len <= 0) {
                        break;
                    }
                    output.write(buffer, 0, len);
                }
            }
        }
        return output.toByteArray();
    }
}
