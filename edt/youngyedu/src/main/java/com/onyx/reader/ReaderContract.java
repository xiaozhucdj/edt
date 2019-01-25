package com.onyx.reader;

import android.graphics.Bitmap;
import android.view.View;

import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.base.BasePresenter;
import com.onyx.base.BaseView;
import com.yougy.common.media.BookVoiceBean;

/**
 * Created by ming on 2017/4/1.
 */

public interface ReaderContract {

    interface ReaderView extends BaseView<ReaderPresenter> {
        void updatePage(final int page, final Bitmap bitmap, BookVoiceBean bean);
        View getContentView();
        void showThrowable(final Throwable throwable);
        void openDocumentFinsh();
        void updateDirectory(ReaderDocumentTableOfContent content);
    }

    interface ReaderPresenter extends BasePresenter {
        void openDocument(final String documentPath ,final String booId);
        void setDocumentViewRect(final int width, final int height);
        void gotoPage(final int page);
        void nextScreen();
        void prevScreen();
        Reader getReader();
        void getDirectory();
        void close();
    }
}
