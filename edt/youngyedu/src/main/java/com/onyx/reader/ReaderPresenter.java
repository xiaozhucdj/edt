package com.onyx.reader;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.GammaCorrectionRequest;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.PreviousScreenRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.data.DrmCertificateFactory;
import com.yougy.common.utils.LogUtils;

/**
 * Created by ming on 2017/4/1.
 */

public class ReaderPresenter implements ReaderContract.ReaderPresenter {

    private ReaderContract.ReaderView readerView;
    private Reader reader;
    private int mPags;

    public ReaderPresenter(ReaderContract.ReaderView readerView) {
        this.readerView = readerView;
    }

    String bookforNumber = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSwehg/hFz+VCKc9mAYVSPclL+vbqD9YuVVF4zed7ZgJbl3Tg7e3DnHb/uRXK0t+BEl40UtaGguIFWbgOVySlrGSp8Z81EAB37ZoVU30Rqy0LP" ;
    String springKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWEMKTmfN38grscTip5OsjWayRat2g6uAuLkb1TMFTg1NRrdgchoCxsyG8NCMz3diCTJnl2Bg5dkKHpszCrajCiQO+ki6J3WU889+O4l1kMaG54lwrWIsIZJNgJmQ9GGl/DY4OAhV6Lg0rY6mymdHzEPeJiaBWed5VUxAXp58FQQIDAQAB" ;
    @Override
    public void openDocument(String documentPath) {
        DrmCertificateFactory factory  = new DrmCertificateFactory(readerView.getViewContext()) ;

//        if (documentPath.contains("7245022")){
//            factory.setKey(bookforNumber);
//        }else if(documentPath.contains("7244705")){
//            factory.setKey(springKey);
//        }else{
//            factory.setKey("");
//        }

        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(),
                factory  , false);
        getReader().submitRequest(getContext(), openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    setDocumentViewRect(readerView.getContentView().getWidth(), readerView.getContentView().getHeight());
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
//                    gotoPage(0);
                    mPags = getReader().getNavigator().getTotalPage();
                    readerView.openDocumentFinsh();
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void gotoPage(final int page) {
        LogUtils.e(getClass().getName(), "gotoPage..............." + page);
        GotoPageRequest gotoPageRequest = new GotoPageRequest(page);
        getReader().submitRequest(getContext(), gotoPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
//                    readerView.updatePage(page, getReader().getViewportBitmap().getBitmap());
                    gamma(page);
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
        GammaCorrectionRequest gammaRequest = new GammaCorrectionRequest(globalGamma, imageGamma, textGamma, glyphEmbolden);
        getReader().submitRequest(getContext(), gammaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    readerView.updatePage(page, getReader().getViewportBitmap().getBitmap());
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
        reader.submitRequest(getContext(), closeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    //close success
                }
            }
        });
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
}
