package com.yougy.plide.pipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.ChangeStyleRequest;
import com.onyx.android.sdk.reader.host.request.CloseRequest;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.GammaCorrectionRequest;
import com.onyx.android.sdk.reader.host.request.GotoPageRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.data.DrmCertificateFactory;
import com.yougy.common.global.FileContonst;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.plide.PlideRequestProcessor;
import com.yougy.plide.Result;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ming on 2017/4/1.
 */

public class PlideReaderPresenter {
    Context mContext;
    private Reader reader = new Reader();
    private int mTotalPags;
    private String path;
    private boolean mIsInit;

    private int mPage;
    private boolean mIsCropPage;

    public void setCropPage(boolean isCropPage) {
        mIsCropPage = isCropPage;
    }

    public PlideReaderPresenter(Context context) {
        this.mContext = context;
    }

    public Result<Integer> openDocument(String documentPath, String bookId) throws InterruptedException {
        mIsInit = false;
        path = documentPath;
        DrmCertificateFactory factory = new DrmCertificateFactory(mContext);
        if (!StringUtils.isEmpty(bookId)) {
            String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
            if (!StringUtils.isEmpty(keys) && keys.contains(bookId)) {
                try {
                    JSONObject object = new JSONObject(keys);
                    factory.setKey(object.getString(bookId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(),
                factory, false);
        Result<Integer> result = new Result<Integer>();
        result.setResultCode(-999);
        getReader().submitRequest(getContext(), openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -1) {
                return result;
            } else {
                int h = 920;
                int w = 960;
                try {
                    Result<Integer> tempResult = setDocumentViewRect(w, h);
                    if (tempResult.getResultCode() == 0) {
                        result.setResultCode(0);
                        result.setData(tempResult.getData());
                        result.setErrorMsg(null);
                        return result;
                    } else {
                        result.setResultCode(-1);
                        result.setErrorMsg(tempResult.getErrorMsg());
                        return result;
                    }
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }
    }

    public Result<Integer> openDocument(String documentPath, String bookId, boolean isAutoResize, ImageView imageView) throws InterruptedException {
        mIsInit = false;
        path = documentPath;
        DrmCertificateFactory factory = new DrmCertificateFactory(mContext);
        if (!StringUtils.isEmpty(bookId)) {
            String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
            if (!StringUtils.isEmpty(keys) && keys.contains(bookId)) {
                try {
                    JSONObject object = new JSONObject(keys);
                    factory.setKey(object.getString(bookId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(),
                factory, false);
        Result<Integer> result = new Result<Integer>();
        result.setResultCode(-999);
        getReader().submitRequest(getContext(), openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -1) {
                return result;
            } else {
                int h = 920;
                int w = 960;
                if (isAutoResize) {
                    h = imageView.getHeight();
                    w = imageView.getWidth();
                }
                Log.d("ContentDisplay", "openDocument: w = " + w + "  h = " + h);
                try {
                    Result<Integer> tempResult = setDocumentViewRect(w, h);
                    if (tempResult.getResultCode() == 0) {
                        result.setResultCode(0);
                        result.setData(tempResult.getData());
                        result.setErrorMsg(null);
                        return result;
                    } else {
                        result.setResultCode(-1);
                        result.setErrorMsg(tempResult.getErrorMsg());
                        return result;
                    }
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }
    }

    public Result<Integer> setDocumentViewRect(int width, int height) throws InterruptedException {
        Result<Integer> result = new Result<Integer>();
        result.setResultCode(-999);
        CreateViewRequest createViewRequest = new CreateViewRequest(width, height);
        getReader().submitRequest(getContext(), createViewRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -1) {
                return result;
            } else {
                if (FileUtils.getDownBookSuffix(path).equalsIgnoreCase(FileUtils.epub)) {
                    try {
                        Result<Integer> tempResult = setChangeStyleReqest();
                        if (tempResult.getResultCode() == 0) {
                            result.setResultCode(0);
                            result.setData(tempResult.getData());
                            return result;
                        } else {
                            result.setResultCode(-1);
                            result.setErrorMsg(tempResult.getErrorMsg());
                            return result;
                        }
                    } catch (InterruptedException e) {
                        throw e;
                    }
                } else {
                    mTotalPags = getReader().getNavigator().getTotalPage();
                    result.setResultCode(0);
                    result.setData(mTotalPags);
                    return result;
                }
            }
        }
    }

    public Result<Bitmap> gotoPage(int page) throws InterruptedException {
        mPage = page;
        Result<Bitmap> result = new Result<Bitmap>();
        result.setResultCode(-999);
        GotoPageRequest gotoPageRequest = new GotoPageRequest(page);
        getReader().submitRequest(getContext(), gotoPageRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    throwable.printStackTrace();
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -1) {
                return result;
            } else {
//                if (mIsInit) {
//                    result.setResultCode(0);
//                    result.setData(getReader().getViewportBitmap().getBitmap());
//                    return result;
//                } else {
//                    try {
//                        Result<Bitmap> tempResult = gamma(page);
//                        if (tempResult.getResultCode() == 0){
//                            result.setResultCode(0);
//                            result.setData(tempResult.getData());
//                            return result;
//                        }
//                        else{
//                            result.setResultCode(-1);
//                            result.setErrorMsg(tempResult.getErrorMsg());
//                            return result;
//                        }
//                    } catch (InterruptedException e) {
//                        throw e;
//                    }
//                }

                result.setResultCode(0);
                result.setData(getReader().getViewportBitmap().getBitmap());
                return result;

            }
        }
    }

    private Result<Bitmap> gamma(final int page) throws InterruptedException {
//        int defaultGamma = 100; // gamma correction ranges between [100, 200], 100 means no gamma correction, 200 is max gamma correction
//        int globalGamma = defaultGamma; // globalGamma is not used yet
//        int imageGamma = 200; // imageGamma works only when textGamma is not set
//        int textGamma = 200; // text gamma works for PDF texts
//        int glyphEmbolden = 0; // ranges from [0, 5], 0 means no embolden, 5 is max embolden
        Result<Bitmap> result = new Result<Bitmap>();
        result.setResultCode(-999);
        GammaCorrectionRequest gammaRequest = new GammaCorrectionRequest(100, 100, 200, 1);
        getReader().submitRequest(getContext(), gammaRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    throwable.printStackTrace();
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == -1) {
                return result;
            } else {
                if (mIsCropPage) {
                    try {
                        Result<Bitmap> tempResult = cropPage();
                        if (tempResult.getResultCode() == 0) {
                            result.setResultCode(0);
                            result.setData(tempResult.getData());
                            return result;
                        } else {
                            result.setResultCode(-1);
                            result.setErrorMsg(tempResult.getErrorMsg());
                            return result;
                        }
                    } catch (InterruptedException e) {
                        throw e;
                    }
                } else {
                    mIsInit = true;
                    result.setResultCode(0);
                    result.setData(getReader().getViewportBitmap().getBitmap());
                    return result;
                }
            }
        }
    }


    public String getCurrentPageName(final BaseReaderRequest request) {
        return request.getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage(final BaseReaderRequest request) {
        return PagePositionUtils.getPageNumber(getCurrentPageName(request));
    }

    public Reader getReader() {
        return reader;
    }

    public Result close(PlideRequestProcessor processor) throws InterruptedException {

        Result result = new Result();
        result.setResultCode(-999);
        if (reader.getDocument() == null) {
            result.setResultCode(0);
            processor.setPresenter(null);
            return result;
        }
        final CloseRequest closeRequest = new CloseRequest();
        reader.submitRequest(getContext(), closeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    throwable.printStackTrace();
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
        }
        processor.setPresenter(null);
        return result;
    }


    private Context getContext() {
        return mContext;
    }

    public void subscribe() {
    }

    public void unSubscribe() {

    }

    public int getTotalPages() {
        return mTotalPags;
    }

    /**
     * 设置epub格式
     */
    private Result setChangeStyleReqest() throws InterruptedException {
        Result<Integer> result = new Result<Integer>();
        result.setResultCode(-999);
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
                mTotalPags = getReader().getNavigator().getTotalPage();
                if (throwable == null) {
                    synchronized (result) {
                        result.setResultCode(0);
                        result.setData(mTotalPags);
                        result.notify();
                    }
                } else {
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
        }
        return result;
    }

    /**
     * 裁剪
     */
    private Result<Bitmap> cropPage() throws InterruptedException {
        Result<Bitmap> result = new Result<Bitmap>();
        result.setResultCode(-999);
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest((mPage + ""));
        getReader().submitRequest(getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                if (throwable == null) {
                    mIsInit = true;
                    synchronized (result) {
                        result.setResultCode(0);
                        result.notify();
                    }
                } else {
                    throwable.printStackTrace();
                    synchronized (result) {
                        result.setResultCode(-1);
                        result.setErrorMsg(throwable.getMessage());
                        result.notify();
                    }
                }
            }
        });
        synchronized (result) {
            if (result.getResultCode() == -999) {
                result.wait();
            }
            if (result.getResultCode() == 0) {
                result.setData(getReader().getViewportBitmap().getBitmap());
            }
            return result;
        }
    }
}


