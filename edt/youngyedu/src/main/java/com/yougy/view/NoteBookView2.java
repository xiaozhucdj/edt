package com.yougy.view;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.global.FileContonst;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Label;
import com.yougy.home.bean.Line;
import com.yougy.home.bean.Note;
import com.yougy.home.bean.Point;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiangliang on 2016/6/27.
 */
public class NoteBookView2 extends View {
    private static final String TAG = "NoteBookView";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;// 画布的画笔
    private Paint mPaint;// 真实的画笔
    private float mX, mY;// 临时点坐标
    private static final float TOUCH_TOLERANCE = 4;
    // 保存Path路径的集合
    private List<DrawPath> savePath;
    // 记录Path路径的对象
    private DrawPath dp;
    private int screenWidth;
    private int screenHeight;// 屏幕長寬
    //该标志用于判断是否需要移除索引后面的操作
    private boolean flag;
    private Matrix matrix;

    private List<Line> lines = new ArrayList<>();
    /**
     * 历史保存在sd卡上的 位图 ，主要解决 前进后退 情况画板 不会还原问题 2016 09 23
     */
    private int mSystenPenType;

    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

    public NoteBookView2(Context context) {
        super(context);
        init();
    }

    public NoteBookView2(Context context, int w, int h) {
        super(context);
        screenWidth = w;
        screenHeight = h;
        init();
    }

    public NoteBookView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (screenHeight <= 0 || screenWidth <= 0) {
            screenWidth = UIUtils.getScreenWidth();
            screenHeight = UIUtils.getScreenHeight();
        }

        if (screenWidth <= 0 || screenHeight <= 0) {
            screenWidth = 960;
            screenHeight = 1280;
        }
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        // 保存一次一次绘制出来的图形
        mCanvas = new Canvas(mBitmap);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
        mPaint.setStrokeWidth(2.0f);// 画笔宽度
        mPaint.setColor(Color.BLACK);
        savePath = new ArrayList<>();

        mBitmapPaint = new Paint(mPaint);

        matrix = new Matrix();

        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            LogUtils.e(getClass().getName(), FileContonst.DEVICE_TYPE_PL107);
            matrix.postRotate(90);
            matrix.postTranslate(UIUtils.getScreenHeight(), 0);
        } else if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_N96) || SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_EDU)) {
            LogUtils.e(getClass().getName(), "N96");
            matrix.postRotate(270);
            matrix.postTranslate(0, UIUtils.getScreenWidth());
        }
        //适用于设备N96

        //适用于设备PL107

        EpdController.setStrokeColor(0xff000000);
//        EpdController.setStrokeStyle(1);
//        EpdController.setStrokeWidth(2.0f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPen();
    }

    public void drawBitmap(Bitmap bitmap) {
//        recycle();
        mCanvas.drawBitmap(bitmap, 0, 0, mPaint);
        invalidate();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    private int mCanvasColor = 0x00000000;

    @Override
    public void onDraw(Canvas canvas) {
        LogUtils.e(TAG, "onDraw.....................");
        canvas.drawColor(mCanvasColor);
        // 将前面已经画过得显示出来
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

    private void touch_start(float x, float y) {
        if (mPath != null) {
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
    }

    private void touch_move(float x, float y) {
        if (mPath != null) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(mY - y);
//            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            line.getPoints().add(new Point(x, y));
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
//            }
            mCanvas.drawPath(mPath, dp.paint);
        }
    }

    private void touch_up(float x, float y) {
        if (mPath != null) {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            //保存路径
            savePath.add(dp);
            mPath = null;// 重新置空
        }
    }


    private int index = -1;

    //清理整个画板，不保存之前数据
    public void clearAll() {
        index = -1;
        savePath.clear();
        recycle();
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        mCanvas.setBitmap(mBitmap);
        invalidate();
    }

    /**
     * 保存图片
     */
    public void save(Label label) {
        if (label == null) {
            label = DataSupport.findLast(Label.class);
        }
        label.setBytes(bitmap2Bytes());
        if (label.isSaved()) {
            label.update(label.getId());
        } else {
            label.save();
        }
    }

    public void save(Note note) {
        note.getLines().addAll(lines);
        note.obj2Bytes();
        if (!note.isSaved()) {
            note.save();
        } else {
            ContentValues values = new ContentValues();
            values.put("bytes", note.getBytes());
            DataSupport.update(Note.class, values, note.getId());
        }
        lines.clear();
    }

    public byte[] bitmap2Bytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }


    private Line line;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIntercept) {
            return false;
        }

        if (!isEnabled() || event.getPointerCount() > 1) {
            return false;
        }

        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            mSystenPenType = MotionEvent.TOOL_TYPE_UNKNOWN;
        } else {
            mSystenPenType = MotionEvent.TOOL_TYPE_STYLUS;
        }
        if (event.getToolType(0) == mSystenPenType) {
            setPen();
        }
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
            useEraser();
        }

        float x = event.getX();
        float y = event.getY();

        if (x < 0 && x > getWidth() && y < 0 && y > getHeight()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果flag为true，则说明进行了undo、redo操作，这时再进行按下操作时应将索引后面的path清除掉
                EpdController.enterScribbleMode(this);
                if (flag) {
                    int size = savePath.size();
                    for (int i = size - 1; i > index; i--) {
                        savePath.remove(i);
                    }
                }
                flag = false;
                index = -1;
                // 每次down下去重新new一个Path
                mPath = new Path();
                //每一次记录的路径对象是不一样的
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = new Paint(mPaint);
                line = new Line();
                line.setWidth(dp.paint.getStrokeWidth());
                line.setColor(dp.paint.getColor());
                line.setType(dp.paint.getXfermode() == null ? 0 : 1);
                line.setStart(new Point(x, y));
                touch_start(x, y);
                float dst[] = mapPoint(x, y);
                EpdController.moveTo(dst[0], dst[1], mPaint.getStrokeWidth());
                break;
            case MotionEvent.ACTION_MOVE:
                if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
                    touch_move(x, y);
                    LogUtils.e(TAG, "action move......(x,y) : " + x + "," + y);
                    long start = System.currentTimeMillis();
                    int n = event.getHistorySize();
                    for (int i = 0; i < n; i++) {
                        dst = mapPoint(event.getHistoricalX(i), event.getHistoricalY(i));
                        EpdController.quadTo(dst[0], dst[1], UpdateMode.DU);
                    }
                    long end = System.currentTimeMillis();
                    LogUtils.e(TAG, " take time : " + (end - start));
                    dst = mapPoint(event.getX(), event.getY());
                    EpdController.quadTo(dst[0], dst[1], UpdateMode.DU);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (line != null) {
                    line.setEnd(new Point(x, y));
                    lines.add(line);
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
                        EpdController.leaveScribbleMode(this);
                        invalidate();
                    }
                    touch_up(x, y);
                }
                break;
        }
        return true;
    }


    float[] mapPoint(float x, float y) {
        int viewLocation[] = {0, 0};
        getLocationOnScreen(viewLocation);
        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        matrix.mapPoints(dst, screenPoints);
        return dst;
    }


    public void setPen() {
        mPaint.setXfermode(null);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setColor(Color.BLACK);
        setScreenPaint();
    }

    public void useEraser() {
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(20);
        setScreenEraser();
    }

    private void setScreenPaint() {
        EpdController.setStrokeColor(0xff000000);
        EpdController.setStrokeWidth(2.0f);
    }

    private void setScreenEraser() {
        EpdController.setStrokeColor(0xffffffff);
        EpdController.setStrokeWidth(20.0f);
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        Runtime.getRuntime().gc();
    }

    public void leaveScribbleMode(boolean isPen) {
        if (isPen) {
            setPen();
            EpdController.leaveScribbleMode(this);
            invalidate();
        }
    }

    public void leaveScribbleMode() {
        EpdController.leaveScribbleMode(this);
        invalidate();
    }

    private boolean mIntercept = false;

    public void setIntercept(boolean intercept) {
        mIntercept = intercept;
    }
}