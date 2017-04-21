package com.onyx.reader;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.PreviousScreenRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

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

    @Override
    public void openDocument(String documentPath) {
        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(), false);
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
                    mPags=getReader().getNavigator().getTotalPage() ;
                    readerView.openDocumentFinsh();
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void gotoPage(final int page) {
        GotoPageRequest gotoPageRequest = new GotoPageRequest(page);
        getReader().submitRequest(getContext(), gotoPageRequest, new BaseCallback() {
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
        final GetTableOfContentRequest  request = new GetTableOfContentRequest  ();
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

    private Context getContext() {
        return readerView.getViewContext();
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unSubscribe() {

    }

    public int getPages(){
        return  mPags;
    }
}
