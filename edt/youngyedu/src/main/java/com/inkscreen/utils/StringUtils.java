package com.inkscreen.utils;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String checkNullString(String str) {
        if ("null".equals(str)) {
            return null;
        }
        return str;
    }

    public static String nullString(String str) {
        if (str == null || "null".equals(str)) {
            return "";
        }
        return str;
    }

    public static String emptyStr(String str) {
        if (str == null || "null".equals(str) || "".equals(str)) {
            return null;
        }
        return str;
    }

    public static boolean isEmptyStr(String str) {
        if (str == null || "null".equals(str) || "".equals(str)) {
            return true;
        }
        return false;
    }

    public static String urlEncode(String str) {
        if (str != null) {
            return URLEncoder.encode(str);
        }
        return null;
    }

    public static String urlDecode(String str) {
        if (str != null) {
            return URLDecoder.decode(str);
        }
        return null;
    }

    public static String stringWithSeparator(List<String> ss, String sep) {
        if (ss != null && ss.size() > 0) {
            StringBuilder strb = new StringBuilder();
            for (int i = 0; i < ss.size(); i++) {
                strb.append(ss.get(i));
                if (i < ss.size() - 1) {
                    strb.append(sep);
                }
            }
            return strb.toString();
        }
        return null;
    }

    /**
     * 判断字符串的编码
     *
     * @param str
     * @return
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
            exception2.printStackTrace();
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        return null;
    }

    /*

 *  把中文字符串转换为十六进制Unicode编码字符串

 */

    public static String stringToUnicode(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            if (ch > 255)
                str += "\\u" + Integer.toHexString(ch);
            else
                str += "\\" + Integer.toHexString(ch);
        }
        return str;
    }

    public static String formatCurrencyFen(int price){
        int price1 = price / 100;

        double price2 = new BigDecimal(price / 100d).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
        if(price1==price2){
            return price1+"";
        }
        return price2+"";
    }

/*

 *  把十六进制Unicode编码字符串转换为中文字符串

 */

    public static String unicodeToString(String str) {
        if(isEmptyStr(str))return str;
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

        Matcher matcher = pattern.matcher(str);

        char ch;

        while (matcher.find()) {

            ch = (char) Integer.parseInt(matcher.group(2), 16);

            str = str.replace(matcher.group(1), ch + "");

        }

        return str;

    }
}
