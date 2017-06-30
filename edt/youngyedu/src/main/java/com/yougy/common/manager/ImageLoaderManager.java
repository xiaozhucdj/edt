package com.yougy.common.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Administrator on 2016/10/14.
 * 网络请求图片
 */
public class ImageLoaderManager {
    private  boolean mdebug = false ;
    private   String mUrl = "http://b173.photo.store.qq.com/psb?/V12282cC2jBWVQ/WsrgLcNGTO*HLWvKaXwfQeloMMGTgMUKqSqiFSBqEN0!/b/dK0AAAAAAAAA&bo=gALAAwAAAAAFB2U!&rf=viewer_4 ";

    private  static  ImageLoaderManager instance ;
    private ImageLoaderManager(){

    }

    public static synchronized   ImageLoaderManager getInstance(){
        if (instance == null){
            instance = new ImageLoaderManager();
        }
        return  instance;
    }

    //////////////////////////////Context/////////////////////////////////////////////

    /**
     *
     * @param context  上下文
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param errorID       ：显示错误图片
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public  void  loadImageContext(Context context , String url, int placeholderID , int errorID , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }

        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .error(errorID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  上下文
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageContext(Context context , String url, int placeholderID  , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  上下文
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param view          ：显示的imgaView
     */
    public   void  loadImageContext(Context context , String url, int placeholderID  , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    //////////////////////////////Activity/////////////////////////////////////////////

    /**
     *
     * @param context  Activity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param errorID       ：显示错误图片
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageActivity(Activity context , String url, int placeholderID , int errorID , int w, int h , ImageView view){

        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .error(errorID)
//                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  Activity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageActivity(Activity context , String url, int placeholderID  , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  Activity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param view          ：显示的imgaView
     */
    public   void  loadImageActivity(Activity context , String url, int placeholderID  , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }


    //////////////////////////////FragmentActivity/////////////////////////////////////////////

    /**
     *
     * @param context  FragmentActivity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param errorID       ：显示错误图片
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragmentActivity(FragmentActivity context , String url, int placeholderID , int errorID , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .error(errorID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  FragmentActivity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragmentActivity(FragmentActivity context , String url, int placeholderID  , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  FragmentActivity
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragmentActivity(FragmentActivity context , String url, int placeholderID  , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }



    //////////////////////////////Fragment/////////////////////////////////////////////

    /**
     *
     * @param context  Fragment
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param errorID       ：显示错误图片
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragment(android.support.v4.app.Fragment context , String url, int placeholderID , int errorID , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .error(errorID)
//                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  Fragment
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param w             ：设置控件 宽
     * @param h             ：设置控件 高
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragment(Fragment context , String url, int placeholderID  , int w, int h , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(w, h)
                .into(view);
    }

    /**
     *
     * @param context  Fragment
     * @param url       请求地址
     * @param placeholderID ：占位
     * @param view          ：显示的imgaView
     */
    public   void  loadImageFragment(Fragment context , String url, int placeholderID  , ImageView view){
        if (mdebug){
            url = mUrl;
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeholderID)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

}
