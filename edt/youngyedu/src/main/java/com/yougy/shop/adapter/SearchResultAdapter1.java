package com.yougy.shop.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.utils.LogUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.BR;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.SearchResult;

import java.util.List;

/**
 * Created by jiangliang on 2017/3/2.
 */

public class SearchResultAdapter1 extends RecyclerView.Adapter<SearchResultAdapter1.SearchResultHolder> {

    private List<BookInfo> mBookInfos;
    private String bookSummary;

    public SearchResultAdapter1(List<BookInfo> infos) {
        LogUtils.e("SearchResult", "adapter............................");
        mBookInfos = infos;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchResult binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_result_item1, parent, false);
        SearchResultHolder holder = new SearchResultHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        String infoSummary = mBookInfos.get(position).getBookSummary();
        if (!TextUtils.isEmpty(infoSummary) && infoSummary.length() > 65) {
            bookSummary = infoSummary.substring(0, 65) + "......";
        } else {
            bookSummary = infoSummary;
        }
        holder.getBinding().setVariable(BR.bookInfo, mBookInfos.get(position));
        holder.getBinding().setVariable(BR.bookSummary,bookSummary);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mBookInfos.size();
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public SearchResultHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewDataBinding getBinding() {
            return this.binding;
        }
    }

}
