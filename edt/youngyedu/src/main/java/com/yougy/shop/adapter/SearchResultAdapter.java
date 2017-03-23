package com.yougy.shop.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jiangliang on 2016/9/19.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultHolder> {

    private List<BookInfo> mBookInfos;
    public SearchResultAdapter(List<BookInfo> infos) {
        mBookInfos = infos;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SearchResultHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        holder.bindView(mBookInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mBookInfos == null ? 0 : mBookInfos.size();
    }

    public static class SearchResultHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.book_img)
        ImageView mBookImg;
        @BindView(R.id.book_name)
        TextView mBookName;
        @BindView(R.id.book_author)
        TextView mBookAuthor;
        @BindView(R.id.book_intro)
        TextView mBookIntro;

        public SearchResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(BookInfo info) {
            ImageLoaderManager.getInstance().loadImageActivity((Activity) mBookImg.getContext(),info.getBookCover(),R.drawable.img_book_cover,mBookImg);
            mBookName.setText(info.getBookTitle());
            mBookAuthor.setText(info.getBookAuthor());
            mBookIntro.setText(info.getBookSummary());
        }

        public static SearchResultHolder create(ViewGroup parent) {
            return new SearchResultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false));
        }
    }
}



