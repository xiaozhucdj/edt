package com.artifex.mupdfdemo.pdf.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.pdf.PdfInterface.TextProcessor;
import com.artifex.mupdfdemo.pdf.bean.Annotation;
import com.artifex.mupdfdemo.pdf.bean.BitmapHolder;
import com.artifex.mupdfdemo.pdf.bean.LinkInfo;
import com.artifex.mupdfdemo.pdf.bean.PatchInfo;
import com.artifex.mupdfdemo.pdf.bean.TextSelector;
import com.artifex.mupdfdemo.pdf.bean.TextWord;
import com.artifex.mupdfdemo.pdf.task.AsyncTask;
import com.yougy.common.utils.LogUtils;
import com.yougy.home.imple.PageListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class PageView extends ViewGroup {

    //////////////////////////////File final color 常量///////////////////////////////////////////////////////
    private static final int HIGHLIGHT_COLOR = 0x802572AC;
    private static final int LINK_COLOR = 0x80AC7225;
    private static final int BOX_COLOR = 0xFF4444FF;
    private static final int INK_COLOR = 0xFFFF0000;
    private static final float INK_THICKNESS = 10.0f;
    private static final int BACKGROUND_COLOR = 0xFFFFFFFF;
    /**
     * 一次会话的事件
     */
    protected final Context mContext;
    protected int mPageNumber;
    private Point mParentSize;
    protected Point mSize;   // Size of page at minimum zoom
    protected float mSourceScale;

    /**
     * 图像呈现至少放大
     */
//    private ImageView mEntire; // Image rendered at minimum zoom
    private BitmapHolder mEntireBmh;
    private AsyncTask<Void, Void, TextWord[][]> mGetText;
    private AsyncTask<Void, Void, LinkInfo[]> mGetLinkInfo;
    private AsyncTask<Void, Void, Bitmap> mDrawEntire;
    private ImageView mShowView;
    /**
     * 视图大小的基础上创建补丁
     */
    private Point mPatchViewSize; // View size on the basis of which the patch was created
    private Rect mPatchArea;
    private ImageView mPatch;
    private BitmapHolder mPatchBmh;
    private AsyncTask<PatchInfo, Void, PatchInfo> mDrawPatch;
    private RectF mSearchBoxes[];
    protected LinkInfo mLinks[];
    private RectF mSelectBox;
    private TextWord mText[][];
    private RectF mItemSelectBox;
    protected ArrayList<ArrayList<PointF>> mDrawing;
    private View mSearchView;
    private boolean mIsBlank;
    private boolean mHighlightLinks;

    private ProgressBar mBusyIndicator;
    private final Handler mHandler = new Handler();
    //下载图片
//    private ThreadManager.ThreadPoolProxy mThreadPool;
    private ConcurrentHashMap<String, Runnable> mMapRuunable = new ConcurrentHashMap<String, Runnable>();
    private PageListener mPageListener;

    //////////////////////////////构造函数///////////////////////////////////////
    public PageView(Context c, Point parentSize) {
        super(c);
        mContext = c;
        mParentSize = parentSize;
        setBackgroundColor(BACKGROUND_COLOR);
        mEntireBmh = new BitmapHolder();
        mPatchBmh = new BitmapHolder();
//        mThreadPool = ThreadManager.getSinglePool("image");
    }

    //////////////////////////////View函数///////////////////////////////////////
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x, y;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                x = mSize.x;
                break;
            default:
                x = MeasureSpec.getSize(widthMeasureSpec);
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                y = mSize.y;
                break;
            default:
                y = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(x, y);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {


        int w = right - left;
        int h = bottom - top;

//        if (mEntire != null) {
//            mEntire.layout(0, 0, w, h);
//        }

        if (mShowView != null) {
            mShowView.layout(0, 0, w, h);
        }
        if (mSearchView != null) {
            mSearchView.layout(0, 0, w, h);
        }

        if (mPatchViewSize != null) {
            if (mPatchViewSize.x != w || mPatchViewSize.y != h) {
                // Zoomed since patch was created
                mPatchViewSize = null;
                mPatchArea = null;
                if (mPatch != null) {
                    mPatch.setImageBitmap(null);
                    mPatchBmh.setBm(null);
                }
            } else {
                mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
            }
        }

        if (mBusyIndicator != null) {
            int bw = mBusyIndicator.getMeasuredWidth();
            int bh = mBusyIndicator.getMeasuredHeight();

            mBusyIndicator.layout((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
        }

    }


    //////////////////////////////abstract 函数///////////////////////////////////////
    protected abstract Bitmap drawPage(int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract Bitmap updatePage(BitmapHolder h, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract LinkInfo[] getLinkInfo();

    protected abstract TextWord[][] getText();

    protected abstract void addMarkup(PointF[] quadPoints, Annotation.Type type);

    //////////////////////////////private函数///////////////////////////////////////

    /**
     * 重置数据
     */
    private void reinit() {
// Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancel(true);
            mDrawEntire = null;
        }

        if (mDrawPatch != null) {
            mDrawPatch.cancel(true);
            mDrawPatch = null;
        }

        if (mGetLinkInfo != null) {
            mGetLinkInfo.cancel(true);
            mGetLinkInfo = null;
        }

        if (mGetText != null) {
            mGetText.cancel(true);
            mGetText = null;
        }

        mIsBlank = true;
        mPageNumber = 0;

        if (mSize == null)
            mSize = mParentSize;

//        if (mEntire != null) {
//            mEntire.setImageBitmap(null);
//            mEntireBmh.setBm(null);
//        }

        if (mPatch != null) {
            mPatch.setImageBitmap(null);
            mPatchBmh.setBm(null);
        }


        mPatchViewSize = null;
        mPatchArea = null;

        mSearchBoxes = null;
        mLinks = null;
        mSelectBox = null;
        mText = null;
        mItemSelectBox = null;


    }


    /////////////////////////////pulic method ///////////////////////////////////////

    /***
     * 释放资源
     */
    public void releaseResources() {
        reinit();

        if (mBusyIndicator != null) {
            removeView(mBusyIndicator);
            mBusyIndicator = null;
        }
    }

    /**
     * 空白页
     *
     * @param page
     */
    public void blank(int page) {
        reinit();
        mPageNumber = page;

  /*      if (mBusyIndicator == null) {
            mBusyIndicator = new ProgressBar(mContext);
            mBusyIndicator.setIndeterminate(true);
            //设置 加载条的颜色
            mBusyIndicator.setBackgroundResource(R.drawable.busy);
            addView(mBusyIndicator);
        }
*/
        setBackgroundColor(BACKGROUND_COLOR);
    }

    /**
     * 设置加载页
     *
     * @param page
     * @param size
     */
    public void setPage(final int page, PointF size) {


//        System.out.println("setPage =="+page);

// Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancel(true);
            mDrawEntire = null;
        }

        mIsBlank = false;
        // Highlights may be missing because mIsBlank was true on last draw
        if (mSearchView != null)
            mSearchView.invalidate();

        mPageNumber = page;
        if (mShowView == null) {
            mShowView = new ImageView(mContext);
            mShowView.setBackgroundColor(BACKGROUND_COLOR);
            mShowView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(mShowView);
        }

        mSourceScale = Math.min(mParentSize.x / size.x, mParentSize.y / size.y);
        Point newSize = new Point((int) (size.x * mSourceScale), (int) (size.y * mSourceScale));
        mSize = newSize;

//        mEntire.setImageBitmap(null);
//        mEntireBmh.setBm(null);

       /* mGetLinkInfo = new AsyncTask<Void, Void, LinkInfo[]>() {
            @Override
            protected LinkInfo[] doInBackground(Void... voids) {
                return getLinkInfo();
            }

            protected void onPostExecute(LinkInfo[] v) {
                mLinks = v;
                invalidate();
            }
        };
        mGetLinkInfo.execute();*/


        // Render the page in the background
        mDrawEntire = new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... v) {
                return drawPage(mSize.x, mSize.y, 0, 0, mSize.x, mSize.y);
            }

            protected void onPreExecute() {
                setBackgroundColor(BACKGROUND_COLOR);
//                mEntire.setImageBitmap(null);
                mEntireBmh.setBm(null);

//                if (mBusyIndicator == null) {
//                    mBusyIndicator = new ProgressBar(mContext);
//                    mBusyIndicator.setIndeterminate(true);
//                    mBusyIndicator.setBackgroundResource(R.drawable.busy);
//                    addView(mBusyIndicator);
//                    mBusyIndicator.setVisibility(INVISIBLE);
//                    mHandler.postDelayed(new Runnable() {
//                        public void run() {
//                            if (mBusyIndicator != null)
//                                mBusyIndicator.setVisibility(VISIBLE);
//                        }
//                    }, PROGRESS_DIALOG_DELAY);
//                }
            }

            protected void onPostExecute(final Bitmap bm) {
//                removeView(mBusyIndicator);
//                mBusyIndicator = null;
                //TODO: 获取PDF 封面
             /*   if (page == 0) {
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mMapRuunable.remove(PdfParams.CURRENT_PDF_FILE_NAME);
                            saveBookCover(bm);
                        }
                    };

                    cancelRun(PdfParams.CURRENT_PDF_FILE_NAME);
                    mMapRuunable.put(PdfParams.CURRENT_PDF_FILE_NAME, runnable);
                    mThreadPool.execute(runnable);
                }*/
                mShowView.setImageBitmap(bm);
                mEntireBmh.setBm(bm);
//                setBackgroundColor(Color.TRANSPARENT);
            }
        };
        mDrawEntire.execute();

        if (mSearchView == null) {
            mSearchView = new View(mContext) {
                @Override
                protected void onDraw(final Canvas canvas) {
                    super.onDraw(canvas);

                    // Work out current total scale factor
                    // from source to view
                    final float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
                    final Paint paint = new Paint();

                    if (!mIsBlank && mSearchBoxes != null) {
                        paint.setColor(HIGHLIGHT_COLOR);
                        for (RectF rect : mSearchBoxes)
                            canvas.drawRect(rect.left * scale, rect.top * scale,
                                    rect.right * scale, rect.bottom * scale,
                                    paint);
                    }

                    if (!mIsBlank && mLinks != null && mHighlightLinks) {
                        paint.setColor(LINK_COLOR);
                        for (LinkInfo link : mLinks)
                            canvas.drawRect(link.rect.left * scale, link.rect.top * scale,
                                    link.rect.right * scale, link.rect.bottom * scale,
                                    paint);
                    }

                    if (mSelectBox != null && mText != null) {
                        paint.setColor(HIGHLIGHT_COLOR);
                        processSelectedText(new TextProcessor() {
                            RectF rect;

                            public void onStartLine() {
                                rect = new RectF();
                            }

                            public void onWord(TextWord word) {
                                rect.union(word);
                            }

                            public void onEndLine() {
                                if (!rect.isEmpty())
                                    canvas.drawRect(rect.left * scale, rect.top * scale, rect.right * scale, rect.bottom * scale, paint);
                            }
                        });
                    }
                    if (mItemSelectBox != null) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(BOX_COLOR);
                        canvas.drawRect(mItemSelectBox.left * scale, mItemSelectBox.top * scale, mItemSelectBox.right * scale, mItemSelectBox.bottom * scale, paint);
                    }

                    if (mDrawing != null) {
                        Path path = new Path();
                        PointF p;
                        Iterator<ArrayList<PointF>> it = mDrawing.iterator();
                        while (it.hasNext()) {
                            ArrayList<PointF> arc = it.next();
                            if (arc.size() >= 2) {
                                Iterator<PointF> iit = arc.iterator();
                                p = iit.next();
                                float mX = p.x * scale;
                                float mY = p.y * scale;
                                path.moveTo(mX, mY);
                                while (iit.hasNext()) {
                                    p = iit.next();
                                    float x = p.x * scale;
                                    float y = p.y * scale;
                                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                                    mX = x;
                                    mY = y;
                                }
                                path.lineTo(mX, mY);
                            }
                        }

                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);

                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(INK_THICKNESS * scale);
                        paint.setColor(INK_COLOR);
                        canvas.drawPath(path, paint);
                    }
                }
            };
            addView(mSearchView);
        }
        requestLayout();
    }


    public void setPage(final int page, final MuPDFCore mCore) {


        if (mDrawEntire != null) {
            mDrawEntire.cancel(true);
            mDrawEntire = null;
        }

        mIsBlank = false;


        mPageNumber = page;
        if (mShowView == null) {
            mShowView = new ImageView(mContext);
            mShowView.setBackgroundColor(BACKGROUND_COLOR);
//            mShowView.setBackgroundColor(UIUtils.getColor(R.color.text_color_black));
            mShowView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(mShowView);
        }


//        mEntire.setImageBitmap(null);
//        mEntireBmh.setBm(null);


        // Render the page in the background
        mDrawEntire = new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... v) {
                PointF size = mCore.getPageSize(page);
                mSourceScale = Math.min(mParentSize.x / size.x, mParentSize.y / size.y);
                Point newSize = new Point((int) (size.x * mSourceScale), (int) (size.y * mSourceScale));
                mSize = newSize;
                LogUtils.i("pdf --w" + mSize.x);
                LogUtils.i("pdf --y" + mSize.y);
                return drawPage(mSize.x, mSize.y, 0, 0, mSize.x, mSize.y);

            }

            protected void onPreExecute() {
                setBackgroundColor(BACKGROUND_COLOR);
//                mEntire.setImageBitmap(null);
//                mEntireBmh.setBm(null);

            }

            protected void onPostExecute(final Bitmap bm) {
                //TODO: 获取PDF 封面
//                if (page == 0) {
//                    final Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            mMapRuunable.remove(PdfParams.CURRENT_PDF_FILE_NAME);
//                            saveBookCover(bm);
//                        }
//                    };

//                    cancelRun(PdfParams.CURRENT_PDF_FILE_NAME);
//                    mMapRuunable.put(PdfParams.CURRENT_PDF_FILE_NAME, runnable);
//                    mThreadPool.execute(runnable);
//                }

//                mEntire.setImageBitmap(null);
//                mEntireBmh.setBm(null);
                mShowView.setImageBitmap(bm);
//                mEntireBmh.setBm(bm);
               mShowView.invalidate();
                if (mPageListener!=null){
                    mPageListener.onPangeFinishListener();
                }
            }
        };
        mDrawEntire.execute();
    }

    public void   setPageListener( PageListener pageListener ){
        mPageListener = pageListener ;
    }




    public void setSearchBoxes(RectF searchBoxes[]) {
        mSearchBoxes = searchBoxes;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    public void setLinkHighlighting(boolean f) {
        mHighlightLinks = f;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    public void deselectText() {
        mSelectBox = null;
        mSearchView.invalidate();
    }

    public void selectText(float x0, float y0, float x1, float y1) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX0 = (x0 - getLeft()) / scale;
        float docRelY0 = (y0 - getTop()) / scale;
        float docRelX1 = (x1 - getLeft()) / scale;
        float docRelY1 = (y1 - getTop()) / scale;
        // Order on Y but maintain the point grouping
        if (docRelY0 <= docRelY1)
            mSelectBox = new RectF(docRelX0, docRelY0, docRelX1, docRelY1);
        else
            mSelectBox = new RectF(docRelX1, docRelY1, docRelX0, docRelY0);

        mSearchView.invalidate();

        if (mGetText == null) {
            mGetText = new AsyncTask<Void, Void, TextWord[][]>() {
                @Override
                protected TextWord[][] doInBackground(Void... params) {
                    return getText();
                }

                @Override
                protected void onPostExecute(TextWord[][] result) {
                    mText = result;
                    mSearchView.invalidate();
                }
            };

            mGetText.execute();
        }
    }

    public void startDraw(float x, float y) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;
        if (mDrawing == null)
            mDrawing = new ArrayList<ArrayList<PointF>>();

        ArrayList<PointF> arc = new ArrayList<PointF>();
        arc.add(new PointF(docRelX, docRelY));
        mDrawing.add(arc);
    }

    public void continueDraw(float x, float y) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;

        if (mDrawing != null && mDrawing.size() > 0) {
            ArrayList<PointF> arc = mDrawing.get(mDrawing.size() - 1);
            arc.add(new PointF(docRelX, docRelY));
            mSearchView.invalidate();
        }
    }

    public void cancelDraw() {
        mDrawing = null;
        mSearchView.invalidate();
    }


    public void setItemSelectBox(RectF rect) {
        mItemSelectBox = rect;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    public void addHq(boolean update) {
        Rect viewArea = new Rect(getLeft(), getTop(), getRight(), getBottom());
        // If the viewArea's size matches the unzoomed size, there is no need for an hq patch
        if (viewArea.width() != mSize.x || viewArea.height() != mSize.y) {
            Point patchViewSize = new Point(viewArea.width(), viewArea.height());
            Rect patchArea = new Rect(0, 0, mParentSize.x, mParentSize.y);

            // Intersect and test that there is an intersection
            if (!patchArea.intersect(viewArea))
                return;

            // Offset patch area to be relative to the view top left
            patchArea.offset(-viewArea.left, -viewArea.top);

            boolean area_unchanged = patchArea.equals(mPatchArea) && patchViewSize.equals(mPatchViewSize);

            // If being asked for the same area as last time and not because of an update then nothing to do
            if (area_unchanged && !update)
                return;

            boolean completeRedraw = !(area_unchanged && update);

            // Stop the drawing of previous patch if still going
            if (mDrawPatch != null) {
                mDrawPatch.cancel(true);
                mDrawPatch = null;
            }

            if (completeRedraw) {
                // The bitmap holder mPatchBm may still be rendered to by a
                // previously invoked task, and possibly for a different
                // area, so we cannot risk the bitmap generated by this task
                // being passed to it
                mPatchBmh.drop();
                mPatchBmh = new BitmapHolder();
            }

            // Create and add the image view if not already done
            if (mPatch == null) {
                mPatch = new OpaqueImageView(mContext);
                mPatch.setScaleType(ImageView.ScaleType.FIT_CENTER);
                addView(mPatch);
                mSearchView.bringToFront();
            }

            mDrawPatch = new AsyncTask<PatchInfo, Void, PatchInfo>() {
                protected PatchInfo doInBackground(PatchInfo... v) {
                    if (v[0].completeRedraw) {
                        v[0].bm = drawPage(v[0].patchViewSize.x, v[0].patchViewSize.y,
                                v[0].patchArea.left, v[0].patchArea.top,
                                v[0].patchArea.width(), v[0].patchArea.height());
                    } else {
                        v[0].bm = updatePage(v[0].bmh, v[0].patchViewSize.x, v[0].patchViewSize.y,
                                v[0].patchArea.left, v[0].patchArea.top,
                                v[0].patchArea.width(), v[0].patchArea.height());
                    }

                    return v[0];
                }

                protected void onPostExecute(PatchInfo v) {
                    if (mPatchBmh == v.bmh) {
                        mPatchViewSize = v.patchViewSize;
                        mPatchArea = v.patchArea;
                        if (v.bm != null) {
                            mPatch.setImageBitmap(v.bm);
                            v.bmh.setBm(v.bm);
                            v.bm = null;
                        }
                        //requestLayout();
                        // Calling requestLayout here doesn't lead to a later call to layout. No idea
                        // why, but apparently others have run into the problem.
                        mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
                        invalidate();
                    }
                }
            };

            mDrawPatch.execute(new PatchInfo(patchViewSize, patchArea, mPatchBmh, completeRedraw));
        }
    }

    public void update() {
        // Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancel(true);
            mDrawEntire = null;
        }

        if (mDrawPatch != null) {
            mDrawPatch.cancel(true);
            mDrawPatch = null;
        }

        // Render the page in the background
        mDrawEntire = new AsyncTask<Void, Void, Bitmap>() {
            protected Bitmap doInBackground(Void... v) {
                // Pass the current bitmap as a basis for the update, but use a bitmap
                // holder so that the held bitmap will be nulled and not hold on to
                // memory, should this view become redundant.
                return updatePage(mEntireBmh, mSize.x, mSize.y, 0, 0, mSize.x, mSize.y);
            }

            protected void onPostExecute(Bitmap bm) {
                if (bm != null) {
                    mShowView.setImageBitmap(bm);
                    mShowView.invalidate();
//                    mEntireBmh.setBm(bm);
                }
                invalidate();
            }
        };

        mDrawEntire.execute();

        addHq(true);
    }

    public void removeHq() {
        // Stop the drawing of the patch if still going
        if (mDrawPatch != null) {
            mDrawPatch.cancel(true);
            mDrawPatch = null;
        }

        // And get rid of it
        mPatchViewSize = null;
        mPatchArea = null;
        if (mPatch != null) {
            mPatch.setImageBitmap(null);
            mPatchBmh.setBm(null);
        }
    }

    public int getPage() {
        return mPageNumber;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }


    ///////////////////////////////protected method/////////////////////////////////////////////
    protected void processSelectedText(TextProcessor tp) {
        (new TextSelector(mText, mSelectBox)).select(tp);
    }

    protected PointF[][] getDraw() {
        if (mDrawing == null)
            return null;

        PointF[][] path = new PointF[mDrawing.size()][];

        for (int i = 0; i < mDrawing.size(); i++) {
            ArrayList<PointF> arc = mDrawing.get(i);
            path[i] = arc.toArray(new PointF[arc.size()]);
        }

        return path;
    }


    //TODO:

    /**
     * 保存书的封面 ，存储需要进行压缩
     */
  /*  private void saveBookCover(Bitmap bitmap) {

        String fileName = PdfParams.CURRENT_PDF_FILE_NAME;
        String fileSavePath = FileUtils.getTextBookIconFilesDir();

        File file = new File(fileSavePath + fileName);
        if (file.exists()) {
            return;
//            file.delete();
        } else {
            File parentFile = new File(file.getParent());
            parentFile.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();

        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

/*    *//**
     * 取消
     *//*
    public void cancelRun(String url) {
        Runnable runnable = mMapRuunable.remove(url);
        if (runnable != null) {
            mThreadPool.cancel(runnable);
        }
    }*/
}
