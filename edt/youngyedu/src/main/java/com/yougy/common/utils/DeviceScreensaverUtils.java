package com.yougy.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.ReflectUtil;
import com.yougy.init.bean.Student;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/8/13.
 */

public class DeviceScreensaverUtils {


    /***
     * 设置息平时候的显示内容
     */
    public static void setScreensaver() {
        Student student = SpUtils.getStudent();
        String path = "data/local/assets/info.png";
        File bitmapFile = new File(path);

        bitmapFile.setReadable(true, false);
        bitmapFile.setWritable(true, false);

        if (bitmapFile.exists()) {
            System.out.println("文件存在");
//            Bitmap bitmap = BitmapFactory.decodeResource(UIUtils.getResources(), R.drawable.yuanye);
            String strInfos = "班级 :" + student.getClassName()+ "\n姓名 :" + student.getUserRealName()+"同学";
            Bitmap bitmap = getNewBitMap(strInfos);
            saveBitmapFile(rotateBitmap(bitmap), bitmapFile,false);
        }
    }

    private static Bitmap getNewBitMap(String text) {
        Bitmap newBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(36.0F);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        StaticLayout sl = new StaticLayout(text, textPaint, newBitmap.getWidth() - 8, Layout.Alignment.ALIGN_NORMAL, 1.0f, 10.0f, false);
//        canvas.translate(6, 40);
        sl.draw(canvas);
        return newBitmap;
    }

    private static void saveBitmapFile(Bitmap bitmap, File file, boolean isDevice) {
        int startX = 0;
        int startY = 0;
        int orientation = 0;

        if (!isDevice) {
            if (SystemUtils.getDeviceModel().equalsIgnoreCase("PL107")) {
                startY = 560;
            } else if (SystemUtils.getDeviceModel().equalsIgnoreCase("EDU")) {
                startX = 800;
            }
        }


        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            Device.currentDevice().setInfoShowConfig(0, startX, startY);
            Class<View> cls = View.class;
            Method sMethodSetInfoShowConfig = ReflectUtil.getMethodSafely(cls, "setInfoShowConfig", int.class, int.class, int.class);
            ReflectUtil.invokeMethodSafely(sMethodSetInfoShowConfig, null, orientation, startX, startY);

        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            saveBitmapFile(bitmap, file,isDevice);
        }
    }

    private static Bitmap rotateBitmap(Bitmap origin) {
        float alpha = 0;
        if (SystemUtils.getDeviceModel().equalsIgnoreCase("PL107")) {
            alpha = 90;
        } else if (SystemUtils.getDeviceModel().equalsIgnoreCase("EDU")) {
            alpha = -90;
        }

        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static void setDeviceBg() {
//        String path = "data/local/assets/standby1.png";
////        String path =   "/data/local/assets/images/standby1.png" ;
//        File bitmapFile = new File(path);
//        bitmapFile.setReadable(true, false);
//        bitmapFile.setWritable(true, false);
//        Bitmap bmp= BitmapFactory.decodeResource(UIUtils.getResources(), R.drawable.img_device_bg);
//        saveBitmapFile(bmp, bitmapFile,true);
    }
}
