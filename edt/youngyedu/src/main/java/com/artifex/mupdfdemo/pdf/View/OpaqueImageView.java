package com.artifex.mupdfdemo.pdf.View;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/6/29.
 * //不透明的ImgagerView
 * // Make our ImageViews opaque to optimize redraw
 */
public class OpaqueImageView extends ImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    /**
     * 是否不透明
     * @return
     */
    @Override
    public boolean isOpaque() {
        return true;
    }
}
