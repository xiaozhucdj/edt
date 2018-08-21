package com.yougy.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.home.bean.NoteInfo;
import com.yougy.ui.activity.R;

import java.util.List;


/**
 * Created by Administrator on 2017/4/27.
 */

public class AllNotesAdapter extends RecyclerView.Adapter<AllNotesAdapter.AllHolerFragmentNotes> {

    private Context mContext;
    private List<NoteInfo> mInfos;

    public AllNotesAdapter(Context context, List<NoteInfo> noteInfos) {
        this.mContext = context;
        this.mInfos = noteInfos;
    }

    @Override
    public AllHolerFragmentNotes onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_all_fragment_notes, null);
        return new AllHolerFragmentNotes(view);
    }

    @Override
    public void onBindViewHolder(final AllHolerFragmentNotes holder, int position) {
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
    public class AllHolerFragmentNotes extends RecyclerView.ViewHolder {

        private final TextView mTvTitle;
//        private final TextView mTvNoteGrade;
//        private final RelativeLayout mRlItemNotes;
//        private final ImageView mImgIcon;
//        private final TextView mTvMyTitle;

        public AllHolerFragmentNotes(View itemView) {
            super(itemView);
            mTvTitle= itemView.findViewById(R.id.tv_myTitle);
//            mTvNoteGrade = (TextView) itemView.findViewById(tv_noteGrade);
//            mTvMyTitle = (TextView) itemView.findViewById(R.id.tv_myTitle);
//
//            mImgIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);
//            mRlItemNotes = (RelativeLayout) itemView.findViewById(R.id.rl_item_book);

        }

//        private void setViewVisibility(boolean visibility) {
//            mTvNoteGrade.setVisibility(visibility ? View.VISIBLE : View.GONE);
//            mTvNoteName.setVisibility(visibility ? View.VISIBLE : View.GONE);
//        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                //设置书的名字
                mTvTitle.setText(mInfos.get(position).getNoteTitle());

            }
        }
    }
}
