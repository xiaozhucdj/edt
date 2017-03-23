package com.inkscreen.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by elanking on 2017/1/18.
 */

public class ImageLoadUtil {


    private  static  ImageLoadUtil instance ;
    private ImageLoadUtil(){

    }
    public static synchronized ImageLoadUtil getInstance(){
        if (instance == null){
            instance = new ImageLoadUtil();
        }
        return  instance;
    }
    public   void  loadImageActivity(Activity context , String url,  int errorID ,  ImageView view){

        Glide.with(context)
                .load(url)
                .error(errorID)
//                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }
}
