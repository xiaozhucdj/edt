package com.onyx.android.sdk.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

import com.onyx.android.sdk.R;

public class BookmarkIconFactory
{
    private static Paint paint = new Paint();
    static Bitmap sBookmarkActivated;
    static Bitmap sBookmarkDeactivated;

    public static void loadAllBitmap(Context context) {
        if (sBookmarkActivated == null) {
            sBookmarkActivated = drawBookmark(true, context);
        }
        if (sBookmarkDeactivated == null){
            sBookmarkDeactivated = drawBookmark(false, context);
        }
    }

    public static Bitmap getBookmarkIcon(boolean activated) {
        return activated ? sBookmarkActivated : sBookmarkDeactivated;
    }

    private static Bitmap drawBookmark(boolean activated, Context context)
    {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setAlpha(320);
        if (activated) {
            paint.setStyle(Style.FILL);
        }
        else {
            paint.setStyle(Style.STROKE);
        }
        int width = (int) context.getResources().getDimension(R.dimen.reader_bookmark_width);
        int height = (int) context.getResources().getDimension(R.dimen.reader_bookmark_height);
        int moveto_x = (int) context.getResources().getDimension(R.dimen.reader_bookmark_moveto_x);
        int moveto_y = (int) context.getResources().getDimension(R.dimen.reader_bookmark_moveto_y);
        int lineto_x = (int) context.getResources().getDimension(R.dimen.reader_bookmark_lineto_x);
        int lineto_y = (int) context.getResources().getDimension(R.dimen.reader_bookmark_lineto_y);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        Path path = new Path();
        if (activated) {
            path.moveTo(moveto_x, moveto_y);
            path.lineTo(lineto_x, lineto_y);
            path.lineTo(moveto_x, lineto_y);
            path.lineTo(moveto_x, moveto_y);
        } else {
            path.moveTo(moveto_x, moveto_y);
            path.lineTo(lineto_x, lineto_y);
            path.lineTo(lineto_x, moveto_y);
            path.lineTo(moveto_x, moveto_y);
        }
        path.close();
        c.drawPath(path, paint);

        return bitmap;
    }
}
