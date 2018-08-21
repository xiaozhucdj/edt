package com.yougy.home.adapter;

/**
 * Created by Administrator on 2016/11/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.DialogNoteSubjectInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/***
 * 创建笔记的
 */
public class DialogNoteSubjectAdapter extends RecyclerView.Adapter<DialogNoteSubjectAdapter.HolderDialogSubject> {
    private List<DialogNoteSubjectInfo> mInfos;

    public DialogNoteSubjectAdapter(List<DialogNoteSubjectInfo> infos) {
        this.mInfos = infos;
    }

    @Override
    public HolderDialogSubject onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = UIUtils.inflate(R.layout.adaper_dialog_subject);
        return new DialogNoteSubjectAdapter.HolderDialogSubject(view);
    }

    @Override
    public void onBindViewHolder(final HolderDialogSubject holder, int position) {
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }

    public class HolderDialogSubject extends RecyclerView.ViewHolder {
        private final TextView mTvSubject;

        public HolderDialogSubject(View itemView) {
            super(itemView);
            mTvSubject = itemView.findViewById(R.id.tv_subject);
        }

        public void setViewData(int position) {

            if (mInfos != null && mInfos.size() > 0) {
                mTvSubject.setText(mInfos.get(position).getSubject());
                mTvSubject.setSelected(mInfos.get(position).isSelect());
                }
        }
    }
}