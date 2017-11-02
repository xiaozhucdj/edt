package com.yougy.homework;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yougy.ui.activity.R;

/**
 * Created by FH on 2017/10/13.
 */

public class PageableRecyclerView extends LinearLayout {
    RecyclerView recyclerView;
    ViewGroup pageBtnContainer;

    private int maxItemNumInOnePage = 0;
    private Adapter customAdapter;


    private int currentSelectPageIndex = 0;

    public PageableRecyclerView(Context context) {
        this(context, null);
    }

    public PageableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return customAdapter.onCreateViewHolder(parent , viewType);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                position = position + currentSelectPageIndex * maxItemNumInOnePage;
                customAdapter.onBindViewHolder(holder , position);
            }

            @Override
            public int getItemCount() {
                if (customAdapter == null){
                    return 0;
                }
                int temp = customAdapter.getItemCount() - currentSelectPageIndex * maxItemNumInOnePage;
                if (temp > maxItemNumInOnePage){
                    return maxItemNumInOnePage;
                }
                return temp;
            }
        });
        LayoutParams rcvParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0 , 1);
        addView(recyclerView, rcvParam);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        LayoutParams pageBtnContainerParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pageBtnContainer = linearLayout;
        addView(pageBtnContainer, pageBtnContainerParam);
    }

    public <VG extends ViewGroup> PageableRecyclerView setCustomPageBtnContainer(VG container) {
        if (pageBtnContainer != null) {
            removeView(pageBtnContainer);
        }
        pageBtnContainer = container;
        return this;
    }

    public PageableRecyclerView setMaxItemNumInOnePage(int maxItemNumInOnePage) {
        this.maxItemNumInOnePage = maxItemNumInOnePage;
        return this;
    }

    public PageableRecyclerView setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
        return this;
    }

    public PageableRecyclerView setAdapter(Adapter adapter){
        customAdapter = adapter;
        customAdapter.setPageableRecyclerView(this);
        return this;
    }

    public void notifyDataSetChanged() {
        if (customAdapter == null){
            return;
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        refreshPageBar();
    }

    private void refreshPageBar() {
        int needPageNum;
        if (maxItemNumInOnePage == 0){
            needPageNum = 0;
        }
        else {
            needPageNum = customAdapter.getItemCount()%maxItemNumInOnePage== 0
                    ? customAdapter.getItemCount()/maxItemNumInOnePage
                    : customAdapter.getItemCount()/maxItemNumInOnePage + 1;
        }
        if (needPageNum <= 1) {
            pageBtnContainer.removeAllViews();
            return;
        }
        int currentPageNum = pageBtnContainer.getChildCount();
        while (currentPageNum > needPageNum) {
            pageBtnContainer.removeViewAt(currentPageNum - 1);
            currentPageNum--;
        }
        while (currentPageNum < needPageNum) {
            pageBtnContainer.addView(customAdapter.makePageBtn((currentPageNum + 1) + ""));
            currentPageNum++;
        }
        for (int i = 0; i < pageBtnContainer.getChildCount(); i++) {
            if (i == currentSelectPageIndex) {
                pageBtnContainer.getChildAt(i).setSelected(true);
            } else {
                pageBtnContainer.getChildAt(i).setSelected(false);
            }
        }
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        recyclerView.addItemDecoration(decor);
    }

    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        recyclerView.addOnItemTouchListener(listener);
    }

    public void setCurrentPage(int page){
        currentSelectPageIndex = page - 1;
    }

    public RecyclerView getRealRcyView() {
        return recyclerView;
    }

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder> {
        private PageableRecyclerView pageableRecyclerView;

        public Adapter setPageableRecyclerView(PageableRecyclerView pageableRecyclerView) {
            this.pageableRecyclerView = pageableRecyclerView;
            return this;
        }

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);
        public abstract void onBindViewHolder(VH holder, int position);
        public abstract int getItemCount();

        private Button makePageBtn(final String text) {
            Button button = (Button) LayoutInflater.from(pageableRecyclerView.getContext())
                    .inflate(R.layout.item_page_btn , pageableRecyclerView.pageBtnContainer , false);
            button.setText(text);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = Integer.parseInt(text) - 1;
                    pageableRecyclerView.currentSelectPageIndex = index;
                    pageableRecyclerView.notifyDataSetChanged();
                }
            });
            return button;
        }
    }


}
