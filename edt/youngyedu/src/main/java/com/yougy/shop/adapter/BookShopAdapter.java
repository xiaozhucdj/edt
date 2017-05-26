package com.yougy.shop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.utils.LogUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.view.ShopBookItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangliang on 2016/9/21.
 */
public class BookShopAdapter extends RecyclerView.Adapter<BookShopHolder> {

    private List<String> mClassifies;
    private List<List<BookInfo>> infos;
    public BookShopAdapter(List<String> classifies, List<List<BookInfo>> infos) {
        LogUtils.e("BookShopAdapter", "classifies' size : " + classifies.size() + "... infos' size : " + infos.size());
        mClassifies = classifies;
        this.infos = infos;
    }

    @Override
    public BookShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BookShopHolder.create(parent, listener);
    }

    @Override
    public void onBindViewHolder(BookShopHolder holder, int position) {
        holder.bindView(mClassifies.get(position), infos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mClassifies == null ? 0 : mClassifies.size();
    }

    private OnMoreClickListener listener;

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    public interface OnMoreClickListener {
        void onMoreClick(int position);
    }

}

class BookShopHolder extends RecyclerView.ViewHolder {

    ShopBookItem mShopBookItem;

    public BookShopHolder(View itemView, BookShopAdapter.OnMoreClickListener listener) {
        super(itemView);
        mShopBookItem = (ShopBookItem) itemView;
        mShopBookItem.setOnMoreClickListener(listener);
    }

    public void bindView(String classify, List<BookInfo> infos, int position) {
        LogUtils.e("BookShopAdapter", "bindView...... classify : " + classify + ",infos' size : " + infos.size());
        List<BookInfo> ifs = new ArrayList<>();
        if (infos.size() > 5) {
            ifs.addAll(infos.subList(0, 5));
        } else {
            ifs.addAll(infos);
        }
        mShopBookItem.updateView(classify, ifs, position);
    }

    public static BookShopHolder create(ViewGroup parent, BookShopAdapter.OnMoreClickListener listener) {
        return new BookShopHolder(new ShopBookItem(parent.getContext()), listener);
    }

}
