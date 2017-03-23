/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import java.util.HashSet;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.ui.OnyxGridView;
import com.onyx.android.sdk.ui.data.MultipleSelectionAdapter;

/**
 * @author Joy
 *
 */
public class DialogBaseMultipleSettings extends DialogBaseOnyx
{
    private Button mButtonPreviousPage = null;
    private Button mButtonNextPage = null;
    private Button mButtonSet = null;
    private Button mButtonCancel = null;
    private TextView mTextViewProgress = null;
    private TextView mTextViewTitle = null;
    private OnyxGridView mGridView = null;
    private MultipleSelectionAdapter mAdapter = null;
    private View mView = null;

    private boolean mMustChooseAtLeastOnItem = true;

    public DialogBaseMultipleSettings(Context context)
    {
        super(context);

        this.setContentView(R.layout.dialog_settings_selection_template);
        mView = findViewById(R.id.layout_dialog_view);

        mButtonPreviousPage = (Button) this.findViewById(R.id.button_previous_dialogpaged);
        mButtonNextPage = (Button) this.findViewById(R.id.button_next_dialogpaged);
        mButtonSet = (Button) this.findViewById(R.id.button_set_dialogpaged);
        mButtonCancel = (Button) this.findViewById(R.id.button_cancel_dialogpaged);
        mTextViewProgress = (TextView) this.findViewById(R.id.textview_paged_dialogpaged);
        mTextViewTitle = (TextView) this.findViewById(R.id.textview_title);
        mGridView = (OnyxGridView) this.findViewById(R.id.gridview_dialogpaged);

        mButtonNextPage.setVisibility(View.GONE);
        mButtonPreviousPage.setVisibility(View.GONE);
        mTextViewProgress.setVisibility(View.GONE);

        mButtonPreviousPage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (mGridView.getPagedAdapter().getPaginator().canPrevPage()) {
                    mGridView.getPagedAdapter().getPaginator().prevPage();
                }
            }
        });

        mButtonNextPage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                int height = mGridView.getHeight();

                if (mGridView.getPagedAdapter().getPaginator().canNextPage()) {
                    mGridView.getPagedAdapter().getPaginator().nextPage();
                }

                if (height != mGridView.getLayoutParams().height) {
                    mGridView.getLayoutParams().height = height;
                }
            }
        });

        mGridView.registerOnAdapterChangedListener(new OnyxGridView.OnAdapterChangedListener()
        {

            @Override
            public void onAdapterChanged()
            {
                mAdapter = (MultipleSelectionAdapter) mGridView.getAdapter();

                mAdapter.registerDataSetObserver(new DataSetObserver()
                {
                    @Override
                    public void onChanged()
                    {
                        DialogBaseMultipleSettings.this.updateTextViewProgress();
                        EpdController.invalidate(mView, UpdateMode.GU);
                    }

                    @Override
                    public void onInvalidated()
                    {
                        DialogBaseMultipleSettings.this.updateTextViewProgress();
                        EpdController.invalidate(mView, UpdateMode.GU);
                    }
                });
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                HashSet<Integer> selections = mAdapter.getSelection();

                int idx = mGridView.getPagedAdapter().getPaginator().getAbsoluteIndex(position);
                Integer i = Integer.valueOf(idx);

                if (selections.contains(i)) {
                    if (selections.size() <= 1 && !mMustChooseAtLeastOnItem) {
                        return;
                    }
                    DialogBaseMultipleSettings.this.removeSelection(idx);
                }
                else {
                    DialogBaseMultipleSettings.this.addSelection(idx);
                }
            }
        });

        DialogBaseMultipleSettings.this.setOnShowListener(new OnShowListener()
        {

            @Override
            public void onShow(DialogInterface dialog)
            {
                int height = mGridView.getHeight();

                if (height != mGridView.getLayoutParams().height) {
                    mGridView.getLayoutParams().height = height;
                }

            }
        });

    }

    public TextView getTextViewTitle()
    {
        return mTextViewTitle;
    }

    public OnyxGridView getGridView()
    {
        return mGridView;
    }

    public Button getButtonSet()
    {
        return mButtonSet;
    }

    public Button getButtonCancel()
    {
        return mButtonCancel;
    }

    private void updateTextViewProgress()
    {
        final int current_page = mGridView.getPagedAdapter().getPaginator().getPageIndex() + 1;
        final int page_count = (mGridView.getPagedAdapter().getPaginator().getPageCount() != 0) ?
                mGridView.getPagedAdapter().getPaginator().getPageCount() : 1;

                mTextViewProgress.setText(String.valueOf(current_page) + this.getContext().getResources().getString(R.string.slash) + String.valueOf(page_count));
        if (page_count >= 2) {
            mButtonNextPage.setVisibility(View.VISIBLE);
            mButtonPreviousPage.setVisibility(View.VISIBLE);
            mTextViewProgress.setVisibility(View.VISIBLE);
        }
    }

    private void addSelection(int position)
    {
        mAdapter.addSelection(position);
        mAdapter.notifyDataSetChanged();
    }
    
    private void removeSelection(int position)
    {
        mAdapter.removeSelection(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        EpdController.invalidate(mView, UpdateMode.GU);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {

            if (mGridView.getPagedAdapter().getPaginator().canNextPage()) {
                mGridView.getPagedAdapter().getPaginator().nextPage();
            }

            return true;
        } else if(keyCode == KeyEvent.KEYCODE_PAGE_UP){
            if (mGridView.getPagedAdapter().getPaginator().canPrevPage()) {
                mGridView.getPagedAdapter().getPaginator().prevPage();
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void setIsCanChooseZeroItem(boolean canChooseZeroItem)
    {
        mMustChooseAtLeastOnItem = canChooseZeroItem;
    }
}
