package com.onyx.android.sdk.ui.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.sys.OnyxSysCenter;
import com.onyx.android.sdk.data.util.GAdapterUtil;
import com.onyx.android.sdk.ui.ContentItemView;
import com.onyx.android.sdk.ui.ContentView;

import java.util.HashMap;

/**
 * Created by solskjaer49 on 16/1/9 15:19.
 */
public class DialogScreenRefreshConfig extends OnyxAlertDialog {
    public interface onScreenRefreshChangedListener {
        void onRefreshIntervalChanged(int pageTurning);
    }

    private GAdapter mAdapter = null;
    static final int DEFAULT_INTERVAL_COUNT = 5;
    int interval = DEFAULT_INTERVAL_COUNT;

    public void setListener(onScreenRefreshChangedListener listener) {
        this.listener = listener;
    }

    onScreenRefreshChangedListener listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        interval = OnyxSysCenter.getScreenUpdateGCInterval(getActivity(), DEFAULT_INTERVAL_COUNT);
        buildScreenRefreshAdapter();
        setParams(new Params().setTittleString(getString(R.string.screen_refresh))
                .setCustomLayoutResID(R.layout.alert_dialog_screeen_refresh_config)
                .setEnableFunctionPanel(false)
                .setEnablePageIndicator(false)
                .setCustomLayoutHeight((int) (mAdapter.size() * getResources().getDimension(R.dimen.button_minHeight)))
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        final ContentView refreshContentView = (ContentView) customView.findViewById(R.id.screen_refresh_contentView);
                        refreshContentView.setShowPageInfoArea(false);
                        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
                        mapping.put(GAdapterUtil.TAG_TITLE_RESOURCE, R.id.textview_title);
                        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.radio_selected);
                        mapping.put(GAdapterUtil.TAG_DIVIDER_VIEW, R.id.divider);
                        refreshContentView.setupGridLayout(mAdapter.size(),1);
                        refreshContentView.setSubLayoutParameter(R.layout.dialog_screen_refresh_item, mapping);
                        refreshContentView.setAdapter(mAdapter, 0);
                        refreshContentView.setCallback(new ContentView.ContentViewCallback() {
                            @Override
                            public void onItemClick(ContentItemView view) {
                                GObject temp = view.getData();
                                int dataIndex = refreshContentView.getCurrentAdapter().getGObjectIndex(temp);
                                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                                refreshContentView.getCurrentAdapter().setObject(dataIndex, temp);
                                refreshContentView.unCheckOtherViews(dataIndex, true);
                                refreshContentView.updateCurrentPage();
                                if (listener != null) {
                                    listener.onRefreshIntervalChanged(view.getData().getInt(GAdapterUtil.TAG_UNIQUE_ID));
                                }
                                DialogScreenRefreshConfig.this.dismiss();
                            }
                        });
                    }
                })
                .setEnableNegativeButton(false));
        super.onCreate(savedInstanceState);
    }

    public void show(FragmentManager fm) {
        super.show(fm, DialogScreenRefreshConfig.class.getSimpleName());
    }

    private GAdapter buildScreenRefreshAdapter() {
        if (mAdapter == null) {
            mAdapter = new GAdapter();
            mAdapter.addObject(createScreenRefreshItem(R.string.always, 1));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_3_pages, 3));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_5_pages, 5));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_7_pages, 7));
            mAdapter.addObject(createScreenRefreshItem(R.string.every_9_pages, 9));
            mAdapter.addObject(createScreenRefreshItem(R.string.never, Integer.MAX_VALUE));
        }
        return mAdapter;
    }

    private GObject createScreenRefreshItem(int stringResource, int refreshPageInterval) {
        GObject object = GAdapterUtil.createTableItem(stringResource, 0, 0, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, refreshPageInterval);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
        if (refreshPageInterval == interval) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }
}
