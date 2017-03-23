package com.yougy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.zhy.autolayout.utils.AutoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by FH on 2017/2/13.
 */

public class NewShopBookItem extends RelativeLayout implements View.OnClickListener{
    @BindView(R.id.shop_book_item_checkbox)
    ImageButton checkbox;
    @BindView(R.id.shop_book_item_book_img)
    public ImageView bookImgview;
    @BindView(R.id.shop_book_item_book_name_tv)
    public TextView bookNameTv;
    @BindView(R.id.shop_book_item_book_author_tv)
    public TextView bookAuthorTv;
    @BindView(R.id.shop_book_item_book_price_tv)
    public TextView bookPriceTv;
    @BindView(R.id.shop_book_item_btn)
    public Button btn;
    @BindView(R.id.shop_book_item_separator_line)
    public View separatorLine;

    Context mContext;
    BookInfo mBookInfo;
    OnItemActionListener mOnItemActionListener;
    int position = -1;


    @OnClick({R.id.shop_book_item_btn})
    public void onClick(View view){
        if (view instanceof NewShopBookItem){
            if (mOnItemActionListener != null){
                mOnItemActionListener.onItemClick(position);
            }
        }
        else if (view instanceof ImageButton){
            checkbox.setSelected(!checkbox.isSelected());
            if (mOnItemActionListener != null){
                mOnItemActionListener.onCheckedChanged(position , checkbox.isSelected());
            }
        }
        else {
            if (mOnItemActionListener != null){
                mOnItemActionListener.onBtnClick(position);
            }
        }
    }
    public NewShopBookItem(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public NewShopBookItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public NewShopBookItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        setVisibility(GONE);
        View.inflate(context, R.layout.shop_cart_favorite_list_book_item, this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , 245));
        AutoUtils.auto(this);
        ButterKnife.bind(this);
        setOnClickListener(this);
        checkbox.setOnClickListener(this);
    }

    public void setIsLast(boolean isLast) {
        if (isLast) {
            separatorLine.setVisibility(GONE);
        }
        else {
            separatorLine.setVisibility(VISIBLE);
        }
    }

    public void setBookInfo(BookInfo bookInfo){
        mBookInfo = bookInfo;
        if (bookInfo == null){
            setVisibility(GONE);
        }
        else {
            setVisibility(VISIBLE);
            bookNameTv.setText(bookInfo.getBookTitle());
            bookAuthorTv.setText("作者:" + bookInfo.getBookAuthor());
            bookPriceTv.setText("价格:￥" + bookInfo.getBookSalePrice());
            refreshImg(bookImgview , bookInfo.getBookCover());
        }
    }


    private void refreshImg(ImageView view, String url) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (w == 0 || h == 0) {
            //测量控件大小
            view.measure(View.MeasureSpec.makeMeasureSpec(0 , MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0 , MeasureSpec.UNSPECIFIED));
            w  = view.getMeasuredWidth() ;
            h  = view.getMeasuredHeight() ;
            if (w == 0){
                w = view.getLayoutParams() == null ? 0 : view.getLayoutParams().width;
            }
            if (h == 0){
                h = view.getLayoutParams() == null ? 0 : view.getLayoutParams().height;
            }
        }
        ImageLoaderManager.getInstance().loadImageContext(mContext,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
                view);
    }
    public void setPosition(int position){
        this.position = position;
    }

    public void setOnItemActionListener(OnItemActionListener mOnItemActionListener) {
        this.mOnItemActionListener = mOnItemActionListener;
    }

    public interface OnItemActionListener{
        public void onItemClick(int position);
        public void onCheckedChanged(int position , boolean checked);
        public void onBtnClick(int position);
    }

    public void setChecked(boolean checked , boolean callListener){
        if (checkbox.isSelected() != checked){
            checkbox.setSelected(checked);
            if (mOnItemActionListener != null && callListener){
                mOnItemActionListener.onCheckedChanged(position , checkbox.isSelected());
            }
        }
    }

    public boolean isChecked(){
        return checkbox.isSelected();
    }

    public void setBtnText(String str){
        if (str != null){
            btn.setText(str);
        }
    }
}
