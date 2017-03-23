package com.yougy.home.fragment.showFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yougy.common.fragment.BFragment;

/**
 * Created by jiangliang on 2016/8/2.
 */
public class DrawFragment extends BFragment {
    private static final String WEB_URL = "drawingboard.html";
    private final static String FILE_SCHEME = "file";
    private WebView mWebView;

    private Context mContext;

    private static final String TAG = "DrawFragment";
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWebView = new WebView(mContext);
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        return mWebView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Uri mResourceUri = new Uri.Builder().scheme(FILE_SCHEME)
                .encodedAuthority("/android_asset")
                .appendEncodedPath(Uri.encode(WEB_URL, "/"))
                .build();
        mWebView.loadUrl(mResourceUri.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    class WebAppInterface {
        DrawFragment mDrawFragment;

        public WebAppInterface(DrawFragment drawFragment) {
            mDrawFragment = drawFragment;
        }

        @JavascriptInterface
        public void drawCallBack(String points) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mDrawFragment);
            ft.commit();
            if (null != onDrawListener) {
                onDrawListener.onDraw2UI(points);
            }
        }
    }

    private OnDrawListener onDrawListener;

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    public interface OnDrawListener {
        void onDraw2UI(String points);
    }

}
