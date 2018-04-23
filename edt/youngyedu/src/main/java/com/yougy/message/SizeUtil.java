package com.yougy.message;

import java.math.BigDecimal;

/**
 * Created by FH on 2017/4/6.
 */

public class SizeUtil {
    private static final long KB = 1024;
    private static final long MB = KB*KB;
    private static final long GB = MB*MB;
    /**
     * 转换字节数的long值为字符串,自动转换为KB,MB,GB等单位.
     * @param size 要转换的字节数long值,自动转换成KB,MB,GB等单位,保留1位小数,采用四舍五入形式
     * @return 转换好的字符串
     */
    public static String convertSizeLong2String(long size){
        return convertSizeLong2String(size , 1 , BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 转换字节数的long值为字符串,自动转换为KB,MB,GB等单位.
     * @param size 要转换的字节数long值
     * @param scale 要保留的小数点后位数
     * @param scaleType 保留小数时采用的模式,如四舍五入(BigDecimal.ROUND_HALF_UP),具体见 {@link BigDecimal}
     * @return 转换好的字符串
     */
    public static String convertSizeLong2String(long size , int scale , int scaleType){
        if (size < KB){
            return size + "B";
        }
        else if (size < MB){
            return doScale(((double) size)/((double) KB) , scale , scaleType) + "KB";
        }
        else if (size < GB){
            return doScale(((double) size)/((double) MB) , scale , scaleType) + "MB";
        }
        else {
            return doScale(((double) size)/((double) GB) , scale , scaleType) + "GB";
        }
    }


    /**
     * 对小数进行小数点后的位数的裁剪,采用四舍五入的模式.
     * @param origin 要裁剪double类型的小数
     * @param scaleDigit 小数点后保留的位数
     * @return 进行裁剪后的小数的字符串
     */
    public static String doScale(double origin , int scaleDigit){
        return doScale(origin , scaleDigit , BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 对小数进行小数点后的位数的裁剪
     * @param origin 要裁剪double类型的小数
     * @param scaleDigit 小数点后保留的位数
     * @param scaleType 保留小数时采用的模式,如四舍五入(BigDecimal.ROUND_HALF_UP),具体见 {@link BigDecimal}
     * @return 进行裁剪后的小数的字符串
     */
    public static String doScale(double origin , int scaleDigit , int scaleType){
        BigDecimal bd = new BigDecimal(origin);
        bd = bd.setScale(scaleDigit , scaleType);
        return bd.toString();
    }

    /**
     * 对小数进行小数点后的位数的裁剪,采用四舍五入的模式.
     * @param origin 要裁剪double类型的小数
     * @param scaleDigit 小数点后保留的位数
     * @return 进行裁剪后的小数的double
     */
    public static double doScale_double(double origin , int scaleDigit){
        return doScale_double(origin , scaleDigit , BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 对小数进行小数点后的位数的裁剪
     * @param origin 要裁剪double类型的小数
     * @param scaleDigit 小数点后保留的位数
     * @param scaleType 保留小数时采用的模式,如四舍五入(BigDecimal.ROUND_HALF_UP),具体见 {@link BigDecimal}
     * @return 进行裁剪后的小数的double
     */
    public static double doScale_double(double origin , int scaleDigit , int scaleType){
        BigDecimal bd = new BigDecimal(origin);
        bd = bd.setScale(scaleDigit , scaleType);
        return bd.doubleValue();
    }

}
