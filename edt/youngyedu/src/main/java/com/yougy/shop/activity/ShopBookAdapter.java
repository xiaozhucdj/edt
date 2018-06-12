package com.yougy.shop.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/6/2.
 */

public class ShopBookAdapter extends RecyclerView.Adapter<ShopBookAdapter.BookInfoViewHolder> {
    List<BookInfo> mdata;
    Context mContext;

    public ShopBookAdapter(Context mContext , List<BookInfo> mdata) {
        this.mContext = mContext;
        this.mdata = mdata;
    }

    @Override
    public BookInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookInfoViewHolder holder = new BookInfoViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.order_book_info_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BookInfoViewHolder holder, int position) {
        holder.bookNameTv.setText(mdata.get(position).getBookTitle());
        holder.bookPriceTv.setText("￥" + mdata.get(position).getBookSalePrice());
        refreshImg(holder.bookImg , mdata.get(position).getBookCoverS());
    }


    private void refreshImg(ImageView view, String url) {
/*        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if (w == 0 || h == 0) {
            //测量控件大小
            int result[] = UIUtils.getViewWidthAndHeight(view);
            w = result[0];
            h = result[1];
        }
        if (w == 0 || h == 0){
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getLayoutParams().width , View.MeasureSpec.EXACTLY)
                    , View.MeasureSpec.makeMeasureSpec(view.getLayoutParams().height , View.MeasureSpec.EXACTLY));
            w  = view.getMeasuredWidth() ;
            h  = view.getMeasuredHeight() ;
        }*/
        ImageLoaderManager.getInstance().loadImageContext(mContext,
                url,
                R.drawable.img_book_cover,
                FileContonst.withS,
                FileContonst.heightS,
                view);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class BookInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.book_item_img)
        ImageView bookImg;
        @BindView(R.id.book_name_tv)
        TextView bookNameTv;
        @BindView(R.id.book_price_tv)
        TextView bookPriceTv;

        public BookInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
            AutoUtils.auto(itemView);
        }
    }
}
