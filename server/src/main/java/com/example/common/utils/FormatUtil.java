package com.example.common.utils;

import cn.hutool.http.HtmlUtil;

/**
 * @author tycoding
 */
public class FormatUtil {

    public static String replaceHtml(String content) {
        String str = HtmlUtil.cleanHtmlTag(content).trim()
                .replaceAll(" ", "")
                .replaceAll("　　", "")
                .replaceAll("　　　　", "")
                .replaceAll("\\s*|\t|\r|\n", "")
                .replaceAll("&ensp;", "")
                .replaceAll("&ldquo", "")
                .replaceAll("&nbsp;", "");
        if (str.length() > 71) {
            return str.substring(0, 70) + "...";
        }
        return str + "...";
    }
}
