package com.yougy.home.svgparser;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.yougy.ui.activity.R;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ImageView imgView = (ImageView)findViewById(R.id.img);

        Drawable drawables[] = new Drawable[] {
//                getSvgDrawable(R.raw.gal_model),
//                getSvgDrawable(R.raw.w5_bottom),
//                getSvgDrawable(R.raw.w5_shoes),
//                getSvgDrawable(R.raw.w5_top),
//                getSvgDrawable(R.raw.w5_outerwear),
                getSvgDrawable(R.raw.w5_accessory),
        };

        LayerDrawable layered = new LayerDrawable(drawables);
        imgView.setImageDrawable(layered);
    }

    private PictureDrawable getSvgDrawable(int resId) {
        SVG s = SVGParser.getSVGFromResource(getResources(), resId);
        PictureDrawable pd = new PictureDrawable(s.getPicture());
        return pd;
    }
}
