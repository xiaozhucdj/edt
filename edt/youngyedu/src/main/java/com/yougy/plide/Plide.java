package com.yougy.plide;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;


/**
 * Created by FH on 2018/1/9.
 */

public class Plide{
    private Context mContext;
    private Plide(Context context) {
        mContext = context;
    }

    public static Plide with(Context context) throws PlideException {
        if (context == null){
            throw new PlideException("Plide---with() error! context 为空!");
        }
        return new Plide(context);
    }

    public LoadRequest load(String url) throws PlideException {
        if (TextUtils.isEmpty(url)){
            throw new PlideException("Plide---load() error! url 为空");
        }
        if (url.startsWith("/")){ //TODO 此处可以用正则更加严格的限定url的格式,今后有空了改
            if (!FileUtils.exists(url)){
                throw new PlideException("Plide---load() error! url : " + url + "指向的本地文件不存在!");
            }
        }
        else if (url.startsWith("http://")){ //TODO 此处可以用正则更加严格的限定url的格式,今后有空了改

        }
        else {
            throw new PlideException("Plide---load() error! url : " + url + " 不合法");
        }
        return new LoadRequest(url , mContext);
    }

    public static void clearCache(ImageView imageView){
        LoadController mLoadController = LoadController.popLoadController(imageView);
        if (mLoadController != null){
            if (!mLoadController.isReaderPresenterAbandoned()){
                LogUtils.e("FH" , "clearPdfCache!!!!");
                mLoadController.getReaderPresenter().close();
                mLoadController.abandonReaderPresenter();
            }
        }
    }

}
