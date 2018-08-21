package com.yougy.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.home.bean.NoteInfo;
import com.yougy.ui.activity.R;

import java.util.List;


/**
 * Created by Administrator on 2016/7/28.
 * 笔记适配器
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.HolerFragmentNotes> {

    private Context mContext;
    private List<NoteInfo> mInfos;

    public NotesAdapter(Context context, List<NoteInfo> noteInfos) {
        this.mContext = context;
        this.mInfos = noteInfos;
    }

    @Override
    public HolerFragmentNotes onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_fragment_notes, null);
        return new HolerFragmentNotes(view);
    }

    @Override
    public void onBindViewHolder(final HolerFragmentNotes holder, int position) {
        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos != null) {
            return mInfos.size();
        }
        return 0;
    }

    //holder band
    public class HolerFragmentNotes extends RecyclerView.ViewHolder {


        //        private final TextView mTvNoteName;
//        private final TextView mTvNoteGrade;
        private final ImageView mIvIcon;
        private final ImageView mIvAdd;
        private final TextView mTvMyTitle;

        public HolerFragmentNotes(View itemView) {
            super(itemView);
//            mTvNoteName = (TextView) itemView.findViewById(R.id.tv_noteName);
//            mTvNoteGrade = (TextView) itemView.findViewById(R.id.tv_noteGrade);
            mTvMyTitle = itemView.findViewById(R.id.tv_myTitle);
            mIvIcon = itemView.findViewById(R.id.img_book_icon);
            mIvAdd = itemView.findViewById(R.id.img_add_icon);
        }

//        private void setViewVisibility(boolean visibility) {
//            mTvNoteGrade.setVisibility(visibility ? View.VISIBLE : View.GONE);
//            mTvNoteName.setVisibility(visibility ? View.VISIBLE : View.GONE);
//        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                //设置书的名字
                if (mInfos.get(position).isAddView()) {
//                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_add));
//                    mImgIcon.setBackgroundColor(UIUtils.getColor(R.color.white));
//                    setViewVisibility(false);
                    mIvAdd.setVisibility(View.VISIBLE);
                    mIvIcon.setVisibility(View.GONE);
                    mTvMyTitle.setVisibility(View.GONE);
                } else {
                    mTvMyTitle.setText(mInfos.get(position).getNoteTitle());
                    mTvMyTitle.setVisibility(View.VISIBLE);
                    mIvAdd.setVisibility(View.GONE);
                    mIvIcon.setVisibility(View.VISIBLE);

//                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_note_cover));
//                    mImgIcon.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.img_book_backgroud));
                    //判断是否自己添加的笔记
//                    if (mInfos.get(position).getNoteCreator() == SpUtils.getTeacher().getUserId()) {
//                        mTvMyTitle.setText(mInfos.get(position).getNoteTitle());
//                        setViewVisibility(false);
//                        mTvMyTitle.setVisibility(View.VISIBLE);
//                    } else {
//                        mTvMyTitle.setText(mInfos.get(position).getNoteTitle());
//                        mTvNoteName.setText(mInfos.get(position).getNoteTitle());
//                        mTvNoteGrade.setText(mInfos.get(position).getNoteFitGradeName());
////                        setViewVisibility(true);
//                        mTvMyTitle.setVisibility(View.GONE);
//                    }
                }
            }
        }
    }
}