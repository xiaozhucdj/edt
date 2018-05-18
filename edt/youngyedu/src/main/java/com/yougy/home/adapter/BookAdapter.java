package com.yougy.home.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

import static com.yougy.ui.activity.R.id.img_add_icon;

/**
 * Created by Administrator on 2016/7/14.
 * 书的适配器
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.HolerFragmentBook> {

    private boolean mIsL = true;
    private final Fragment mFragment;
    private Context mContext;
    private List<BookInfo> mInfos;
    private boolean mIsReference;
    private OnItemDeteteListener mOnItemDeleteListener;//声明接口

    public void setReference(boolean isReference) {
        mIsReference = isReference;
    }

    public void setPicL(boolean L) {
        mIsL = L;
    }

    /**
     * 构造函数 初始化数据
     */
    public BookAdapter(Context mContext, List<BookInfo> mInfos, Fragment fragment) {
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

        if (mOnItemDeleteListener != null) {
            holder.mImgBookDeleteL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemDeleteListener.onItemDeteteClickL(position);
                }
            });
            holder.mImgBookDeleteS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemDeleteListener.onItemDeteteClickS(position);
                }
            });


            holder.mImgBookIconL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemDeleteListener.onItemDownClickL(position);
                }
            });

            holder.mImgBookIconS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemDeleteListener.onItemDownClickS(position);
                }
            });

        }

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


    public void setOnItemClickListener(OnItemDeteteListener onItemClickListener) {
        mOnItemDeleteListener = onItemClickListener;
    }

    /**
     * holder
     */
    public class HolerFragmentBook extends RecyclerView.ViewHolder {

        private final ImageView mImgBookIconL;
        private final ImageView mImgBookIconS;
        private final RelativeLayout mRlL;
        private final RelativeLayout mRlS;
        private final ImageView mImgBookSaveL;
        private final ImageView mImgBookSaveS;
        private final TextView mTvBookSaveL;
        private final TextView mTvBookSaveS;
        private final ImageView mImgBookDeleteL;
        private final ImageView mImgBookDeleteS;
        private final ImageView mImgAdd;

        public HolerFragmentBook(View itemView) {
            super(itemView);
            mImgBookIconL = (ImageView) itemView.findViewById(R.id.img_book_iconL);
            mImgBookIconS = (ImageView) itemView.findViewById(R.id.img_book_iconS);
            mRlL = (RelativeLayout) itemView.findViewById(R.id.rl_book_itemL);
            mRlS = (RelativeLayout) itemView.findViewById(R.id.rl_book_itemS);

            mImgBookSaveL = (ImageView) itemView.findViewById(R.id.img_book_save_l);
            mImgBookSaveS = (ImageView) itemView.findViewById(R.id.img_book_save_s);

            mTvBookSaveL = (TextView) itemView.findViewById(R.id.tv_book_save_l);
            mTvBookSaveS = (TextView) itemView.findViewById(R.id.tv_book_save_s);

            mImgBookDeleteL = (ImageView) itemView.findViewById(R.id.img_delete_book_l);
            mImgBookDeleteS = (ImageView) itemView.findViewById(R.id.img_delete_book_s);


            mImgAdd= (ImageView) itemView.findViewById(R.id.img_add_icon) ;

        }


        public void setViewData(int position) {
            if (mIsReference) {
                mImgBookDeleteL.setVisibility(View.VISIBLE);
                mImgBookDeleteS.setVisibility(View.VISIBLE);
            }
            if (mIsL) {
                mRlL.setVisibility(View.VISIBLE);

                if (mInfos.get(position).getBookId() == -1) {
                    mImgBookSaveL.setVisibility(View.GONE);
                    mTvBookSaveL.setVisibility(View.GONE);
                    mImgBookDeleteL.setVisibility(View.GONE);
                    mImgAdd.setVisibility(View.VISIBLE);
                    mImgBookIconL.setImageDrawable(null);
                    return;
                }

                mRlS.setVisibility(View.GONE);
                loadImage(mInfos.get(position).getBookCoverL(), 201, 267, mImgBookIconL);
                if (!StringUtils.isEmpty((FileUtils.getBookFileName(mInfos.get(position).getBookId(), FileUtils.bookDir)))) {
                    mImgBookSaveL.setImageDrawable(UIUtils.getDrawable(R.drawable.img_down_book));
                    mTvBookSaveL.setText("已\n下\n载");
                } else {
                    mImgBookSaveL.setImageDrawable(UIUtils.getDrawable(R.drawable.img_un_down_book));
                    mTvBookSaveL.setText("未\n下\n载");
                }

            } else {
                mRlL.setVisibility(View.GONE);
                mRlS.setVisibility(View.VISIBLE);
                loadImage(mInfos.get(position).getBookCoverS(), 151, 201, mImgBookIconS);
                if (!StringUtils.isEmpty((FileUtils.getBookFileName(mInfos.get(position).getBookId(), FileUtils.bookDir)))) {
                    mImgBookSaveS.setImageDrawable(UIUtils.getDrawable(R.drawable.img_down_book));
                    mTvBookSaveS.setText("已\n下\n载");
                } else {
                    mImgBookSaveS.setImageDrawable(UIUtils.getDrawable(R.drawable.img_un_down_book));
                    mTvBookSaveS.setText("未\n下\n载");
                }
            }
        }

        private void loadImage(String url, int w, int h, ImageView iv) {
            if (mInfos != null && mInfos.size() > 0) {
                ImageLoaderManager.getInstance().loadImageFragment(mFragment,
                        url,
                        R.drawable.img_book_cover,
                        R.drawable.img_book_cover,
                        w,
                        h,
                        iv);
            }
        }
    }

    public interface OnItemDeteteListener {
        void onItemDeteteClickL(int position);

        void onItemDeteteClickS(int position);

        void onItemDownClickL(int position);

        void onItemDownClickS(int position);
    }
}
