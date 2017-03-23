package com.inkscreen.will.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.inkscreen.model.DrawPath;
import com.inkscreen.model.Point;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xcz on 2016/11/18.
 */
public class TuyaView extends View {
    public Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;// 画布的画笔  
    private Paint mPaint;// 真实的画笔  
    private float mX, mY;// 临时点坐标  
    private static final float TOUCH_TOLERANCE = 4;

    // 保存Path路径的集合,用List集合来模拟栈
    public static List<DrawPath> savePath;

    // 保存已删除Path路径的集合
    private static List<DrawPath> deletePath;

    // 记录Path路径的对象
    private DrawPath dp;
    private int screenWidth, screenHeight;
    private int mBackColor = Color.WHITE;
    private int mBlackOrWhite = Color.BLACK;
    private int currentStyle = 1;
    private Context mContext;

//    public class DrawPath implements Serializable{
//        @JSONField(serialize = false)
//        public Path path;// 路径  
//        @JSONField(serialize = false)
//        public Paint paint;// 画笔  
//        public List<Point> points = new ArrayList<>();
//
//        public List<Point> getPoints() {
//            return points;
//        }
//
//        public void setPoints(List<Point> points) {
//            this.points = points;
//        }
//        public void init()
//        {
////            paint = new Paint();
////            paint.setAntiAlias(true);
////            paint.setStyle(Paint.Style.STROKE);
////            paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘  
////            paint.setStrokeCap(Paint.Cap.ROUND);// 形状  
////            paint.setStrokeWidth(5);// 画笔宽度
////            path=new Path();
////            if(points!=null&&points.size()>0) {
////                path.moveTo(points.get(0).x, points.get(0).y);
////            }
////            for(int index=0;index<points.size()-1;index++)
////            {
////                path.quadTo(points.get(index).x,points.get(index).y, ( points.get(index).x+ points.get(index+1).x) / 2, (points.get(index).y + points.get(index+1).y) / 2);
////            }
//        }
//    }

    public TuyaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public TuyaView(Context context) {
        super(context);
        init(context);
//        screenWidth = w;
//        screenHeight = h;
//        Log.e("TuyaView", screenWidth + "<--tuya bitmap w and h -->" + screenHeight);
//        mContext = context;
//
//        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
//        // 保存一次一次绘制出来的图形  
//        mCanvas = new Canvas(mBitmap);
//        mCanvas.drawColor(mBackColor);
//        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
////        mPaint = new Paint();
////        mPaint.setAntiAlias(true);
////        mPaint.setStyle(Paint.Style.STROKE);
////        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘  
////        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状  
////        mPaint.setStrokeWidth(5);// 画笔宽度  
////        mPaint.setColor(Color.WHITE);
//
//        setPaintStyle();
//        savePath = new ArrayList<DrawPath>();
//        deletePath = new ArrayList<>();
    }

//    public TuyaView(Context context) {
//        super(context);
//        init(context);
//    }


    public TuyaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        setPaintStyle();
        savePath = new ArrayList<DrawPath>();
        deletePath = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (screenWidth == 0 || screenHeight == 0) {
            if (w > 0 && h > 0) {
                initBitmap(w, h);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getHeight() > 0 && getWidth() > 0) {
            initBitmap(getWidth(), getHeight());
        }
    }

    private void initBitmap(int w, int h) {

        screenWidth = w;
        screenHeight = h;
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        // 保存一次一次绘制出来的图形  
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBackColor);
        if(savePath!=null&&savePath.size()>0)
        {
            redrawOnBitmap();
        }
    }

    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状

        mPaint.setDither(true);
        if (currentStyle == 1) {
            mPaint.setStrokeWidth(5);
            mPaint.setColor(Color.BLACK);
        } else {//橡皮擦
//               mPaint.setAlpha(0);
//               mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(50);
        }
    }


    //以下为样式修改内容
    //设置画笔样式
    public void selectPaintStyle(int which) {
        if (which == 0) {
            currentStyle = 1;
            setPaintStyle();
        }
        //当选择的是橡皮擦时，设置颜色为白色
        if (which == 1) {
            currentStyle = 2;
            setPaintStyle();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.drawColor(mBackColor);
        // 将前面已经画过得显示出来  
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
        if (mPath != null) {
            // 实时的显示  
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        Point point = new Point();
        point.x = x;
        point.y = y;

        dp.getPoints().add(point);

    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用math.lineTo也是可以的)  
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            Point point = new Point();
            point.x = x;
            point.y = y;
            // dp.setTag(currentStyle);
            dp.getPoints().add(point);

            if (deletePath != null && deletePath.size() > 0) {
                deletePath.clear();

            }


        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        //将一条完整的路径保存下来(相当于入栈操作)
        savePath.add(dp);
        mPath = null;// 重新置空  
    }

    /**
     *  
     *      * 撤销的核心思想就是将画布清空， 
     *      * 将保存下来的Path路径最后一个移除掉
     *      * 重新将路径画在画布上面
     *      
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }


    public void reldo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }

    /**
     *  
     *      * 重做 
     *      
     */
    public void redo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.clear();
            redrawOnBitmap();
        }

        if (deletePath != null && deletePath.size() > 0) {
            deletePath.clear();

        }
    }


    public void recover() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            //将取出的路径重绘在画布上
            mCanvas.drawPath(dp.path, dp.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            invalidate();
        }
    }

    public void initPaht(List<DrawPath> paths) {

//        try {
        if (savePath == null) {
            savePath = new ArrayList();
        }
        savePath.clear();
        savePath.addAll(paths);
        redrawOnBitmap();
//        }catch (Exception e){
//            Log.d("xcz",">>>>>>>>>>>>>>"+e);
//
//        }
    }
//    public void read(String json){
////        SharedPreferences preferences = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE);
////        String json = preferences.getString("alterSampleJson", null);
//        if (json != null)
//        {
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<SaveInfo>>(){}.getType();
//            List<SaveInfo> alterSamples = new ArrayList<>();
//            alterSamples = gson.fromJson(json, type);
//            mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
//                    Bitmap.Config.ARGB_8888);
//            mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布  
////        mCanvas = new Canvas(mBitmap);
////        mCanvas.drawColor(mBackColor);
//            Iterator<SaveInfo> iter = alterSamples.iterator();
//            while (iter.hasNext()) {
//                SaveInfo drawPath = iter.next();
//                mCanvas.drawPath(drawPath.path, drawPath.paint);
//            }
//            invalidate();// 刷新
//
//        }
//
//
//    }

    private void redrawOnBitmap() {
        if (screenWidth > 0 && screenHeight > 0) {
            mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                    Bitmap.Config.ARGB_4444);
//        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布  
            if (mCanvas != null) {
                mCanvas.setBitmap(mBitmap);
                Log.i("xcz", "1111111111111");
            } else {
                mCanvas = new Canvas(mBitmap);
                Log.i("xcz", "222222");
//            mCanvas.drawColor(mBackColor);
            }
            mCanvas.drawColor(mBackColor);
            Iterator<DrawPath> iter = savePath.iterator();
            while (iter.hasNext()) {
                DrawPath drawPath = iter.next();

                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();// 刷新  

        }

//        mCanvas = new Canvas(mBitmap);
//        mCanvas.drawColor(mBackColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 每次down下去重新new一个Path  
                mPath = new Path();
                //每一次记录的路径对象是不一样的  
                dp = new DrawPath();
                dp.path = mPath;
                dp.setTag(currentStyle);
//                if(currentStyle == 1){
//                    mPaint =
//
//                }else
//                {
//                    mPaint =
//                }
                dp.paint = mPaint;
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void saveToSDCard() {

//        SharedPreferences.Editor editor = mContext.getSharedPreferences("AlterSamplesList", mContext.MODE_PRIVATE).edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(savePath);
//        editor.putString("alterSampleJson", json);
//        editor.commit();


//        String fileUrl = Environment.getExternalStorageDirectory()
//                .toString() + "/android/data/test.png";

        String fileUrl = "/sdcard/bbb.png";
//        try {
//            FileOutputStream fos = new FileOutputStream(new File(fileUrl));
//            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Bitmap bitmap = mBitmap;
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  压缩图片
        //  if (clearBlank) {
//        bitmap = clearBlank(bitmap, 10);
        //  }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            File file = new File(fileUrl);
            if (file.exists()) {
                file.delete();
            }
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            outputStream.write(buffer);
//            outputStream.close();
        }
    }

    public void setMyBack(@ColorInt int backColor) {
        mBlackOrWhite = backColor;
    }


    public void setMyWhite(@ColorInt int backColor) {
        mBlackOrWhite = backColor;
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }
}
