package com.yougy.shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class ShopCartAdapter2 extends RecyclerView.Adapter<ShopCartAdapter2.ShopCartHolder2>   {
    private Context mContext;
    private List<BookInfo> mInfos;

    public ShopCartAdapter2(Context mContext, List<BookInfo> mInfos) {
        this.mContext = mContext;
        this.mInfos = mInfos;
    }

    @Override
    public ShopCartHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.shop_cart_item2, null);
        return new ShopCartHolder2(view);
    }

    @Override
    public void onBindViewHolder(final ShopCartHolder2 holder, int position) {
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除item
                if (mListener != null) {
                    mListener.onItemDeleteListener(holder.getLayoutPosition());
                }
            }
        });
        holder.mSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BookInfo info = mInfos.get(holder.getLayoutPosition());
                //更改item状态
                holder.mSelect.setSelected( !info.isCheck()) ;
                //更改数据
                info.setCheck( !info.isCheck());

                if (mListener != null) {
                    mListener.onItemCheckListener();
                }
            }
        });
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }




    public class ShopCartHolder2 extends RecyclerView.ViewHolder {

        private final ImageButton mSelect;
        private final ImageView mBookImg;
        private final TextView mBookName;
        private final TextView mBookAuthor;
        private final TextView mBookPrice;
        private final TextView mDelete;

        public ShopCartHolder2(View itemView) {
            super(itemView);
            mSelect = (ImageButton) itemView.findViewById(R.id.select);
            mBookImg = (ImageView) itemView.findViewById(R.id.book_img);
            mBookName = (TextView) itemView.findViewById(R.id.book_name);
            mBookAuthor = (TextView) itemView.findViewById(R.id.book_author);
            mBookPrice = (TextView) itemView.findViewById(R.id.book_price);
            mDelete = (TextView) itemView.findViewById(R.id.delete);
        }

        //设置数据
        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                mBookImg.setImageResource(R.drawable.cart_book);
                mBookName.setText(mInfos.get(position).getBookTitle());
                mBookAuthor.setText(mInfos.get(position).getBookAuthor());
//                mBookPrice.setText(mInfos.get(position).getPrice() + "");
                mBookPrice.setText("JiangLiang");
                mSelect.setSelected(mInfos.get(position).isCheck());
            }
        }
    }

    public void setListener(ItemStateListener listener) {
        mListener = listener;
    }

    private ItemStateListener mListener;

    /***
     * 外部接口
     */
    public interface ItemStateListener {
        /**删除item*/
        void onItemDeleteListener(int item);
        /**check 改变item*/
        void onItemCheckListener() ;
    }
}
