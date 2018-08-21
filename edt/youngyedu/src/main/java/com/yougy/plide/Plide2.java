package com.yougy.plide;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by FH on 2018/7/7.
 */

public class Plide2 {
    private static Plide2 instance = new Plide2();

    public static Plide2 getInstance() {
        return instance;
    }

    public Plide2() {}

    private HashMap<String , PlideRequestProcessor> processorMap = new HashMap<String , PlideRequestProcessor>();

    private ArrayList<String> hasClearedProcessorList = new ArrayList<String>();

    protected void sendNewRequest(PlideRequest request) {
        synchronized (processorMap) {
            String imageViewHashString = "Imageview" + request.getmImageView().hashCode();
            if (hasClearedProcessorList.contains(imageViewHashString)){
                return;
            }
            PlideRequestProcessor processor = processorMap.get(imageViewHashString);
            if (processor == null) {
                processor = new PlideRequestProcessor(request.getmImageView().getContext());
                processorMap.put(imageViewHashString, processor);
            }
            request.setProcessor(processor);
            processor.push(request);
        }
    }

    public void clearCache(ImageView imageView) {
        synchronized (processorMap) {
            String imageViewHashString = "Imageview" + imageView.hashCode();
            if (!hasClearedProcessorList.contains(imageViewHashString)){
                hasClearedProcessorList.add(imageViewHashString);
            }
            PlideRequestProcessor processor = processorMap.get(imageView);
            if (processor != null) {
                processor.recycle();
                processorMap.remove(imageView);
            }
        }
    }

    public static PlideLoadRequestionBuilder with(Context context) {
        return new PlideLoadRequestionBuilder(context);
    }

    public static PlideLoadRequestionBuilder with(Activity activity) {
        //TODO 自动回收内存未实现
        return new PlideLoadRequestionBuilder(activity.getApplicationContext());
    }

    public static PlideLoadRequestionBuilder with(Fragment fragment) {
        //TODO 自动回收内存未实现
        return new PlideLoadRequestionBuilder(fragment.getActivity().getApplicationContext());
    }


}
