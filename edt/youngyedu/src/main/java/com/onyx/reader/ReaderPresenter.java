package com.onyx.reader;

import android.content.Context;
import android.graphics.RectF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.GammaCorrectionRequest;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.ScaleByRectRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.data.DrmCertificateFactory;
import com.yougy.common.global.FileContonst;
import com.yougy.common.media.BookVoiceBean;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

/**
 * Created by ming on 2017/4/1.
 */

public class ReaderPresenter implements ReaderContract.ReaderPresenter {

    private ReaderContract.ReaderView readerView;
    private Reader reader;
    private int mPags;
    private String path;
    private int mPage;
    private String mBookId;

    /**
     * 自适应 放大 ，裁边
     */
    private boolean isSelfAdapter = false;

    /**
     * 是否使用 语音点读
     */
    private boolean mIsUserVoice = false;

    /**
     * 是否使用gama值
     */
    private boolean mIsSetGama  ;
    private RectF viewportInDoc;

    public ReaderPresenter(ReaderContract.ReaderView readerView) {
        this.readerView = readerView;
    }

    @Override
    public void openDocument(final String documentPath, String bookId) {
        mBookId = bookId;
        path = documentPath;
        DrmCertificateFactory factory = new DrmCertificateFactory(readerView.getViewContext());
        if (!StringUtils.isEmpty(bookId)) {
            String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
            if (!StringUtils.isEmpty(keys) && keys.contains(bookId)) {
                try {
                    JSONObject object = new JSONObject(keys);
                    String key = object.getString(bookId);
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
//                    if (FileUtils.getDownBookSuffix(path).equalsIgnoreCase(FileUtils.epub)) {
//                        setChangeStyleReqest();
//                    } else {
//                        mPags = getReader().getNavigator().getTotalPage();
//                        readerView.openDocumentFinsh();
//                    }
                    mPags = getReader().getNavigator().getTotalPage();
                    readerView.openDocumentFinsh();
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }


//    /**
//     * 设置epub格式
//     */
//    private void setChangeStyleReqest() {
//
//        ReaderTextStyle style = ReaderTextStyle.defaultStyle();
//        //设置字体大小
//        style.setFontSize(ReaderTextStyle.SPUnit.create(23.0F));
//        //设置边距
//        ReaderTextStyle.Percentage left = new ReaderTextStyle.Percentage(130);
//        ReaderTextStyle.Percentage bottom = new ReaderTextStyle.Percentage(160);
//        ReaderTextStyle.Percentage right = new ReaderTextStyle.Percentage(130);
//        ReaderTextStyle.Percentage top = new ReaderTextStyle.Percentage(160);
//        style.setPageMargin(new ReaderTextStyle.PageMargin(left, bottom, right, top));
////        //对齐方式 ,两边对齐
////        style.setAlignment(ReaderTextStyle.Alignment.ALIGNMENT_NONE);
////        //设置行间距
////        style.setLineSpacing(new ReaderTextStyle.Percentage(120));
//
//        ChangeStyleRequest request = new ChangeStyleRequest(style);
//        getReader().submitRequest(getContext(), request, new BaseCallback() {
//            @Override
//            public void done(BaseRequest baseRequest, Throwable throwable) {
//                mPags = getReader().getNavigator().getTotalPage();
//                readerView.openDocumentFinsh();
//            }
//        });
//    }

    @Override
    public void gotoPage(final int page) {
        mPage = page;
        LogUtils.e(getClass().getName(), "gotoPage..............." + page);
        GotoPageRequest gotoPageRequest = new GotoPageRequest(page);
        getReader().submitRequest(getContext(), gotoPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    if (isSelfAdapter) {
                        setCropPage(-0.02f);
                    } else if (mIsSetGama) {
                        setGama(page);
                    } else {
                        readerView.updatePage(page, getReader().getViewportBitmap().getBitmap(), getBookVice());
                    }
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    private void setCropPage(final float delta) {
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest((mPage + ""));
        getReader().submitRequest(readerView.getViewContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    viewportInDoc = request.getReaderViewInfo().viewportInDoc;
                    final RectF rect = new RectF();
                    float offset = viewportInDoc.width() * delta;
                    rect.set(viewportInDoc.left + offset,
                            viewportInDoc.top + offset,
                            viewportInDoc.right - offset,
                            viewportInDoc.bottom - offset);


                    final ScaleByRectRequest request = new ScaleByRectRequest(mPage + "", rect);
                    getReader().submitRequest(readerView.getViewContext(), request, new BaseCallback() {
                        @Override
                        public void done(BaseRequest baseRequest, Throwable throwable) {
                            if (throwable == null) {
                                if (mIsSetGama) {
                                    setGama(mPage);
                                } else {
                                    readerView.updatePage(mPage, getReader().getViewportBitmap().getBitmap(), getBookVice());
                                }
                            } else {
                                readerView.showThrowable(throwable);
                            }
                        }
                    });
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    private void setGama(final int page) {
        int defaultGamma = 100; // gamma correction ranges between [100, 200], 100 means no gamma correction, 200 is max gamma correction
        int globalGamma = defaultGamma; // globalGamma is not used yet
        int imageGamma = 200; // imageGamma works only when textGamma is not set
        int textGamma = 200; // text gamma works for PDF texts
        int glyphEmbolden = 0; // ranges from [0, 5], 0 means no embolden, 5 is max embolden
        GammaCorrectionRequest gammaRequest = new GammaCorrectionRequest(100, 100, 200, 1);
        getReader().submitRequest(getContext(), gammaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    mIsSetGama = false;
                    readerView.updatePage(page, getReader().getViewportBitmap().getBitmap(), getBookVice());
                } else {
                    readerView.showThrowable(throwable);
                }
            }
        });
    }

    @Override
    public void nextScreen() {
        LogUtils.e(getClass().getName(), "nextScreen...............");
//        final NextScreenRequest nextScreenRequest = new NextScreenRequest();
//        getReader().submitRequest(getContext(), nextScreenRequest, new BaseCallback() {
//            @Override
//            public void done(BaseRequest baseRequest, Throwable throwable) {
//                if (throwable == null) {
//                    readerView.updatePage(getCurrentPage(nextScreenRequest), getReader().getViewportBitmap().getBitmap());
//                } else {
//                    readerView.showThrowable(throwable);
//                }
//            }
//        });
    }

    @Override
    public void prevScreen() {
        LogUtils.e(getClass().getName(), "prevScreen...............");
//        final PreviousScreenRequest nextScreenRequest = new PreviousScreenRequest();
//        getReader().submitRequest(getContext(), nextScreenRequest, new BaseCallback() {
//            @Override
//            public void done(BaseRequest baseRequest, Throwable throwable) {
//                if (throwable == null) {
//                    readerView.updatePage(getCurrentPage(nextScreenRequest), getReader().getViewportBitmap().getBitmap());
//                } else {
//                    readerView.showThrowable(throwable);
//                }
//            }
//        });
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
        if (reader != null) {
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


    private BookVoiceBean getBookVice() {
        if (!mIsUserVoice) {
            return null;
        }
        BookVoiceBean bean = null;
        //文件角标1开始 ，pdf角标0开始
        int index = mPage + 1;
        String filePath = FileUtils.getMediaJsonPath() + mBookId + "/" + index + ".txt";
        if (FileUtils.exists(filePath)) {
            String json = FileUtils.readerFile(filePath);
            if (!StringUtils.isEmpty(json)) {
                LogUtils.e("media  ..json ==" + json);
                bean = GsonUtil.fromJson(json, BookVoiceBean.class);
                if (bean.getVoiceInfos() != null && bean.getVoiceInfos().size() > 0) {
                    Collections.sort(bean.getVoiceInfos());
                }
            }
        }
        return bean;
    }

    public void setSelfAdapter(boolean selfAdapter) {
        isSelfAdapter = selfAdapter;
    }

    public void setmIsUserVoice(boolean mIsUserVoice) {
        this.mIsUserVoice = mIsUserVoice;
    }

    public void setmIsSetGama(boolean mIsSetGama) {
        this.mIsSetGama = mIsSetGama;
    }
}

