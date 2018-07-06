package com.yougy.plide;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by FH on 2018/1/12.
 */

public class LoadRequest {
    private String url;
    private LoadListener mLoadListener;
    private Context mContext;
    protected LoadRequest(String url , Context context) {
        this.url = url;
        mContext = context;
    }

    public LoadController into(ImageView imageView,boolean forceBaseWidth) throws PlideException{
        if(imageView == null){
            throw new PlideException("Plide---into() error! into imageview 为空!");
        }
        return LoadController.getLoadController(imageView)
                .doLoadMainLogic(url , mLoadListener , mContext);
    }

    public LoadRequest setLoadListener(LoadListener listener){
        mLoadListener = listener;
        return this;
    }
    public LoadRequest useCache(boolean useCache){
        //TODO 未实现方法
        return this;
    }
}
