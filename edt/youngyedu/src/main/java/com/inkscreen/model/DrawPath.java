package com.inkscreen.model;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/12/20.
 */
public class DrawPath implements Serializable{


    public transient  Path path;// 路径  

    public transient Paint paint;// 画笔  
    public List<Point> points = new ArrayList<>();

    public int tag = -1;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
    public void init()
    {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘  
            paint.setStrokeCap(Paint.Cap.ROUND);// 形状  


            if (tag == 1 || tag == -1){
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2.0f);// 笔
            }else {
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(20.0f);// 橡皮擦
            }

        paint.setAntiAlias(true);
        path = new Path();
            if(points!=null&&points.size()>0) {
                path.moveTo(points.get(0).x, points.get(0).y);
            }
            for(int index=0;index<points.size()-1;index++)
            {
                path.quadTo(points.get(index).x,points.get(index).y, ( points.get(index).x+ points.get(index+1).x) / 2, (points.get(index).y + points.get(index+1).y) / 2);
            }
    }

//    public void init(int currentStyle)
//    {
//        paint = new Paint();
//
//            paint.setAntiAlias(true);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘  
//            paint.setStrokeCap(Paint.Cap.ROUND);// 形状  
//
//            if (currentStyle ==1 ){
//                paint.setStrokeWidth(5);// 画笔宽度
//                paint.setColor(Color.BLACK);
//            }else {
//                paint.setStrokeWidth(50);// 画笔宽度
//                paint.setColor(Color.WHITE);
//            }
//
//
//
//        path = new Path();
//        if(points!=null&&points.size()>0) {
//            path.moveTo(points.get(0).x, points.get(0).y);
//        }
//        for(int index=0;index<points.size()-1;index++)
//        {
//            path.quadTo(points.get(index).x,points.get(index).y, ( points.get(index).x+ points.get(index+1).x) / 2, (points.get(index).y + points.get(index+1).y) / 2);
//        }
//    }

//    //初始化画笔样式
//    private void setPaintStyle() {
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
//        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
//
//        mPaint.setDither(true);
//        if (currentStyle == 1) {
//            mPaint.setStrokeWidth(5);
//            mPaint.setColor(Color.BLACK);
//        } else {//橡皮擦
////               mPaint.setAlpha(0);
////               mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//            mPaint.setColor(Color.WHITE);
//            mPaint.setStrokeWidth(50);
//        }
//    }

}
