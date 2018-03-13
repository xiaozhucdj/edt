package com.yougy.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.shop.activity.ShopBookDetailsActivity;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2016/9/21.
 */
public class ShopBookView extends LinearLayout implements View.OnClickListener {

    private ImageView mBookIv;
    private TextView mBookNameTv;
    private TextView mBookPriceTv;
    private BookInfo mInfo;
    private Context mContext;

    public ShopBookView(Context context) {
        super(context);
        init(context);
    }

    public ShopBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context){
         mContext= context ;
        View.inflate(context, R.layout.recommend_item,this);
        mBookIv = (ImageView) findViewById(R.id.search_result_img);
        mBookNameTv = (TextView) findViewById(R.id.search_result_name);
        mBookPriceTv = (TextView) findViewById(R.id.search_result_price);
        setOnClickListener(this);
    }

    public void updateView(BookInfo info){
        LogUtils.e("ShopBookView","updateView.................");
        mInfo = info;
        mBookIv.setImageResource(R.drawable.cart_book);
        mBookNameTv.setText(info.getBookTitle());
        mBookPriceTv.setText(String.format(YougyApplicationManager.getInstance().getResources().getString(R.string.book_price), info.getBookSalePrice() + ""));
        refreshImg(mBookIv ,info.getBookCoverS());
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ShopBookDetailsActivity.class);
        intent.putExtra(ShopGloble.BOOK_ID , Integer.parseInt(mInfo.getBookId() + ""));
        getContext().startActivity(intent);
        LogUtils.e("ShopBookView","onClick................." + mInfo.getBookTitle());
    }

    private void refreshImg(ImageView view, String url) {
        ImageLoaderManager.getInstance().loadImageContext(mContext,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                128,
                168,
                view);
    }
}
