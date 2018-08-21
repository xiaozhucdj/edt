package com.yougy.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */

public class AllHomeworkAdapter extends RecyclerView.Adapter<AllHomeworkAdapter.HomeworkHolder> {
    private List<HomeworkBookSummary> mInfos;

    /**
     * 构造函数 初始化数据
     */
    public AllHomeworkAdapter(List<HomeworkBookSummary> mInfos) {
        this.mInfos = mInfos;
    }

    /**
     * 创建HOLDER  和创建View
     */
    @Override
    public HomeworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = UIUtils.inflate(R.layout.adapter_all_fragment_homework);
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


        public HomeworkHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_myTitle);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                mTvName.setText(mInfos.get(position).getHomeworkTitle());
            }
        }
    }
}