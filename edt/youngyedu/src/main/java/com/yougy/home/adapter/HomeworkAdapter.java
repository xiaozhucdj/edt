package com.yougy.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.HomeWorkBean;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkHolder> {
    private List<HomeWorkBean> mInfos;

    /**
     * 构造函数 初始化数据
     */
    public HomeworkAdapter(List<HomeWorkBean> mInfos) {
        this.mInfos = mInfos;
    }

    /**
     * 创建HOLDER  和创建View
     */
    @Override
    public HomeworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = UIUtils.inflate(R.layout.adapter_fragment_homework);
        return new HomeworkHolder(view);
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(final HomeworkHolder holder, int position) {
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
    public class HomeworkHolder extends RecyclerView.ViewHolder {
        private final TextView mTvName;
        private final ImageView mImgIcon;
        private final TextView mTvStatus;


        public HomeworkHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mImgIcon = (ImageView) itemView.findViewById(R.id.img_homework_icon);
            mTvStatus = (TextView) itemView.findViewById(R.id.tv_status);

        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                mImgIcon.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.img_book_backgroud));
                mTvName.setText( mInfos.get(position).getHomeworkFitBookTitle());
                if ( mInfos.get(position).getHomeworkStatus() ==1){
                        //开通
                    mTvStatus.setVisibility(View.GONE);
                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_homework_cover));
                }else{
                        //未开通
                    mTvStatus.setVisibility(View.VISIBLE);
                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_homework_undredge));
                }
            }
        }
    }
}