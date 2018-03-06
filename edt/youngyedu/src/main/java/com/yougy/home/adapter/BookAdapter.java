package com.yougy.home.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 * 书的适配器
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.HolerFragmentBook> {

    private final Fragment mFragment;
    private Context mContext;
    private List<BookInfo> mInfos;

    /**
     * 构造函数 初始化数据
     */
    public BookAdapter(Context mContext, List<BookInfo> mInfos , Fragment fragment) {
        this.mContext = mContext;
        this.mInfos = mInfos;
        this.mFragment = fragment;
        AppCompatImageViewCollection.setAlignView(true);
    }

    /**
     * 创建HOLDER  和创建View
     */
    @Override
    public HolerFragmentBook onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_fragment_book, null);
        return new HolerFragmentBook(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(final HolerFragmentBook holder, int position) {
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

    /**
     * holder
     */
    public  class HolerFragmentBook extends RecyclerView.ViewHolder {

        private final ImageView mImgBookIcon;

        public HolerFragmentBook(View itemView) {
            super(itemView);
            mImgBookIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                ImageLoaderManager.getInstance().loadImageFragment(mFragment,
                        mInfos.get(position).getBookCoverL(),
                        R.drawable.img_book_cover,
                        R.drawable.img_book_cover,
                        200,
                        266,
                        mImgBookIcon);
            }
        }
    }
}
