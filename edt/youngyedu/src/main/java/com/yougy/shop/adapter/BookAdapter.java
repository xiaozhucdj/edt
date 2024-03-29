package com.yougy.shop.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jiangliang on 2017/2/9.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {

    private static Context mContext;
    private List<BookInfo> infos;


    public BookAdapter(List<BookInfo> infos, Context context) {
        this.infos = infos;
        mContext = context;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BookHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        holder.bindView(infos.get(position));
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public BookInfo getItemBook(int position) {
        return infos.get(position);
    }

    public static class BookHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_result_img)
        ImageView searchResultImg;
        @BindView(R.id.search_result_name)
        TextView searchResultName;
        @BindView(R.id.search_result_price)
        TextView searchResultPrice;
        @BindView(R.id.search_result_pre_price)
        TextView searchResultPrePrice;

        public BookHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(BookInfo info) {
            LogUtils.e("BookAdapter", "book info : " + info);
            searchResultImg.setImageResource(R.drawable.cart_book);
            searchResultName.setText(info.getBookTitle());
            int schoolType = SpUtils.getStudent().getSchoolLevel();
            if (schoolType > 0) {
                searchResultPrice.setVisibility(View.GONE);
                searchResultPrePrice.setVisibility(View.GONE);
            } else {
                searchResultPrice.setVisibility(View.VISIBLE);
                searchResultPrePrice.setVisibility(View.VISIBLE);
                searchResultPrice.setText(String.format(YoungyApplicationManager.getInstance().getResources().getString(R.string.book_price), String.valueOf(info.getBookSpotPrice())));
                if (info.getBookSpotPrice() < info.getBookSalePrice()) {
                    searchResultPrePrice.setVisibility(View.VISIBLE);
                    searchResultPrePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    searchResultPrePrice.setText(String.format(searchResultPrice.getContext().getResources().getString(R.string.book_price), String.valueOf(info.getBookSalePrice())));
                }
            }
            ImageLoaderManager.getInstance().loadImageContext(mContext,
                    info.getBookCoverS(),
                    R.drawable.img_book_cover,
                    R.drawable.img_book_cover,
                    FileContonst.withS,
                    FileContonst.heightS,
                    searchResultImg);
        }

        public static BookHolder create(ViewGroup parent) {
            return new BookHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item, parent, false));
        }
    }


}
