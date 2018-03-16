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
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Label;
import com.yougy.home.bean.Line;
import com.yougy.home.bean.Note;
import com.yougy.home.bean.Point;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiangliang on 2016/6/27.
 */
public class NoteBookView extends View {
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
    private int screenWidth = 1000, screenHeight = 1000;// 屏幕長寬
    private OnUndoOptListener undoOptListener;
    private OnDoOptListener redoOptListener;
    //该标志用于判断是否需要移除索引后面的操作
    private boolean flag;
    private Context mContext;
    private boolean contentChanged;
    private Matrix matrix;

    private List<Line> lines = new ArrayList<>();
    private List<Line> undoLines = new ArrayList<>();
    /**
     * 历史保存在sd卡上的 位图 ，主要解决 前进后退 情况画板 不会还原问题 2016 09 23
     */
    private Bitmap mSrcBitmp;

    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    public boolean isContentChanged() {
        return this.contentChanged;
    }

    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

    public NoteBookView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NoteBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public interface OnUndoOptListener {
        void disenableUndoView();

        void enableUndoView();

        boolean isEnableUndo();
    }

    public interface OnDoOptListener {
        void disenableReDoView();

        void enableReDoView();

        boolean isEnableRedo();
    }

    public void setUndoOptListener(OnUndoOptListener listener) {
        undoOptListener = listener;
    }

    public void setReDoOptListener(OnDoOptListener listener) {
        redoOptListener = listener;
    }

    private void disenableUndoView() {
        if (null != undoOptListener) {
            undoOptListener.disenableUndoView();
        }
    }

    private void enableUndoView() {
        if (null != undoOptListener) {
            undoOptListener.enableUndoView();
        }
    }

    private void disenableRedoView() {
        if (null != redoOptListener) {
            redoOptListener.disenableReDoView();
        }
    }

    private void enableReDoView() {
        if (null != redoOptListener) {
            redoOptListener.enableReDoView();
        }
    }

    private void init() {
        screenWidth = UIUtils.getScreenWidth();
        screenHeight = UIUtils.getScreenHeight();
        //这里会测试 LOG screenWidth screenHeight ==0 。理论上不可能为0 除非程序发生了崩溃 继续运行导致的
        if (screenWidth < 0 || screenHeight < 0) {
            screenWidth = 960;
            screenHeight = 1280;
        }
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
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

        if (SystemUtils.getDeviceModel().equalsIgnoreCase("PL107")) {
            LogUtils.e(getClass().getName(), "PL107");
            matrix.postRotate(90);
            matrix.postTranslate(UIUtils.getScreenHeight(), 0);
        } else if (SystemUtils.getDeviceModel().equalsIgnoreCase("N96") || SystemUtils.getDeviceModel().equalsIgnoreCase("EDU")) {
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
           userOutPen();
    }

    public void drawBitmap(Bitmap bitmap) {
        mSrcBitmp = bitmap;
        mCanvas.drawBitmap(bitmap, 0, 0, mPaint);
        invalidate();
    }

    private List<Line> historyLines = new ArrayList<>();

    public void drawLines(List<Line> lines, boolean flag) {
        float preX = 0;
        float preY = 0;
        historyLines = lines;
        resetCanvas();
        Paint paint = new Paint(mPaint);
        for (Line line : lines) {
            Path path = new Path();
            Point start = line.getStart();
            Point end = line.getEnd();
            path.moveTo(start.getX(), start.getY());
            preX = start.getX();
            preY = start.getY();
            for (Point point : line.getPoints()) {
                path.quadTo(preX, preY, (preX + point.getX()) / 2, (preY + point.getY()) / 2);
                preX = point.getX();
                preY = point.getY();
            }
            path.lineTo(end.getX(), end.getY());
            paint.setStrokeWidth(line.getWidth());
            paint.setAntiAlias(true);
            paint.setColor(line.getColor());
            paint.setXfermode(line.getType() == 0 ? null : new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPath(path, paint);
        }
        if (flag) {
            invalidate();
        }
    }


    private int mCanvasColor = 0x00000000;

    public void setCanvasColor(int mCanvasColor) {
        this.mCanvasColor = mCanvasColor;
    }


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
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                line.getPoints().add(new Point(x, y));
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
            mCanvas.drawPath(mPath, dp.paint);
        }
    }

    private void touch_up(float x, float y) {
        if (mPath != null) {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            enableUndoView();
            disenableRedoView();
            //保存路径
            savePath.add(dp);
            mPath = null;// 重新置空
        }
    }


    private int index = -1;

    /**
     * 重新设置画布，相当于清空画布
     */

    private void resetCanvas() {
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        if (mSrcBitmp != null) {
            mCanvas.drawBitmap(mSrcBitmp, 0, 0, mPaint);
        }
    }

    /**
     * 将图片以文件形式保存到SD卡中
     */
    private void saveBitmap2Local(String fileUrl) throws IOException {
        File file = new File(fileUrl);
        if (!file.exists()) {
            File parentFile = new File(file.getParent());
            parentFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * undo的核心思想就是将画布清空，将索引在保存下来的Path路径中进行递减，重新将索引之前的所有路径画在画布上面。
     */
    public void undo() {
        undoLines.add(lines.remove(lines.size() - 1));
        contentChanged = true;
        flag = true;
        resetCanvas();
        drawLines(historyLines, false);
        if (savePath != null && savePath.size() > 0) {
            //如果没有做过undo操作，则将索引置为集合末位
            if (index == -1) {
                index = savePath.size() - 1;
            }
            index--;
            if (index == -1) {
                disenableUndoView();
            }
            enableReDoView();
            for (int i = 0; i <= index; i++) {
                DrawPath drawPath = savePath.get(i);
                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();
        }
    }

    /**
     * redo的核心思想就是索引递增，得到该位置的路径和画笔，画到画布上即可。
     */
    public void redo() {
        lines.add(undoLines.remove(undoLines.size() - 1));
        contentChanged = true;
        flag = true;
        index++;
        if (index == savePath.size() - 1) {
            disenableRedoView();
        }
        enableUndoView();
        DrawPath drawPath = savePath.get(index);
        mCanvas.drawPath(drawPath.path, drawPath.paint);
        invalidate();
    }

    public void clear() {
        index = -1;
        savePath.clear();
        disenableRedoView();
        disenableUndoView();
        resetCanvas();
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

    private byte[] bitmap2Bytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    private float preX;
    private float preY;
    private float currentX;
    private float currentY;
    private Line line;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getDeviceId() != 1) {
            return true;
        }
        if (!isEnabled() || event.getPointerCount() > 1) {
            return false;
        }

        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
           useInEraser();
        } else {
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                if (!flagOfErase) {
                  userOutPen();
                }else {
                    useOutEraser();
                }
            }
        }
        contentChanged = true;
        float x = event.getX();
        float y = event.getY();
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
                preX = x;
                preY = y;
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
                    currentX = x;
                    currentY = y;
                    preX = currentX;
                    preY = currentY;
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
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER | flagOfErase | undoNeedUpdate() | redoNeedUpdate()) {
                        EpdController.leaveScribbleMode(this);
                        invalidate();
                    }
                    touch_up(x, y);
                }
                break;
        }
        return true;
    }

    private boolean redoNeedUpdate() {
        return redoOptListener != null && redoOptListener.isEnableRedo();
    }

    private boolean undoNeedUpdate() {
        boolean result = undoOptListener != null && !undoOptListener.isEnableUndo();
        LogUtils.e(TAG, "undo need update result is : " + result);
        return result;
    }

    float[] mapPoint(float x, float y) {
        int viewLocation[] = {0, 0};
        getLocationOnScreen(viewLocation);
        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        matrix.mapPoints(dst, screenPoints);
        return dst;
    }

/*    public void setPaintSize(float width) {
        mPaint.setStrokeWidth(width);
    }

    public void setPaintAlpha(int value) {
        mPaint.setAlpha(value);
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
        setColorPenAlpha();
    }*/

/*    public void setPen() {
        mPaint.setXfermode(null);
        isColorPen = false;
        mPaint.setStrokeWidth(2.0f);
        mPaint.setColor(Color.BLACK);
        setScreenPaint();
    }*/

   /* public void setOilBlackPen() {
        mPaint.setXfermode(null);
        isColorPen = false;
        mPaint.setStrokeWidth(5.0f);
        mPaint.setColor(Color.BLACK);
        setScreenPaint();
    }*/

/*    private void setColorPenAlpha() {
        mPaint.setXfermode(null);
        if (isColorPen) {
            mPaint.setAlpha(100);
        }
    }

    private boolean isColorPen;*/

  /*  public void setMakerPen() {
        isColorPen = true;
        flagOfErase = false;
        mPaint.setXfermode(null);
        mPaint.setStrokeWidth(15.0f);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(100);
        setScreenPaint();
    }*/

//    private boolean flagOfErase;

 /*   public void useEraser() {
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(20);
        setScreenEraser();
    }*/

 /*   public void setEraserFlag(boolean flag) {
        flagOfErase = flag;
    }*/

/*    private void setScreenPaint() {
        EpdController.setStrokeColor(0xff000000);
        EpdController.setStrokeWidth(2.0f);
    }*/

/*
    private void setScreenEraser() {
        EpdController.setStrokeColor(0xffffffff);
        EpdController.setStrokeWidth(20.0f);
    }

*/

    private float mOutSetPenSize =2.0f;

    public void outSetPenSize(int outSetPenSize) {
        mOutSetPenSize = (float) outSetPenSize;
        flagOfErase = false;
    }

    private float mOutSetEraserSize =2.0f;

    public void outSetEraserSize(int outSetEraserSize) {
        mOutSetEraserSize = (float) outSetEraserSize;
        flagOfErase = true;
    }

    private boolean flagOfErase = false;

    private void userOutPen() {
        LogUtils.i("yuanye  userOutPen ...."+mOutSetPenSize);
        mPaint.setXfermode(null);
        mPaint.setStrokeWidth(mOutSetPenSize-0.2f);
        mPaint.setColor(Color.BLACK);
        EpdController.setStrokeColor(0xff000000);
        EpdController.setStrokeWidth(mOutSetPenSize);
    }

    private void useOutEraser() {
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mOutSetEraserSize);
        EpdController.setStrokeColor(0xffffffff);
        EpdController.setStrokeWidth(mOutSetEraserSize);
    }

    private void useInEraser() {
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(20.0f);
        EpdController.setStrokeColor(0xffffffff);
        EpdController.setStrokeWidth(20.0f);
    }
}