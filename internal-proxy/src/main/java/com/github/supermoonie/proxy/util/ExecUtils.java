package com.github.supermoonie.proxy.util;

import io.netty.util.internal.PlatformDependent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public final class ExecUtils {

    private ExecUtils() {
    }

    /**
     * 同步执行shell，阻塞当前线程
     */
    public static void execBlock(String... shell) throws IOException {
        Process process = Runtime.getRuntime().exec(shell);
        try (InputStream inputStream = process.getInputStream()) {
            byte[] bytes = new byte[8192];
            while ((inputStream.read(bytes)) != -1) {
                // ignore
            }
        } finally {
            process.destroy();
        }
    }

    /**
     * 执行shell并返回标准输出文本内容
     */
    public static String exec(String... shell) throws IOException {
        Process process = Runtime.getRuntime().exec(shell);
        StringBuilder sb = new StringBuilder();
        Charset charset = PlatformDependent.isWindows() ? Charset.forName("GBK") : Charset.defaultCharset();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } finally {
            process.destroy();
        }
        return sb.toString();
    }

    /**
     * 以管理员权限，同步执行shell，阻塞当前线程
     */
    public static void execBlockWithAdmin(String shell) throws IOException {
        //osascript -e "do shell script \"shell\" with administrator privileges"
        Process process = Runtime.getRuntime().exec(new String[]{
                "osascript",
                "-e",
                "do shell script \"" +
                        shell +
                        "\"" +
                        "with administrator privileges"
        });
        try (InputStream inputStream = process.getInputStream()) {
            byte[] bytes = new byte[8192];
            while ((inputStream.read(bytes)) != -1) {
                // ignore
            }
        } finally {
            process.destroy();
        }
    }

}
