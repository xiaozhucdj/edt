/**
 *
 */
package com.onyx.android.sdk.ui.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Simon
 */
public class HTMLReaderWebView extends WebView {

    private static final String TAG = "HTMLReaderWebView";

    private int mCurrentPage;
    private int mTotalPage;

    public static final int PAGE_TURN_TYPE_VERTICAL = 1;
    public static final int PAGE_TURN_TYPE_HORIZOTAL = 2;
    private int pageTurnType = PAGE_TURN_TYPE_VERTICAL;

    private int heightForSaveView = 50;
    private int pageTurnThreshold = 300;

    public void setPageTurnType(int pageTurnType) {
        this.pageTurnType = pageTurnType;
    }

    public void setHeightForSaveView(int heightForSaveView) {
        this.heightForSaveView = heightForSaveView;
    }

    public void setPageTurnThreshold(int pageTurnThreshold) {
        this.pageTurnThreshold = pageTurnThreshold;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(mInternalScrollX, mInternalScrollY);
    }

    @Override
    public void draw(Canvas canvas) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.draw(canvas);
    }

    @Override
    public void computeScroll() {
        scrollTo(mInternalScrollX, mInternalScrollY);
    }

    private int mInternalScrollX = 0;
    private int mInternalScrollY = 0;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.onScrollChanged(mInternalScrollX, mInternalScrollY, oldl, oldt);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void setScroll(int l, int t) {
        mInternalScrollX = l;
        mInternalScrollY = t;
    }

    /**
     * @param context
     */
    public HTMLReaderWebView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public HTMLReaderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public HTMLReaderWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "newProgress = " + newProgress);
                if (newProgress == 100) {
                    setScroll(0, 0);
                    mCurrentPage = 0;
                }
            }
        });

        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                setScroll(0, 0);
                mCurrentPage = 0;
                super.onPageFinished(view, url);

                final HTMLReaderWebView myWebView = (HTMLReaderWebView) view;


                String varMySheet = "var mySheet = document.styleSheets[0];";

                String addCSSRule = "function addCSSRule(selector, newRule) {"
                        + "ruleIndex = mySheet.cssRules.length;"
                        + "mySheet.insertRule(selector + '{' + newRule + ';}', ruleIndex);"

                        + "}";

                String insertRule1 = "addCSSRule('html', 'padding: 0px; height: "
                        + (myWebView.getMeasuredHeight() / getContext().getResources().getDisplayMetrics().density)
                        + "px; -webkit-column-gap: 0px; -webkit-column-width: "
                        + myWebView.getMeasuredWidth() + "px;  text-align:justify; ')";


                myWebView.loadUrl("javascript:" + varMySheet);
                myWebView.loadUrl("javascript:" + addCSSRule);
                myWebView.loadUrl("javascript:" + insertRule1);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        getSettings().setJavaScriptEnabled(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        setOnTouchListener(new View.OnTouchListener() {
            float mX = 0;
            float mY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX = event.getX();
                        mY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        float x = event.getX();
                        float y = event.getY();

                        if (pageTurnType == PAGE_TURN_TYPE_HORIZOTAL) {
                            if (x - mX > pageTurnThreshold) {
                                prevPage();
                            } else if (mX - x > pageTurnThreshold) {
                                nextPage();
                            }

                        } else if (pageTurnType == PAGE_TURN_TYPE_VERTICAL) {
                            if (y - mY > pageTurnThreshold) {
                                prevPage();
                            } else if (mY - y > pageTurnThreshold) {
                                nextPage();
                            }
                        } else if (pageTurnType == (PAGE_TURN_TYPE_VERTICAL & PAGE_TURN_TYPE_HORIZOTAL)) {
                            if (Math.abs(x - mX) > Math.abs(y - mY)) {
                                if (x - mX > pageTurnThreshold) {
                                    prevPage();
                                } else if (mX - x > pageTurnThreshold) {
                                    nextPage();
                                }
                            } else {
                                if (y - mY > pageTurnThreshold) {
                                    prevPage();
                                } else if (mY - y > pageTurnThreshold) {
                                    nextPage();
                                }
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        break;
                }

                return false;
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        setLongClickable(false);
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    public interface OnPageChangedListener {
        public void onPageChanged(int totalPage, int curPage);
    }

    private OnPageChangedListener mOnPageChangedListener;

    public void registerOnOnPageChangedListener(OnPageChangedListener l) {
        mOnPageChangedListener = l;
    }

    public void unRegisterOnOnPageChangedListener() {
        mOnPageChangedListener = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scrollTo(mInternalScrollX, mInternalScrollY);
        super.onDraw(canvas);
        refreshWebViewSize();
    }

    private void refreshWebViewSize() {
        int width = getWidth();
        int scrollWidth = computeHorizontalScrollRange();

        if (width == 0) {
            return;
        }

        mTotalPage = (scrollWidth + width - 1) / width;
        if (mCurrentPage > mTotalPage) {
            mCurrentPage = mTotalPage;
        }

        if (mCurrentPage <= 0) {
            mCurrentPage = 1;
        }

        if (mOnPageChangedListener != null)
            mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
    }

    public void nextPage() {
        if (mCurrentPage < mTotalPage) {
            // EpdController.invalidate(webView, UpdateMode.GC);
            mCurrentPage++;
            setScroll(getScrollX() + getWidth(), 0);
            scrollBy(getWidth(), 0);
            if (mOnPageChangedListener != null)
                mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
        }
    }

    public void prevPage() {
        if (mCurrentPage > 1) {
            // EpdController.invalidate(webView, UpdateMode.GC);
            mCurrentPage--;
            setScroll(getScrollX() - getWidth(), 0);
            scrollBy(getWidth(), 0);
            if (mOnPageChangedListener != null)
                mOnPageChangedListener.onPageChanged(mTotalPage, mCurrentPage);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            nextPage();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            prevPage();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getHtmlCacheDir(Context context) {
        return "/data/data/" + context.getPackageName() + "/html";
    }

    public String saveWebContentToFile(Context context, String expString) {

        String saveTempFile = getHtmlCacheDir(context) + "/result.html";

        OutputStreamWriter outputStreamWriter = null;
        File dirFile = new File(getHtmlCacheDir(context));
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File saveTemp = new File(saveTempFile);
        if (saveTemp.exists()) {
            saveTemp.delete();
        }
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(saveTemp));
            if (expString != null) {
                saveTemp.createNewFile();
                outputStreamWriter.write(expString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saveTempFile;
    }

}
