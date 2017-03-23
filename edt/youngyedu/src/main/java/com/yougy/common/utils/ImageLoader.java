package com.yougy.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jiangliang on 2016/7/5.
 */
public class ImageLoader {

    public static Bitmap loadBmpFromFile(String path) {
        try {
            FileInputStream fs = new FileInputStream(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            FileDescriptor fd = fs.getFD();
            BitmapFactory.decodeFileDescriptor(fd,null,options);
            int width = options.outWidth;
            int height = options.outHeight;
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(width, height);
            LogUtils.e("ImageLoader", "width is : " + width + ",height is : " + height + ",inSampleSize is : " + options.inSampleSize);
            return BitmapFactory.decodeFileDescriptor(fd,null,options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBmpFromBytes(byte [] bytes){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(width, height);
        LogUtils.e("ImageLoader", "width is : " + width + ",height is : " + height + ",inSampleSize is : " + options.inSampleSize);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public static Bitmap loadBitmap(String path){
        try{
            FileInputStream fs = new FileInputStream(path);
            FileDescriptor fd = fs.getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeFileDescriptor(fd,null,options);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static int calculateSampleSize(int width, int height) {
        int sampleSize;
        if (height > width) {
            sampleSize = height / 300;
        } else {
            sampleSize = width / 200;
        }
        return sampleSize;
    }

}
