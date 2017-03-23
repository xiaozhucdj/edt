package com.artifex.mupdfdemo.pdf.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.artifex.mupdfdemo.pdf.PdfInterface.MuPDFView;
import com.artifex.mupdfdemo.pdf.bean.LinkInfo;
import com.artifex.mupdfdemo.pdf.bean.LinkInfoExternal;
import com.artifex.mupdfdemo.pdf.bean.LinkInfoInternal;
import com.artifex.mupdfdemo.pdf.bean.LinkInfoRemote;
import com.artifex.mupdfdemo.pdf.bean.LinkInfoVisitor;
import com.artifex.mupdfdemo.pdf.bean.SearchTaskResult;
import com.artifex.mupdfdemo.pdf.enumType.Hit;


/**
 * Created by Administrator on 2016/6/28.
 */
public class MuPDFReaderView extends ReaderView {
    ////////////////////////////////enum////////////////////////////////////////
    public enum Mode {
        Viewing, Selecting, Drawing
    }

    ////////////////////////////////Filed////////////////////////////////////////
    private final Context mContext;
    //链接是否可按
    private boolean mLinksEnabled = false;
    // 控制View当前的状态
    private Mode mMode = Mode.Viewing;
    //点击是否可用
    private boolean tapDisabled = false;
    //点击页面的边缘
    private int tapPageMargin;

    ////////////////////////////////protected method////////////////////////////////////////

    protected void onTapMainDocArea() {
    }

    protected void onDocMotion() {
    }

    protected void onHit(Hit item) {
    }


    ////////////////////////////////构造 函数////////////////////////////////////////
    public MuPDFReaderView(Activity context) {
        super(context);
        mContext = context;
        // Get the screen size etc to customise tap margins.
        // We calculate the size of 1 inch of the screen for tapping.
        // On some devices the dpi values returned are wrong, so we
        // sanity check it: we first restrict it so that we are never
        // less than 100 pixels (the smallest Android device screen
        // dimension I've seen is 480 pixels or so). Then we check
        // to ensure we are never more than 1/5 of the screen width.
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        tapPageMargin = (int) dm.xdpi;
        if (tapPageMargin < 100)
            tapPageMargin = 100;
        if (tapPageMargin > dm.widthPixels / 5)
            tapPageMargin = dm.widthPixels / 5;
    }


    ////////////////////////////////public method////////////////////////////////////////

    /**
     * 设置链接是否可按
     *
     * @param b
     */
    public void setLinksEnabled(boolean b) {
        mLinksEnabled = b;
        resetupChildren();
    }

    /**
     * 设置 view 显示的模式
     *
     * @param m
     */
    public void setMode(Mode m) {
        mMode = m;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LinkInfo link = null;

        if (mMode == Mode.Viewing && !tapDisabled) {
            ViewGroup viewGroup = (ViewGroup) getDisplayedView();
            MuPDFView pageView = (MuPDFView) viewGroup.getChildAt(0);
            //TODO:接口的实现类暂时还没去实现
            Hit item = pageView.passClickEvent(e.getX(), e.getY());
            onHit(item);
            if (item == Hit.Nothing) {
                if (mLinksEnabled && pageView != null && (link = pageView.hitLink(e.getX(), e.getY())) != null) {
                    link.acceptVisitor(new LinkInfoVisitor() {
                        @Override
                        public void visitInternal(LinkInfoInternal li) {
                            // Clicked on an internal (GoTo) lin
                            // 跳转到PDF 内部链接
                            setDisplayedViewIndex(li.pageNumber);
                        }

                        @Override
                        public void visitExternal(LinkInfoExternal li) {
                            //网页方式打开
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(li.url));
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void visitRemote(LinkInfoRemote li) {
                            // Clicked on a remote (GoToR) link
                        }
                    });
                } else if (e.getX() < tapPageMargin) {
                    //切换 上一页
                    super.smartMoveBackwards();
                } else if (e.getX() > super.getWidth() - tapPageMargin) {
                    //切换 下一页
                    super.smartMoveForwards();
                } else if (e.getY() < tapPageMargin) {
                    //切换 上一页
                    super.smartMoveBackwards();
                } else if (e.getY() > super.getHeight() - tapPageMargin) {
                    //切换 下一页
                    super.smartMoveForwards();
                } else {
                    // 应该是页码
                    onTapMainDocArea();
                }
            }
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return super.onDown(e);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        ViewGroup viewGroup = (ViewGroup) getDisplayedView();

        // 出现一个
        if ( viewGroup == null|| viewGroup.getChildAt(0) == null){
            return  true;
        }

        MuPDFView pageView = (MuPDFView) viewGroup.getChildAt(0);
        switch (mMode) {
            case Viewing:
                if (!tapDisabled)
                    onDocMotion();
                return super.onScroll(e1, e2, distanceX, distanceY);
            case Selecting:
                if (pageView != null)
                    pageView.selectText(e1.getX(), e1.getY(), e2.getX(), e2.getY());
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        switch (mMode) {
            case Viewing:
                return super.onFling(e1, e2, velocityX, velocityY);
            default:
                return true;
        }
    }


    public boolean onScaleBegin(ScaleGestureDetector d) {
        // Disabled showing the buttons until next touch.
        // Not sure why this is needed, but without it
        // pinch zoom can make the buttons appear
        tapDisabled = true;
        return super.onScaleBegin(d);
    }

    public boolean onTouchEvent(MotionEvent event) {

        if (mMode == Mode.Drawing) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    break;
            }
        }

        if ((event.getAction() & event.getActionMasked()) == MotionEvent.ACTION_DOWN) {
            tapDisabled = false;
        }

        return super.onTouchEvent(event);
    }

    private float mX, mY;

    private static final float TOUCH_TOLERANCE = 2;

    private void touch_start(float x, float y) {

        MuPDFView pageView = (MuPDFView) getDisplayedView();
        if (pageView != null) {
            pageView.startDraw(x, y);
        }
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            MuPDFView pageView = (MuPDFView) getDisplayedView();
            if (pageView != null) {
                pageView.continueDraw(x, y);
            }
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {

        // NOOP
    }

    protected void onChildSetup(int i, View v) {
        ViewGroup viewGroup = (ViewGroup) v;
        if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber == i)

            ((MuPDFView) viewGroup.getChildAt(0)).setSearchBoxes(SearchTaskResult.get().searchBoxes);
        else
            ((MuPDFView) viewGroup.getChildAt(0)).setSearchBoxes(null);

        ((MuPDFView) viewGroup.getChildAt(0)).setLinkHighlighting(mLinksEnabled);

        ((MuPDFView) viewGroup.getChildAt(0)).setChangeReporter(new Runnable() {
            public void run() {
                applyToChildren(new ViewMapper() {
                    @Override
                    void applyToView(View view) {
                        ((MuPDFView) view).update();
                    }
                });
            }
        });
    }

    protected void onMoveToChild(int i) {
        if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber != i) {
            SearchTaskResult.set(null);
            resetupChildren();
        }
    }

    @Override
    protected void onMoveOffChild(int i) {
        ViewGroup viewGroup = (ViewGroup) getView(i);
        if (viewGroup != null)
            ((MuPDFView) viewGroup.getChildAt(0)).deselectAnnotation();
    }


    protected void onSettle(View v) {
        ViewGroup viewGroup = (ViewGroup) v;
        // When the layout has settled ask the page to render
        // in HQ
        ((MuPDFView) viewGroup.getChildAt(0)).addHq(false);
    }

    protected void onUnsettle(View v) {
        // When something changes making the previous settled view
        // no longer appropriate, tell the page to remove HQ
        ViewGroup viewGroup = (ViewGroup) v;
        ((MuPDFView) viewGroup.getChildAt(0)).removeHq();
    }

    @Override
    protected void onNotInUse(View v) {
        ViewGroup viewGroup = (ViewGroup) v;
        ((MuPDFView) viewGroup.getChildAt(0)).releaseResources();
    }

    @Override
    protected void onScaleChild(View v, Float scale) {
    }
}
