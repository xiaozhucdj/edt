package com.yougy.view.controlView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/8/24.
 * 用来显示PDF  解析后的图片
 */
public class ShowImgView extends SurfaceView {
    private Bitmap bitmap;
    public Bitmap CurrentBitmap = null;
   /* private float nDisplayX = 0;
    private float nDisplayY = 0*/;
    private int drawCount = 0;
    public ShowImgView(Context context) {
        super(context);
    }

    public ShowImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap != null)
            canvas.drawBitmap(CurrentBitmap, 0, 0, null);
        if(drawCount>0)
        {
            if(CurrentBitmap!=null && CurrentBitmap.isRecycled())
            {
                CurrentBitmap.recycle();
                CurrentBitmap = null;
            }
        }
        drawCount++;
    }

    public void setImageBitmap(Bitmap b) {
        bitmap = b;
        CurrentBitmap = b;
    }

//    public void setDisplay(float nWidth,float nHeight)
//    {
//        nDisplayX = nWidth;
//        nDisplayY = nHeight;
//    }
}
