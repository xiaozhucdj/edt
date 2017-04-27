package com.yougy.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.NoteInfo;
import com.yougy.ui.activity.R;

import java.util.List;

import static com.yougy.ui.activity.R.id.tv_noteGrade;


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

        private final TextView mTvNoteName;
        private final TextView mTvNoteGrade;
        private final RelativeLayout mRlItemNotes;
        private final ImageView mImgIcon;
        private final TextView mTvMyTitle;

        public AllHolerFragmentNotes(View itemView) {
            super(itemView);
            mTvNoteName = (TextView) itemView.findViewById(R.id.tv_noteName);
            mTvNoteGrade = (TextView) itemView.findViewById(tv_noteGrade);
            mTvMyTitle = (TextView) itemView.findViewById(R.id.tv_myTitle);

            mImgIcon = (ImageView) itemView.findViewById(R.id.img_book_icon);
            mRlItemNotes = (RelativeLayout) itemView.findViewById(R.id.rl_item_book);

        }

        private void setViewVisibility(boolean visibility) {
            mTvNoteGrade.setVisibility(visibility ? View.VISIBLE : View.GONE);
            mTvNoteName.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                //设置书的名字
                if (mInfos.get(position).isAddView()) {
                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_add));
                    mImgIcon.setBackgroundColor(UIUtils.getColor(R.color.text_color_white));
                    setViewVisibility(false);
                    mTvMyTitle.setVisibility(View.GONE);
                } else {
                    mImgIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.img_note_cover));
                    mImgIcon.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.img_book_backgroud));
                    //判断是否自己添加的笔记
                    if (mInfos.get(position).getNoteCreator() == Integer.parseInt(SpUtil.getAccountId())) {
                        mTvMyTitle.setText(mInfos.get(position).getNoteTitle());
                        setViewVisibility(false);
                        mTvMyTitle.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoteName.setText(mInfos.get(position).getNoteTitle());
                        mTvNoteGrade.setText(mInfos.get(position).getNoteFitGradeName());
                        setViewVisibility(true);
                        mTvMyTitle.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
