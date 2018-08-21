package com.yougy.plide;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by FH on 2018/7/10.
 */

public class PlideToPageRequest extends PlideRequest {
    int mToPageIndex;
    protected PlideToPageRequest(ImageView imageView, int toPageIndex , String mUrl , PlideOpenDocumentRequest preRequest) {
        super(imageView, mUrl);
        mToPageIndex = toPageIndex;
        this.mPreRequest = preRequest;
    }

    @Override
    public void run() throws InterruptedException {
        getProcessor().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callListeners(PlideLoadListener.STATUS.TO_PAGE_ING , mUrl , mToPageIndex , -999, null , null);
            }
        });
        Result<Bitmap> result = getProcessor().getPresenter().gotoPage(mToPageIndex);
        if (result.getResultCode() == 0){
            getProcessor().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        saveBitmap2Local(result.data , "/sdcard/teacher/temp/" + System.currentTimeMillis() + ".png");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    Log.v("FHHH" , "bitmap!" + result.data + result.data.isRecycled());
                    mImageView.setImageBitmap(result.data);
                    callListeners(PlideLoadListener.STATUS.TO_PAGE_SUCCESS , mUrl , mToPageIndex , -999, null , null);
                }
            });
        }
        else {
            getProcessor().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callListeners(PlideLoadListener.STATUS.ERROR , mUrl , mToPageIndex , -999 , PlideLoadListener.ERROR_TYPE.TO_PAGE_ERROR , result.getErrorMsg());
                }
            });
            throw new PlideRunTimeException("执行toPage失败");
        }
    }

    @Override
    public void onCancelled() {
        getProcessor().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callListeners(PlideLoadListener.STATUS.ERROR , mUrl , mToPageIndex , -999 , PlideLoadListener.ERROR_TYPE.USER_CANCLE, "用户取消toPage");
            }
        });
    }

    private void saveBitmap2Local(Bitmap mBitmap , String fileUrl) throws IOException {
        File file = new File(fileUrl);
        if (!file.exists()) {
            File parentFile = new File(file.getParent());
            parentFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    @Override
    public String toString() {
        return "PlideToPageRequest@" + Integer.toHexString(hashCode()) + " url=" + mUrl + " toPageIndex=" + mToPageIndex;
    }
}
