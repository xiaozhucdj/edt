package com.onyx.android.sdk.ui;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.cms.OnyxBookProgress;
import com.onyx.android.sdk.data.util.GAdapterUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/16/14
 * Time: 12:21 PM
 */
public final class ContentItemView extends LinearLayout {
    private GObject dataObject;
    private int layoutResourceId;
    private Map<String, Integer> dataLayoutMapping;
    private ArrayList<Integer> styleLayoutList;
    private double mWeight = 0.0;
    private HyphenCallback hyphenCallback;

    public void setHyphenCallback(HyphenCallback hyphenCallback) {
        this.hyphenCallback = hyphenCallback;
    }

    public abstract static class HyphenCallback {
        public ArrayList<String> keyList;

        protected HyphenCallback(ArrayList<String> keyList) {
            this.keyList = keyList;
        }

        public abstract void onUpdateHyphenTittleView(View view, Object value);
    }

    private ContentItemView(LayoutInflater inflater, GObject data, int layoutResource, Map<String, Integer> mapping, ArrayList<Integer> styleLayoutList) {
        super(inflater.getContext());
        setBackgroundResource(getDefaultBackgroundResource());
        init(inflater, data, layoutResource, mapping, styleLayoutList);
    }

    public static int getDefaultBackgroundResource() {
        return R.drawable.contentview_item_background;
    }

    public static ContentItemView create(LayoutInflater inflater, GObject data, ArrayList<Integer> styleLayoutList) {
        return new ContentItemView(inflater, data, getLayoutResourceId(data, -1), getLayoutMapping(data, null), styleLayoutList);
    }

    public static ContentItemView create(LayoutInflater inflater, GObject data, int layoutResource, Map<String, Integer> mapping, ArrayList<Integer> styleLayoutList) {
        int layoutOverride = getLayoutResourceId(data, layoutResource);
        Map<String, Integer> mappingOverride = getLayoutMapping(data, mapping);
        return new ContentItemView(inflater, data, layoutOverride, mappingOverride, styleLayoutList);
    }

    /**
     * Return layout resource id. prefer resource id defined in object.
     *
     * @param object
     * @param fallback
     * @return
     */
    static private int getLayoutResourceId(GObject object, int fallback) {
        int layoutOverride = object.getInt(GAdapterUtil.TAG_LAYOUT_RESOURCE, -1);
        if (layoutOverride > 0) {
            return layoutOverride;
        }
        return fallback;
    }

    /**
     * Return layout mapping, prefer mapping defined in object. Search order
     * Object mapping -> mapping specified by user -> default global mapping.
     *
     * @param object
     * @param fallback
     * @return
     */
    static private Map<String, Integer> getLayoutMapping(GObject object, Map<String, Integer> fallback) {
        Object value = object.getObject(GAdapterUtil.TAG_LAYOUT_MAPPING);
        if (value != null && value instanceof Map) {
            return (Map<String, Integer>) (value);
        }
        return fallback;
    }

    private void inflateView(LayoutInflater inflater, int resourceId) {
        if (resourceId <= 0) {
            return;
        }
        View view = inflater.inflate(resourceId, this, false);
        ((LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER;
        addView(view);
        mWeight = ((LayoutParams) view.getLayoutParams()).weight;
    }

    private void init(LayoutInflater inflater, GObject data, int resId, Map<String, Integer> mapping, ArrayList<Integer> styleList) {
        dataObject = data;
        layoutResourceId = getLayoutResourceId(dataObject, resId);
        dataLayoutMapping = getLayoutMapping(dataObject, mapping);
        styleLayoutList = styleList;
        removeAllViews();
        inflateView(inflater, layoutResourceId);
        dataObject.setCallback(new GObject.GObjectCallback() {
            @Override
            public void changed(String key, GObject object) {
                ContentItemView.this.update();
            }
        });
        update();
    }

    public GObject getData() {
        return dataObject;
    }

    // use current resource id and mapping if not found.
    public void setData(final GObject data) {
        dataObject = data;
        layoutResourceId = getLayoutResourceId(dataObject, layoutResourceId);
        dataLayoutMapping = getLayoutMapping(dataObject, dataLayoutMapping);
        update();
    }

    public double getWeight() {
        return mWeight;
    }

    public int getLayoutWidth() {
        if (getChildCount() <= 0) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return this.getChildAt(0).getLayoutParams().width;
    }

    public int getLayoutHeight() {
        if (getChildCount() <= 0) {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return getChildAt(0).getLayoutParams().height;
    }

    private Map<String, Integer> getDataLayoutMapping() {
        if (dataLayoutMapping != null) {
            return dataLayoutMapping;
        }
        return GAdapterUtil.getDefaultMenuMaping();
    }

    private int intValue(Object object) {
        if (object == null) {
            return -1;
        }
        if (!(object instanceof Integer)) {
            return 0;
        }
        return (Integer) object;
    }

    public void update() {
        if (getChildCount() <= 0) {
            return;
        }
        View parent = getChildAt(0);
        if (getData().isDummyObject()) {
            parent.setVisibility(View.INVISIBLE);
            return;
        }
        parent.setVisibility(VISIBLE);
        updateStyleLayout(parent);
        updateByDataLayoutMapping(parent);
    }

    private void updateStyleLayout(View parent) {
        ArrayList<Integer> list = getStyleLayoutList();
        if (list == null) {
            return;
        }
        for (Integer viewId : list) {
            View view = parent.findViewById(viewId);
            setVisibility(view, getData().isDummyObject() ? GONE : VISIBLE);
        }
    }

    private void updateByDataLayoutMapping(View parent) {
        // mapping specifies relation between tag and view
        Map<String, Integer> mapping = getDataLayoutMapping();
        if (mapping == null) {
            return;
        }

        for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
            final String key = entry.getKey();
            int viewId = entry.getValue();
            View view = parent.findViewById(viewId);
            if (view == null) {
                continue;
            }

            if (!getData().hasKey(key)) {
                if (isSelectableKey(key)) {
                    view.setVisibility(INVISIBLE);
                    continue;
                }
            }

            if (isNewTagKey(key) && !GAdapterUtil.isSubLibrary(getData())) {
                view.setVisibility(GAdapterUtil.hasNewBookTag(getData()) ? VISIBLE : GONE);
                continue;
            }

            if (isDownloadFinishTagKey(key)) {
                view.setVisibility(GAdapterUtil.hasDownloadFinishTag(getData()) ? VISIBLE : GONE);
                continue;
            }

            if (isDecorationTagKey(key)) {
                view.setVisibility(GAdapterUtil.hasDecorationTag(getData()) ? VISIBLE : GONE);
                continue;
            }

            Object value = getData().getObject(key);
            /**
             * Todo: If value ==null will hide the view which design to display the data.
             * some time will cause incorrect layout.so if layout need the view set to invincible
             * instead of gone,should store some empty value but not a null.
             */
            if (value == null) {
                view.setVisibility(View.GONE);
                continue;
            }

            if (isDividerKey(key)) {
                updateDividerStatus(view, (Boolean) value);
                continue;
            }

            if (isSelectableKey(key)) {
                updateSelectStatus(view, (Boolean) value);
                continue;
            }

            if (hyphenCallback != null && hyphenCallback.keyList.contains(key)) {
                view.setVisibility(VISIBLE);
                hyphenCallback.onUpdateHyphenTittleView(view, value);
                hyphenCallback.onUpdateHyphenTittleView(view, getData());
                continue;
            }
            // TODO: list all possible tag, should use tag type instead of view type.
            if (view instanceof Button) {
                updateButtonText((Button) view, value);
            } else if (view instanceof ImageView) {
                updateImageView((ImageView) view, value);
            } else if (view instanceof EllipsizingTextView) {
                updateEllipsizingTextView((EllipsizingTextView) view, value);
            } else if (view instanceof TextView) {
                updateStandardTextView((TextView) view, value);
            } else if (view instanceof ProgressBar) {
                updateProgress((ProgressBar) view, value);
            } else if (view instanceof DottedProgressBar) {
                updateDotProgress((DottedProgressBar) view, value);
            }
        }
        updateSelection(parent);
    }

    private void updateSelectStatus(View view, boolean value) {
        view.setVisibility(VISIBLE);
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(value);
        }
    }

    private boolean isSelectableKey(String key) {
        return key.equalsIgnoreCase(GAdapterUtil.TAG_SELECTABLE);
    }

    private boolean isDividerKey(String key) {
        return key.equalsIgnoreCase(GAdapterUtil.TAG_DIVIDER_VIEW);
    }

    private void updateDividerStatus(View view, boolean value) {
        view.setVisibility(VISIBLE);
        if (!value) {
            view.setVisibility(INVISIBLE);
        }
    }

    private boolean isNewTagKey(String key) {
        return key.equalsIgnoreCase(GAdapterUtil.TAG_NEW_WITHOUT_READING);
    }

    private boolean isDownloadFinishTagKey(String key) {
        return key.equalsIgnoreCase(GAdapterUtil.TAG_DOWNLOAD_FINISH);
    }

    private boolean isDecorationTagKey(String key){
        return key.equalsIgnoreCase(GAdapterUtil.TAG_DECORATION);
    }

    private void updateProgress(ProgressBar progressBar, Object value) {
        if (value == null) {
            progressBar.setVisibility(GONE);
            return;
        }
        progressBar.setVisibility(VISIBLE);
        if (value instanceof OnyxBookProgress) {
            double current = ((OnyxBookProgress) value).getCurrent();
            double total = ((OnyxBookProgress) value).getTotal();
            if (progressBar.getMax() <= 0) {
                progressBar.setMax(100);
            }
            //Todo:Use the progress bar max value instead of hard code to multiple 100.
            double progress = progressBar.getMax() * (current / total);
            progressBar.setProgress((int) progress);
        } else if (value instanceof String) {
            progressBar.setProgress(Integer.parseInt((String) value));
        } else if (value instanceof Integer) {
            progressBar.setProgress((Integer) value);
        }
    }

    private void updateDotProgress(DottedProgressBar progressBar, Object value) {
        if (value == null) {
            progressBar.setVisibility(GONE);
            return;
        }
        progressBar.setVisibility(VISIBLE);
        if (value instanceof OnyxBookProgress) {
            double current = ((OnyxBookProgress) value).getCurrent();
            double total = ((OnyxBookProgress) value).getTotal();
            if (progressBar.getMax() <= 0) {
                progressBar.setMax(100f);
            }
            //Todo:Use the progress bar max value instead of hard code to multiple 100.
            double progress = progressBar.getMax() * (current / total);
            progressBar.setProgress((int) progress);
        } else if (value instanceof String) {
            progressBar.setProgress(Integer.parseInt((String) value));
        } else if (value instanceof Integer) {
            progressBar.setProgress((Integer) value);
        }
    }

    private void updateEllipsizingTextView(EllipsizingTextView textView, Object value) {
        if (value == null) {
            textView.setVisibility(GONE);
            return;
        }
        textView.setVisibility(VISIBLE);
        if (value instanceof String) {
            textView.setText((String) value, TextView.BufferType.NORMAL);
        } else if (value instanceof Integer) {
            textView.setText(getContext().getResources().getString((Integer) value), TextView.BufferType.NORMAL);
        }
    }

    private void updateStandardTextView(TextView textView, Object value) {
        if (value == null) {
            textView.setVisibility(GONE);
            return;
        }
        textView.setVisibility(VISIBLE);
        if (value instanceof String) {
            textView.setText((String) value);
        } else if (value instanceof Integer) {
            textView.setText((Integer) value);
        } else if (value instanceof Typeface) {
            textView.setTypeface((Typeface) value);
        } else if (value instanceof SpannableString) {
            textView.setText((SpannableString) value, TextView.BufferType.SPANNABLE);
        }
    }

    private void updateImageView(ImageView imageView, Object value) {
        if (value == null) {
            imageView.setImageResource(android.R.color.transparent);
            return;
        }
        imageView.setVisibility(VISIBLE);
        int n = intValue(value);
        if (n > 0) {
            imageView.setImageResource(n);
        } else if (value instanceof Drawable) {
            imageView.setImageDrawable((Drawable) value);
        } else if (value instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) value;
            if (!bitmap.isRecycled()) {
                imageView.setImageBitmap(bitmap);
            }
        } else if (n == 0) {
            imageView.setImageResource(android.R.color.transparent);
        }
    }

    private void updateButtonText(Button button, Object value) {
        if (value == null) {
            return;
        }
        button.setVisibility(VISIBLE);
        int n = intValue(value);
        if (n > 0) {
            button.setText(n);
        } else if (value instanceof String) {
            button.setText((String) value);
        }
    }

    private void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void exclusiveCheck(View checkedView, View uncheckedView, boolean check) {
        setVisibility(checkedView, check ? View.VISIBLE : View.INVISIBLE);
        setVisibility(uncheckedView, check ? View.INVISIBLE : View.VISIBLE);

        if (checkedView instanceof CheckBox && check) {
            ((CheckBox) checkedView).setChecked(true);
        }
        if (uncheckedView instanceof CheckBox && !check) {
            ((CheckBox) uncheckedView).setChecked(false);
        }
    }

    private void updateSelection(View view) {
        View checkedView = null;
        View uncheckedView = null;
        int checkedViewId = dataObject.getInt(GAdapterUtil.TAG_SELECTION_CHECKED_VIEW_ID, -1);
        if (checkedViewId > 0) {
            checkedView = view.findViewById(checkedViewId);
        }
        int uncheckedViewId = dataObject.getInt(GAdapterUtil.TAG_SELECTION_UNCHECKED_VIEW_ID, -1);
        if (uncheckedViewId > 0) {
            uncheckedView = view.findViewById(uncheckedViewId);
        }
        if (checkedView == null && uncheckedView == null) {
            return;
        }

        boolean inSelectionState = dataObject.getBoolean(GAdapterUtil.TAG_IN_SELECTION, false);
        if (!inSelectionState) {
            setVisibility(checkedView, View.INVISIBLE);
            setVisibility(uncheckedView, View.INVISIBLE);
            return;
        }

        boolean selected = dataObject.getBoolean(GAdapterUtil.TAG_SELECTED, false);
        exclusiveCheck(checkedView, uncheckedView, selected);
    }

    public ArrayList<Integer> getStyleLayoutList() {
        return styleLayoutList;
    }

    public void setThumbnailScaleType(String key, ImageView.ScaleType type) throws NullPointerException {
        ImageView view = (ImageView) getChildAt(0).findViewById(getDataLayoutMapping().get(key));
        view.setScaleType(type);
    }

    public void setImageViewBackGround(String key, int backgroundResID) throws NullPointerException {
        ImageView view = (ImageView) getChildAt(0).findViewById(getDataLayoutMapping().get(key));
        view.setBackgroundResource(backgroundResID);
    }

    public void setItemViewBackGround(int backgroundResID) {
        View itemView = getChildAt(0);
        itemView.setBackgroundResource(backgroundResID);
        setBackgroundResource(backgroundResID);
    }
}