package com.github.supermoonie.proxy.fx.ui;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author supermoonie
 * @since 2020/10/25
 */
public class SendReqControllerTest {

    private static final Pattern TEXT_CONTENT_DISPOSITION_PATTERN = Pattern.compile("^Content-Disposition:\\s*form-data;\\s*name=\"(.*)\"");
    private static final Pattern BINARY_CONTENT_DISPOSITION_PATTERN = Pattern.compile("^Content-Disposition:\\s*form-data;\\s*name=\"(.*)\";\\s*filename=\"(.*)\"");

    @Test
    public void test_pattern() {
        String text = "Content-Disposition: form-data; name=\"file\"; filename=\"640.png\"\n" +
                "Content-Type: image/png\n" +
                "\n" +
                "�PNG";
        Matcher matcher = TEXT_CONTENT_DISPOSITION_PATTERN.matcher(text);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }

}