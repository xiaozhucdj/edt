package com.yougy.shop.adapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.manager.ImageLoaderManager;
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
        LogUtils.e(getClass().getName(),"before.......");
        SearchResult binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_result_item1, parent, false);
        LogUtils.e(getClass().getName(),"after.......");
        SearchResultHolder holder = new SearchResultHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        BookInfo bookInfo = mBookInfos.get(position);
        String infoSummary;
        if (TextUtils.isEmpty(bookInfo.getBookSummary())) {
            infoSummary = "";
        } else {
            infoSummary = Html.fromHtml(bookInfo.getBookSummary()).toString();
        }
        if (!TextUtils.isEmpty(infoSummary) && infoSummary.length() > 65) {
            bookSummary = "简介："+infoSummary.substring(0, 65) + "......";
        } else {
            bookSummary = "简介："+infoSummary;
        }
        if (TextUtils.isEmpty(bookSummary)){
            bookSummary = "暂无简介";
        }
        if (TextUtils.isEmpty(bookInfo.getBookAuthor())){
            bookInfo.setBookAuthor("作者：无名氏。。。");
        }else{
            bookInfo.setBookAuthor("作者："+bookInfo.getBookAuthor());
        }
        ImageLoaderManager.getInstance().loadImageContext(holder.binding.bookImg.getContext(),
                bookInfo.getBookCoverS(),
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                128,
                168,
                holder.binding.bookImg);
        LogUtils.e(getClass().getName(),"book info : " + bookInfo);
        holder.getBinding().setVariable(BR.bookInfo, bookInfo);
        holder.getBinding().setVariable(BR.bookSummary,bookSummary);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mBookInfos.size();
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {
        private SearchResult binding;

        public SearchResultHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(SearchResult binding) {
            this.binding = binding;
        }

        public SearchResult getBinding() {
            return this.binding;
        }
    }

}
