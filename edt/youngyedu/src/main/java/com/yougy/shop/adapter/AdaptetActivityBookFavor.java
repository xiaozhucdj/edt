package com.yougy.shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.init.bean.BookInfo;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.common.activity.BaseActivity;
import com.yougy.shop.activity.NewBookItemDetailsActivity;
import com.yougy.ui.activity.R;
import com.yougy.common.utils.UIUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 * 图书收藏
 */

@Deprecated
public class AdaptetActivityBookFavor extends RecyclerView.Adapter<AdaptetActivityBookFavor.HoleActivityBookFavor> {

    private Context mContext;
    private List<BookInfo> mInfos;

    public AdaptetActivityBookFavor(Context mContext, List<BookInfo> mInfos) {
        this.mContext = mContext;
        this.mInfos = mInfos;
    }

    @Override
    public HoleActivityBookFavor onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adaper_activity_book_favor, null);
        return new HoleActivityBookFavor(view);

    }

    @Override
    public void onBindViewHolder(final HoleActivityBookFavor holder, int position) {

        holder.mRlItemNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             BookInfo info =   mInfos.get(holder.getLayoutPosition()) ;
                //跳转到书详情
                Bundle extras = new Bundle();
                extras.putParcelable("bookInfo", info);
                Intent intent = new Intent(BaseActivity.getCurrentActivity(), NewBookItemDetailsActivity.class);
                intent.putExtras(extras);
                BaseActivity.getCurrentActivity().startActivity(intent);

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

    public class HoleActivityBookFavor extends RecyclerView.ViewHolder {

        private final TextView mTvBookName;
        private final RelativeLayout mRlItemNotes;
        private final ImageView mImgIcon;

        public HoleActivityBookFavor(View itemView) {
            super(itemView);
            mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
            mRlItemNotes = (RelativeLayout) itemView.findViewById(R.id.rl_item_book);
            mImgIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);
        }

        public void setViewData(int position) {

          if (mInfos.size()>0) {
              BookInfo bookInfo = mInfos.get(position);
              mTvBookName.setText(bookInfo.getBookTitle());
              refreshImg(mImgIcon ,bookInfo.getBookCover());
          }
        }
    }

    /***
     * 刷新 书图片
     */
    private void refreshImg(ImageView view, String url) {
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();

        if (w == 0 || h == 0) {
            //测量控件大小
            int result[] = UIUtils.getViewWidthAndHeight(view);
            w = result[0];
            h = result[1];
        }

        System.out.println("w ==="+w);
        System.out.println("h ==="+h);

        ImageLoaderManager.getInstance().loadImageContext(mContext,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                w,
                h,
                view);

    }
}
