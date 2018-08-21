package com.yougy.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.BookCategory;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/1.
 */
public class FitGradeAdapter extends RecyclerView.Adapter<FitGradeAdapter.HolderFitGrade> {
    private List<BookCategory> mInfos;

    public FitGradeAdapter(List<BookCategory> mInfos) {
        this.mInfos = mInfos;
    }

    @Override
    public HolderFitGrade onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = UIUtils.inflate(R.layout.adapter_grade);
        return new FitGradeAdapter.HolderFitGrade(view);
    }

    @Override
    public void onBindViewHolder(final HolderFitGrade holder, int position) {
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }

    public class HolderFitGrade extends RecyclerView.ViewHolder {
        private final TextView mTvGrade;

        public HolderFitGrade(View itemView) {
            super(itemView);
            mTvGrade = itemView.findViewById(R.id.tv_grade);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                mTvGrade.setText(mInfos.get(position).getCategoryName());
                //设置 变化背景
                mTvGrade.setSelected(mInfos.get(position).isSelect());
            }
        }
    }

}
