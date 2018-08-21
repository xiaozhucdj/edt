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
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.HolderSubject> {
    private List<BookCategory> mInfos;

    public SubjectAdapter(List<BookCategory> mInfos) {
        this.mInfos = mInfos;
    }

    @Override
    public HolderSubject onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = UIUtils.inflate(R.layout.adapter_subject);
        return new SubjectAdapter.HolderSubject(view);
    }

    @Override
    public void onBindViewHolder(final HolderSubject holder, int position) {
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }

    public class HolderSubject extends RecyclerView.ViewHolder {
        private final TextView mTvSubject;

        public HolderSubject(View itemView) {
            super(itemView);
            mTvSubject = itemView.findViewById(R.id.tv_subject);
        }

        public void setViewData(int position) {

            if (mInfos != null && mInfos.size() > 0) {
                mTvSubject.setSelected(mInfos.get(position).isSelect());
                mTvSubject.setText(mInfos.get(position).getCategoryName());
            }
        }
    }
}
