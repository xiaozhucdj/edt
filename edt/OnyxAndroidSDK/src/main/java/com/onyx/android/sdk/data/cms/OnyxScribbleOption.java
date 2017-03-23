package com.onyx.android.sdk.data.cms;

import android.graphics.Color;

/**
 * Created by solskjaer49 on 15/9/12 11:03.
 */
public class OnyxScribbleOption {
    public enum BACKGROUND_TYPE {
        EMPTY, GRID, LINE, SCREENSHOT, CUSTOM
    }

    public String documentIndex;
    public float thickness = 1.0f;
    public float baseRenderWidth = 1.0f;
    public float displayRatio = 1.0f;
    public boolean pressureSensitive = false;
    public boolean useQuadForDFB = true;
    public BACKGROUND_TYPE backgroundType = BACKGROUND_TYPE.EMPTY;
    public String customBackResourcePath;
    public int background = Color.WHITE;

    public OnyxScribbleOption() {
    }

    static public OnyxScribbleOption defaultOption() {
        return new OnyxScribbleOption();
    }


}
