package com.example.generator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author administrator
 * @date 2020/07/16
 * @description: 类描述: 字符串工具类
 **/
public class StrUtil {

    private StrUtil() {
    }

    /**
     * 下划线接一个字母或数字
     */
    private static final Pattern UNDERLINE_WITH_CHAR = Pattern.compile("(_[A-Za-z0-9])");

    /**
     * 下划线转驼峰
     *
     * @param underLineString 下换线字符串
     * @return r
     */
    public static String convertUnderLineToCamelCase(String underLineString) {
        underLineString = underLineString.toLowerCase();
        StringBuilder resultBuilder = new StringBuilder(underLineString);
        String result = underLineString;
        Matcher matcher = UNDERLINE_WITH_CHAR.matcher(resultBuilder);
        while (matcher.find()) {
            String temp = matcher.group(0);
            result = result.replace(temp, temp.replaceAll("_", "").toUpperCase());
        }
        return result;
    }

    /**
     * 下划线转驼峰,首字母大写
     *
     * @param underLineString 下换线字符串
     * @return r
     */
    public static String convertUnderLineToFirstUpperCamelCase(String underLineString) {
        String result = convertUnderLineToCamelCase(underLineString);
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    /**
     * 下划线转驼峰,首字母小写
     *
     * @param underLineString 下换线字符串
     * @return r
     */
    public static String convertUnderLineToFirstLowerCamelCase(String underLineString) {
        String result = convertUnderLineToCamelCase(underLineString);
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

}
