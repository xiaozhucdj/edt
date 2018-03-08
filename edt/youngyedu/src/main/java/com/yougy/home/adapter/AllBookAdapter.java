package com.yougy.home.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 * 书的适配器
 */
public class AllBookAdapter extends RecyclerView.Adapter<AllBookAdapter.HolerAllFragmentBook> {

    private final Fragment mFragment;
    private Context mContext;
    private List<BookInfo> mInfos;

    /**
     * 构造函数 初始化数据
     */
    public AllBookAdapter(Context mContext, List<BookInfo> mInfos , Fragment fragment) {
        this.mContext = mContext;
        this.mInfos = mInfos;
        this.mFragment = fragment;
    }

    /**
     * 创建HOLDER  和创建View
     */
    @Override
    public HolerAllFragmentBook onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_all_fragment_book, null);
        return new HolerAllFragmentBook(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(final HolerAllFragmentBook holder, int position) {
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
    public class HolerAllFragmentBook extends RecyclerView.ViewHolder {

        private final ImageView mImgBookIcon;

        public HolerAllFragmentBook(View itemView) {
            super(itemView);
            mImgBookIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);

        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
               refreshImg(mImgBookIcon, mInfos.get(position).getBookCoverS());
            }
        }
    }

    private void refreshImg(ImageView view, String url) {
        ImageLoaderManager.getInstance().loadImageFragment(mFragment,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                162,
                212,
                view);
    }
}
