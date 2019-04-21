package com.tuaier.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tuaier
 */
public class TuaierView {

    private final String DEFAULT_CONTENT_TYPE = "text/html,charset=utf-8";

    private static final Pattern PATTERN =  Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);

    private File file;

    public TuaierView(File file) {
        this.file = file;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.file, "r");
        String line = null;
        while (null != (line = ra.readLine())) {
            line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
            Matcher matcher = PATTERN.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}", "");
                Object paramValue = model.get(paramName);
                if (null == paramName) {
                    continue;
                }
                line = matcher.replaceAll(makeStringForRegExp(paramValue.toString()));
                matcher = PATTERN.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());
    }

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
