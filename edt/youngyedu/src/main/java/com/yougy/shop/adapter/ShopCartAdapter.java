package com.yougy.shop.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.utils.LogUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by jiangliang on 2016/9/18.
 */
public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartHolder> {

    private List<BookInfo> mBookInfos;
    private SparseBooleanArray isSelecteds = new SparseBooleanArray();
    private ShopCartHolder holder;
    private OnCheckedChangeLisenter onCheckedChangeListener;
    private OnDeleteListener onDeleteListener;
    private static final String TAG = "ShopCartAdapter";

    public SparseBooleanArray getIsSelecteds() {
        return isSelecteds;
    }

    public ShopCartAdapter(List<BookInfo> bookInfos) {
        mBookInfos = bookInfos;
        for (int i = 0; i < mBookInfos.size(); i++) {
            isSelecteds.put(i, false);
        }
    }

    @Override
    public ShopCartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        holder = ShopCartHolder.create(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShopCartHolder holder, int position) {
        for (int i = 0; i < isSelecteds.size(); i++) {
            LogUtils.e(TAG, "select is : " + isSelecteds.get(i));
        }
        final BookInfo info = mBookInfos.get(position);
        LogUtils.e(TAG, "position is : " + position);
        holder.bindView(info, isSelecteds.get(position));
        final int tmp = holder.getAdapterPosition();
        holder.mSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtils.e(TAG, "onCheckedChanged.............." + tmp);
                isSelecteds.put(tmp, isChecked);
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChange(isChecked ? info.getBookSalePrice() : -info.getBookSalePrice());
                }
            }
        });
        holder.mSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                ShopCart.flag = false;
                LogUtils.e(TAG, "onTouch..............");
                return false;
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e(TAG, "onClick..............." + tmp);
//                isSelecteds.remove(tmp);
                for (int i = tmp; i < isSelecteds.size() - 1; i++) {
//                    isSelecteds.put(i, isSelecteds.get(i + 1));
                    boolean temp = isSelecteds.get(i);
                    isSelecteds.put(i, isSelecteds.get(i + 1));
                    isSelecteds.put(i + 1, temp);
                }
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(tmp);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBookInfos == null ? 0 : mBookInfos.size();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeLisenter onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeLisenter {
        void onCheckedChange(float value);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }
}

class ShopCartHolder extends RecyclerView.ViewHolder {
    CheckBox mSelect;
    ImageView mBookImg;
    TextView mBookName;
    TextView mBookAuthor;
    TextView mBookPrice;
    Button mDelete;

    public ShopCartHolder(View itemView) {
        super(itemView);
        mSelect = (CheckBox) itemView.findViewById(R.id.select);
        mBookImg = (ImageView) itemView.findViewById(R.id.book_img);
        mBookName = (TextView) itemView.findViewById(R.id.book_name);
        mBookAuthor = (TextView) itemView.findViewById(R.id.book_author);
        mBookPrice = (TextView) itemView.findViewById(R.id.book_price);
        mDelete = (Button) itemView.findViewById(R.id.delete);
    }

    public void bindView(BookInfo info, boolean isSelected) {
        mBookImg.setImageResource(R.drawable.cart_book);
        mBookName.setText(info.getBookTitle());
        mBookAuthor.setText(info.getBookAuthor());
        mBookPrice.setText(info.getBookSalePrice() + "");
        mSelect.setChecked(isSelected);
    }

    public static ShopCartHolder create(ViewGroup parent) {
        return new ShopCartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_cart_item, parent, false));
    }

}
