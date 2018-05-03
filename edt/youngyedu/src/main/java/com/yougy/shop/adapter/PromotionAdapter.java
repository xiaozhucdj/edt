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
import com.yougy.ui.activity.PromotionResult;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by jiangliang on 2018-3-8.
 */

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.BookHolder> {

    private List<BookInfo> infos;
    private String bookSummary;
    public PromotionAdapter(List<BookInfo> infos) {
        LogUtils.e("SearchResult", "adapter............................");
        this.infos = infos;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.e(getClass().getName(),"before.......");
        PromotionResult binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.promotion_item, parent, false);
        LogUtils.e(getClass().getName(),"after.......");
        BookHolder holder = new BookHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        BookInfo bookInfo = infos.get(position);
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
            bookInfo.setBookAuthor("作者：");
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
        holder.getBinding().setVariable(BR.bookInfo, bookInfo);
        holder.getBinding().setVariable(BR.bookSummary,bookSummary);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }
    public BookInfo getItemBook(int position) {
        return infos.get(position);
    }
    class BookHolder extends RecyclerView.ViewHolder {
        private PromotionResult binding;

        public BookHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(PromotionResult binding) {
            this.binding = binding;
        }

        public PromotionResult getBinding() {
            return this.binding;
        }
    }
}
