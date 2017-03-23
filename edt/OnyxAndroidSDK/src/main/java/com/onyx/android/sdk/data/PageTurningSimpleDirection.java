package com.onyx.android.sdk.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by suicheng on 2016/9/29.
 */
public class PageTurningSimpleDirection {
    public static final int NONE = -1;
    public static final int PREV = 0;
    public static final int NEXT = 1;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({NONE, PREV, NEXT})
    // Create an interface for validating int types
    public @interface PageTurningDirectionDef {
    }
}
