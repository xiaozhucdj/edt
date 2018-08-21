package com.onyx.reader;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.ChangeStyleRequest;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.GammaCorrectionRequest;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.PreviousScreenRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.data.DrmCertificateFactory;
import com.yougy.common.global.FileContonst;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ming on 2017/4/1.
 */

public class ReaderPresenter implements ReaderContract.ReaderPresenter {

    private ReaderContract.ReaderView readerView;
    private Reader reader;
    private int mPags;
    private String path;
    private boolean mIsInit ;
    private int mPage;
    private boolean mIsCropPage ;

    public void setCropPage(boolean isCropPage){
        mIsCropPage = isCropPage ;
    }

    public ReaderPresenter(ReaderContract.ReaderView readerView) {
        this.readerView = readerView;
    }

    @Override
    public void openDocument(final String documentPath, String bookId) {
        mIsInit  =false ;
        path = documentPath;
        DrmCertificateFactory factory = new DrmCertificateFactory(readerView.getViewContext());
        if (!StringUtils.isEmpty(bookId)) {
            String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
            if (!StringUtils.isEmpty(keys) && keys.contains(bookId)) {
                try {
                    JSONObject object = new JSONObject(keys);
                    String key = object.getString(bookId) ;
//                    System.out.println("object.getString(bookId) ...."+bookId+"...."+key.substring(key.length()-50 ,key.length()));
                    factory.setKey(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(),
                factory, false);
        getReader().submitRequest(getContext(), openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
//                    setDocumentViewRect(readerView.getContentView().getWidth(), readerView.getContentView().getHeight());
//                    int h = 920;
//                    int w = 960;
                    int h = readerView.getContentView().getHeight();
                    int w = readerView.getContentView().getWidth();
                    setDocumentViewRect(w, h);
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void setDocumentViewRect(int width, int height) {
        CreateViewRequest createViewRequest = new CreateViewRequest(width, height);
        getReader().submitRequest(getContext(), createViewRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    if (FileUtils.getDownBookSuffix(path).equalsIgnoreCase(FileUtils.epub)) {
                        setChangeStyleReqest();
                    } else {
                        mPags = getReader().getNavigator().getTotalPage();
                        readerView.openDocumentFinsh();
                    }
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }


    /**
     * 设置epub格式
     */
    private void setChangeStyleReqest() {

        ReaderTextStyle style = ReaderTextStyle.defaultStyle();
        //设置字体大小
        style.setFontSize(ReaderTextStyle.SPUnit.create(23.0F));
        //设置边距
        ReaderTextStyle.Percentage left = new ReaderTextStyle.Percentage(130);
        ReaderTextStyle.Percentage bottom = new ReaderTextStyle.Percentage(160);
        ReaderTextStyle.Percentage right = new ReaderTextStyle.Percentage(130);
        ReaderTextStyle.Percentage top = new ReaderTextStyle.Percentage(160);
        style.setPageMargin(new ReaderTextStyle.PageMargin(left, bottom, right, top));
//        //对齐方式 ,两边对齐
//        style.setAlignment(ReaderTextStyle.Alignment.ALIGNMENT_NONE);
//        //设置行间距
//        style.setLineSpacing(new ReaderTextStyle.Percentage(120));

        ChangeStyleRequest request = new ChangeStyleRequest(style);
        getReader().submitRequest(getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                mPags = getReader().getNavigator().getTotalPage();
                readerView.openDocumentFinsh();
            }
        });
    }

    @Override
    public void gotoPage(final int page) {
        mPage = page ;
        LogUtils.e(getClass().getName(), "gotoPage..............." + page);
        GotoPageRequest gotoPageRequest = new GotoPageRequest(page);
        getReader().submitRequest(getContext(), gotoPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    if (mIsInit){
                        readerView.updatePage(page, getReader().getViewportBitmap().getBitmap()) ;
                    }else{
                        gamma(page);
                    }
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    private void gamma(final int page) {
        int defaultGamma = 100; // gamma correction ranges between [100, 200], 100 means no gamma correction, 200 is max gamma correction
        int globalGamma = defaultGamma; // globalGamma is not used yet
        int imageGamma = 200; // imageGamma works only when textGamma is not set
        int textGamma = 200; // text gamma works for PDF texts
        int glyphEmbolden = 0; // ranges from [0, 5], 0 means no embolden, 5 is max embolden
        GammaCorrectionRequest gammaRequest = new GammaCorrectionRequest(100, 100, 150, 0);
        getReader().submitRequest(getContext(), gammaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {

                    if (mIsCropPage){
                        cropPage();
                    }else{
                        mIsInit = true ;
                        readerView.updatePage(page, getReader().getViewportBitmap().getBitmap());
                    }
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void nextScreen() {
        LogUtils.e(getClass().getName(), "nextScreen...............");
        final NextScreenRequest nextScreenRequest = new NextScreenRequest();
        getReader().submitRequest(getContext(), nextScreenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerView.updatePage(getCurrentPage(nextScreenRequest), getReader().getViewportBitmap().getBitmap());
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void prevScreen() {
        LogUtils.e(getClass().getName(), "prevScreen...............");
        final PreviousScreenRequest nextScreenRequest = new PreviousScreenRequest();
        getReader().submitRequest(getContext(), nextScreenRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerView.updatePage(getCurrentPage(nextScreenRequest), getReader().getViewportBitmap().getBitmap());
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    public String getCurrentPageName(final BaseReaderRequest request) {
        return request.getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage(final BaseReaderRequest request) {
        return PagePositionUtils.getPageNumber(getCurrentPageName(request));
    }

    @Override
    public Reader getReader() {
        if (reader == null) {
            reader = new Reader();
        }
        return reader;
    }

    @Override
    public void getDirectory() {
        final GetTableOfContentRequest request = new GetTableOfContentRequest();
        getReader().submitRequest(getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerView.updateDirectory(request.getReaderUserDataInfo().getTableOfContent());
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void close() {
        final CloseRequest closeRequest = new CloseRequest();
        if (reader != null){
            reader.submitRequest(getContext(), closeRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest baseRequest, Throwable throwable) {
                    if (throwable == null) {
                        //close success
                    }
                }
            });
        }
    }

    private Context getContext() {
        return readerView.getViewContext();
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unSubscribe() {

    }

    public int getPages() {
        return mPags;
    }



    /**裁剪*/
    private void cropPage() {
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest((mPage+""));
        getReader().submitRequest(getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    mIsInit = true ;
                    readerView.updatePage(mPage, getReader().getViewportBitmap().getBitmap());
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }
}

