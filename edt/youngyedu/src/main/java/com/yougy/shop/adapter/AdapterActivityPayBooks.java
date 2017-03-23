package com.yougy.shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * <p/>
 * 购买列表
 */
public class AdapterActivityPayBooks extends RecyclerView.Adapter<AdapterActivityPayBooks.HolerPayBooks> {
    private Context mContext;
    private List<BookInfo> mInfos;

    public AdapterActivityPayBooks(Context mContext, List<BookInfo> mInfos) {
        this.mContext = mContext;
        this.mInfos = mInfos;
    }

    @Override
    public HolerPayBooks onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_activity_pay_books, null);
        return new HolerPayBooks(view);
    }

    @Override
    public void onBindViewHolder(HolerPayBooks holder, int position) {
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos!=null && mInfos.size()>0){
            return mInfos.size() ;
        }
        return 0;
    }

    /**holder*/
    public class HolerPayBooks extends RecyclerView.ViewHolder {

        private final ImageView mImgBookIcon;
        private final TextView mTvBookName;
        private final TextView mTvBookPirce;

        public HolerPayBooks(View itemView) {
            super(itemView);

            mTvBookName = (TextView) itemView.findViewById(R.id.tv_BookName);
            mTvBookPirce = (TextView) itemView.findViewById(R.id.tv_BookPirce);
            mImgBookIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                //设置书的名字
                mTvBookName.setText(mInfos.get(position).getBookTitle());
                        mTvBookPirce.setText(mInfos.get(position).getBookSalePrice()+"");
            }
        }
    }
}
