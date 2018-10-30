package com.yougy.common.utils;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/7/19.
 */
public class StringUtils {
    /** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
    public static boolean isEmpty(String value) {
        return !(value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim()));
    }

    /** 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true */
    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }

    /**
     * 按照给出的最大字符串长度裁剪字符串,如果超出最大长度,则裁剪后添加省略号,否则原样返回
     * @param str 要裁剪的字符串
     * @param maxWords 最大长度
     * @return 裁剪后的字符串
     */
    public static String cutString(String str , int maxWords){
        if (TextUtils.isEmpty(str)){
            return str;
        }
        if (str.length() > maxWords){
            return str.substring(0 , maxWords) + "...";
        }
        else {
            return str;
        }
    }

    /**
     * 本方法能够自动将给定的多个字符串拼接成一个整串,并且支持在每个子串的前后添加自定义的字符串,支持自定义子串之间的分隔符,支持为拼接结果添加整体的前缀和后缀.
     * 拼接结果格式为 globalProfix+substringProfix+subString1+substringPostfix+separator+substringProfix+subString2+substringPostfix+.....+globalPostfix
     * 举例 例如 smartCombineStrings("[" , "]" , "(" , ")" , "," , "aaa" , "bbb" , "ccc")会输出"[(aaa),(bbb),(ccc)]"这样的字符串
     * 又例如 smartCombineStrings(null , null , null , null , "+" , "111" , "222" , "333")会输出"111+222+333"这样的字符串
     * 又例如 smartCombineStrings(null , null , null , null , "+" , null)会输出""空白字符串
     * @param globalProfix 全局的前缀,会加在最终拼接结果的最前面
     * @param globalPostfix 全局的后缀,会加在最终拼接结果的最后面
     * @param substringProfix 子串的前缀,会加在每个子串的前面
     * @param substringPostfix 子串的后缀,会加在每个子串的后面
     * @param separator 子串之间的分隔符
     * @param subStrings 要拼接的子串
     * @return 各个子串拼接而成的字符串
     */
    public static String smartCombineStrings(String globalProfix , String globalPostfix
            , String substringProfix , String substringPostfix , String separator , String ... subStrings){
        String resultString = TextUtils.isEmpty(globalProfix)?"":globalProfix;
        if (subStrings != null){
            for (int i = 0; i < subStrings.length; i++) {
                if (!TextUtils.isEmpty(subStrings[i])){
                    resultString = resultString + (TextUtils.isEmpty(substringProfix)?"":substringProfix);
                    resultString = resultString + subStrings[i];
                    resultString = resultString + (TextUtils.isEmpty(substringPostfix)?"":substringPostfix);
                    if (i + 1 < subStrings.length){
                        resultString = resultString + (TextUtils.isEmpty(separator)?"":separator);
                    }
                }
            }
        }
        resultString = resultString + (TextUtils.isEmpty(globalPostfix)?"":globalPostfix);
        return resultString;
    }

}
