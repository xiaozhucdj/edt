package com.inkscreen.will.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.inkscreen.model.DrawPath;
import com.inkscreen.model.Point;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.utils.UIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 悠数学功能块手写板功能实现
 * Created by elanking on 2017/1/22.
 */

public class PaintView extends View {
    private static final String TAG = "PaintView";
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
    private boolean contentChanged;
    private Matrix matrix;

    private boolean invalidateAble;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintView(Context context) {
        super(context);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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
        matrix = new Matrix();
        matrix.postRotate(270);
        matrix.postTranslate(0, UIUtils.getScreenWidth());
        EpdController.setStrokeColor(0xff000000);
        EpdController.setStrokeStyle(1);
        EpdController.setStrokeWidth(2.0f);
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        // 保存一次一次绘制出来的图形  
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBackColor);
        if (savePath != null && savePath.size() > 0) {
            redrawOnBitmap();
        }

    }

    private void init(Context context) {
        mContext = context;
        setPaintStyle();
        savePath = new ArrayList<DrawPath>();
        deletePath = new ArrayList<>();
    }

    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
//        mPaint.setDither(true);
        if (currentStyle == 1) {
//            mPaint.setXfermode(null);
            mPaint.setStrokeWidth(2.0f);
            mPaint.setColor(Color.BLACK);
            setScreenPaint();

        } else {//橡皮擦

//            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(20.0f);
            setScreenEraser();
        }
        mBitmapPaint = new Paint(mPaint);
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
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(mBackColor);
        // 将前面已经画过得显示出来
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getDeviceId() != 1) {
            return true;
        }
        if (!isEnabled() || event.getPointerCount() > 1) {
            return false;
        }
        contentChanged = true;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                EpdController.enterScribbleMode(this);
                // 每次down下去重新new一个Path  
                mPath = new Path();
                //每一次记录的路径对象是不一样的  
                dp = new DrawPath();
                dp.path = mPath;
                dp.setTag(currentStyle);
                dp.paint = new Paint(mPaint);
                touchDown(x, y);
//                invalidate();
                float dst[] = mapPoint(x, y);
                EpdController.moveTo(dst[0], dst[1], mPaint.getStrokeWidth());
                break;
            case MotionEvent.ACTION_MOVE:
                if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
                    touchMove(x, y);
//                invalidate();
                    int n = event.getHistorySize();
                    for (int i = 0; i < n; i++) {
                        dst = mapPoint(event.getHistoricalX(i), event.getHistoricalY(i));
                        EpdController.quadTo(dst[0], dst[1], UpdateMode.DU);
                    }
                    dst = mapPoint(event.getX(), event.getY());
                    EpdController.quadTo(dst[0], dst[1], UpdateMode.DU);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchUp(x, y);
//                invalidate();

                break;
        }
        return true;
    }

    private void touchDown(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        Point point = new Point();
        point.x = x;
        point.y = y;

        dp.getPoints().add(point);

    }

    private void touchMove(float x, float y) {
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
            mCanvas.drawPath(mPath, dp.paint);
            if (deletePath != null && deletePath.size() > 0) {
                deletePath.clear();

            }

        }
    }

    private void touchUp(float x, float y) {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        //将一条完整的路径保存下来(相当于入栈操作)
        savePath.add(dp);
        mPath = null;// 重新置空  
    }

    public void initPaht(List<DrawPath> paths) {

        EpdController.leaveScribbleMode(this);
        if (savePath == null) {
            savePath = new ArrayList();
        }
        savePath.clear();
        savePath.addAll(paths);
        redrawOnBitmap();

    }

    public void redo() {
        if (savePath != null) {
            savePath.clear();
            redrawOnBitmap();
        }

        if (deletePath != null && deletePath.size() > 0) {
            deletePath.clear();

        }
        selectPaintStyle(0);
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
            EpdController.leaveScribbleMode(this);
            invalidate();
        }
    }

    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }

    private void redrawOnBitmap() {
        if (screenWidth > 0 && screenHeight > 0) {
            mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                    Bitmap.Config.ARGB_4444);
            if (mCanvas != null) {
                mCanvas.setBitmap(mBitmap);
            } else {
                mCanvas = new Canvas(mBitmap);
            }
            mCanvas.drawColor(mBackColor);
            Iterator<DrawPath> iter = savePath.iterator();
            while (iter.hasNext()) {
                DrawPath drawPath = iter.next();

                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            EpdController.leaveScribbleMode(this);
            invalidate();
        }

    }


    public void invalidataControler() {
        EpdController.leaveScribbleMode(this);
        invalidate();
    }

    private void setScreenPaint() {
        EpdController.setStrokeColor(0xff000000);
        EpdController.setStrokeWidth(2.0f);
    }

    private void setScreenEraser() {
        EpdController.setStrokeColor(0xffffffff);
        EpdController.setStrokeWidth(20.0f);
    }

    float[] mapPoint(float x, float y) {
        int viewLocation[] = {0, 0};
        getLocationOnScreen(viewLocation);
        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        matrix.mapPoints(dst, screenPoints);
        return dst;
    }
}
