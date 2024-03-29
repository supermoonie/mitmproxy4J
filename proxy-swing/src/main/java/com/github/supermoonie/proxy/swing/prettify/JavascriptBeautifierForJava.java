package com.github.supermoonie.proxy.swing.prettify;

import org.apache.commons.io.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/12/1
 */
public class JavascriptBeautifierForJava {

    public static final JavascriptBeautifierForJava INSTANCE = new JavascriptBeautifierForJava();

    // my javascript beautifier of choice
    private static final String BEAUTIFY_JS_RESOURCE = "beautify/beautify.min.js";
    private static final String BEAUTIFY_CSS_RESOURCE = "beautify/beautify-css.min.js";
    private static final String BEAUTIFY_HTML_RESOURCE = "beautify/beautify-html.min.js";

    // name of beautifier function
    private static final String BEAUTIFY_METHOD_NAME = "js_beautify";
    private static final String CSS_BEAUTIFY_METHOD_NAME = "css_beautify";

    private final ScriptEngine jsBeautifyEngine;
    private final ScriptEngine cssBeautifyEngine;
    private final ScriptEngine htmlBeautifyEngine;

    private JavascriptBeautifierForJava() {
        jsBeautifyEngine = new ScriptEngineManager().getEngineByName("nashorn");
        cssBeautifyEngine = new ScriptEngineManager().getEngineByName("nashorn");
        htmlBeautifyEngine = new ScriptEngineManager().getEngineByName("nashorn");
        // this is needed to make self invoking function modules work
        // otherwise you won't be able to invoke your function
        try {
//            jsBeautifyEngine.eval("");
//            htmlBeautifyEngine.eval("var global = this;");
//            cssBeautifyEngine.eval("var global = this;");
            jsBeautifyEngine.eval(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(BEAUTIFY_JS_RESOURCE))));
            cssBeautifyEngine.eval(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(BEAUTIFY_CSS_RESOURCE))));
            htmlBeautifyEngine.eval(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(BEAUTIFY_HTML_RESOURCE))));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public String beautifyJavascriptCode(String javascriptCode) throws ScriptException, NoSuchMethodException {
        return (String) ((Invocable) jsBeautifyEngine).invokeFunction(BEAUTIFY_METHOD_NAME, javascriptCode);
    }

    public String beautifyCssCode(String cssCode) throws ScriptException, NoSuchMethodException {
        return (String) ((Invocable) cssBeautifyEngine).invokeFunction(CSS_BEAUTIFY_METHOD_NAME, cssCode);
    }

    public String beautifyHtmlCode(String htmlCode) throws ScriptException, NoSuchMethodException {
        return (String) ((Invocable) htmlBeautifyEngine).invokeFunction("html_beautify", htmlCode);
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException, IOException {
        String unformattedJs = "var a = 1; b = 2; var user = { name :\"Andrew\"}";

        String formattedJs = JavascriptBeautifierForJava.INSTANCE.beautifyJavascriptCode(unformattedJs);

        System.out.println(formattedJs);

        // will print out:
        //        var a = 1;
        //        b = 2;
        //        var user = {
        //            name: "Andrew"
        //        }
        String content = IOUtils.toString(new FileInputStream("/Users/supermoonie/IdeaProjects/mitmproxy4J/proxy-swing/src/main/resources/test.css"), StandardCharsets.UTF_8);
        String formattedCss = JavascriptBeautifierForJava.INSTANCE.beautifyCssCode(content);
        System.out.println(formattedCss);
//        String content = ".article .mainpage-header {font-size: 54px;font-weight: 700;" +
//                "    letter-spacing: -1.2px;\n" +
//                "    color: #212121;\n" +
//                "    line-height: 68px;\n" +
//                "    margin-bottom: 40px;\n" +
//                "    margin-left: -5px;\n" +
//                "}";
//        Parser parser = new PrettifyParser();
//        List<ParseResult> results = parser.parse("css", content);
//        StringBuilder sb = new StringBuilder();
//        for (ParseResult result : results) {
//            sb.append(content, result.getOffset(), result.getOffset() + result.getLength());
//        }
//        System.out.println(sb.toString());
        var html = "<html><head>title</head><body><h1>Hello World!</h1></body></html>";
        String s = JavascriptBeautifierForJava.INSTANCE.beautifyHtmlCode(html);
        System.out.println(s);

    }
}
