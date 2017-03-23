package com.inkscreen.utils;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yougy.ui.activity.R;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;

public class MyImageGetter implements Html.ImageGetter {
    TextView container;
    URI baseUri;
    Activity a;
    int matchParentWidth;
    private View parentView;

    public MyImageGetter(TextView textView) {
        this.container = textView;
        this.matchParentWidth = 0;
    }

    public MyImageGetter(TextView textView, Activity a, int matchParentWidth) {
        this.container = textView;
        this.a = a;
        this.matchParentWidth = matchParentWidth;
    }

    public MyImageGetter(TextView textView, Activity a, int matchParentWidth, View parentView) {
        this.container = textView;
        this.a = a;
        this.matchParentWidth = matchParentWidth;
        this.parentView = parentView;
    }

    public MyImageGetter(TextView textView, String baseUrl) {
        this.container = textView;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public MyImageGetter(TextView textView, String baseUrl, int matchParentWidth) {
        this.container = textView;
        this.matchParentWidth = matchParentWidth;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable, this, container, matchParentWidth, a);

        asyncTask.execute(source);
        if (null != a) {
            urlDrawable.drawable = a.getResources().getDrawable(R.drawable.icon_image_latex);
            if (urlDrawable.drawable != null) {
                urlDrawable.drawable.setBounds(0, 0, urlDrawable.drawable.getIntrinsicWidth(), urlDrawable.drawable.getIntrinsicHeight());
            }

        }

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable;
    }

    /**
     * Static inner {@link AsyncTask} that keeps a {@link WeakReference} to the {@link HtmlHttpImageGetter.UrlDrawable}
     * and {@link HtmlHttpImageGetter}.
     * <p/>
     * This way, if the AsyncTask has a longer life span than the UrlDrawable,
     * we won't leak the UrlDrawable or the HtmlRemoteImageGetter.
     */
    private static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private final WeakReference<UrlDrawable> drawableReference;
        private final WeakReference<MyImageGetter> imageGetterReference;
        private final WeakReference<View> containerReference;
        private String source;
        private int matchParentWidth;
        private float scale;
        private Activity a;


        public ImageGetterAsyncTask(UrlDrawable d, MyImageGetter imageGetter, View container, int matchParentWidth, Activity a) {
            this.drawableReference = new WeakReference<>(d);
            this.imageGetterReference = new WeakReference<>(imageGetter);
            this.containerReference = new WeakReference<>(container);
            this.matchParentWidth = matchParentWidth;
            this.a = a;

        }

        @Override
        protected Drawable doInBackground(String... params) {
            source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result == null) {
                Log.w(HtmlTextView.TAG, "Drawable result is null! (source: " + source + ")");
                return;
            }
            final UrlDrawable urlDrawable = drawableReference.get();
            if (urlDrawable == null) {
                return;
            }

            // set the correct bound according to the result from HTTP call
            if (scale == 1) {
                urlDrawable.setBounds(0, 0, (int) (result.getIntrinsicWidth() * scale), (int) (result.getIntrinsicHeight() * scale));
            } else {
                urlDrawable.setBounds(22, 0, (int) (result.getIntrinsicWidth() * scale), (int) (result.getIntrinsicHeight() * scale));
            }

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = result;

            final MyImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return;
            }
            // redraw the image by invalidating the container
            imageGetter.container.invalidate();
            // re-set text to fix images overlapping text
            imageGetter.container.setText(imageGetter.container.getText());
            imageGetter.container.forceLayout();
            imageGetter.container.invalidate();
            if (imageGetter.parentView != null) {
                imageGetter.parentView.forceLayout();
                imageGetter.parentView.invalidate();
            }
        }

        /**
         * Get the Drawable from URL
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                scale = getScale(drawable);
                if (scale == 1) {
                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * scale), (int) (drawable.getIntrinsicHeight() * scale));
                } else {
                    drawable.setBounds(22, 0, (int) (drawable.getIntrinsicWidth() * scale), (int) (drawable.getIntrinsicHeight() * scale));
                }
                return drawable;
            } catch (Exception e) {
//                return null;
                scale=1;
                if (null != a) {
                    Drawable d = a.getResources().getDrawable(R.drawable.icon_noimage);
                    if (d != null) {
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    }
                    return d;
                }
                return null;
            }
        }

        private float getScale(Drawable drawable) {
            View container = containerReference.get();
            if (matchParentWidth == 0 || container == null || a == null) {
                return 1f;
            }
            DisplayMetrics metric = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(metric);
            float width = metric.widthPixels; // 宽度（PX）
            float maxWidth = container.getWidth();
            float originalDrawableWidth = drawable.getIntrinsicWidth();
            float s = (width - matchParentWidth) / originalDrawableWidth;
            if (s > 1) {
                return 1f;
            }
            return s;
        }

        private InputStream fetch(String urlString) throws IOException {
            URL url;
            final MyImageGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return null;
            }
            if (imageGetter.baseUri != null) {
                url = imageGetter.baseUri.resolve(urlString).toURL();
            } else {
                url = URI.create(urlString).toURL();
            }

            return (InputStream) url.getContent();
        }
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
