package com.yougy.shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/2/13.
 * 图书推荐
 */

public class PromoteBookAdapter extends RecyclerView.Adapter<PromoteBookAdapter.HolerPromoteBook> {

    private final Context mContext;
    private final List<BookInfo> mInfos;
    @BindView(R.id.img_book_icon)
    ImageView imgBookIcon;
    @BindView(R.id.tv_bookTitle)
    TextView tvBookTitle;
    @BindView(R.id.tv_bookPirce)
    TextView tvBookPirce;

    /**
     * 构造函数 初始化数据
     */
    public PromoteBookAdapter(Context mContext, List<BookInfo> mInfos) {
        this.mContext = mContext;
        this.mInfos = mInfos;
    }


    @Override
    public HolerPromoteBook onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_promote_book, null);
        ButterKnife.bind(this, view);
        return new HolerPromoteBook(view);
    }

    @Override
    public void onBindViewHolder(HolerPromoteBook holder, int position) {
        holder.setViewData(position);
    }

    /**
     * 返回个数
     */
    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }

    public class HolerPromoteBook extends RecyclerView.ViewHolder {

        public HolerPromoteBook(View itemView) {
            super(itemView);
        }


        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                refreshImg(imgBookIcon, mInfos.get(position).getBookCoverL());
                tvBookTitle.setText(mInfos.get(position).getBookTitle());
                tvBookPirce.setText("￥"+mInfos.get(position).getBookSalePrice());
            }
        }
    }

    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if (w == 0 || h == 0) {
            //测量控件大小
            int result[] = UIUtils.getViewWidthAndHeight(view);
            w = result[0];
            h = result[1];
        }


        ImageLoaderManager.getInstance().loadImageContext(mContext,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
                view);
    }
}
