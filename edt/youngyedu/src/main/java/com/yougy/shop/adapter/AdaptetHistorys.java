package com.yougy.shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 * 搜索历史 记录
 */
public class AdaptetHistorys extends RecyclerView.Adapter<AdaptetHistorys.HolerHistorys> {
    private Context mContext;
    private List<String> mInfos;

    public AdaptetHistorys(Context mContext, List<String> mInfos) {
        this.mContext = mContext;
        this.mInfos = mInfos;
    }

    @Override
    public HolerHistorys onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.adapter_history, null);
        return new HolerHistorys(view);
    }

    @Override
    public void onBindViewHolder(final HolerHistorys holder, int position) {
        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onClickItemListener(mInfos.get(holder.getLayoutPosition())) ;
            }
        });

        holder.setViewData(position);
    }

    @Override
    public int getItemCount() {
        if (mInfos == null) {
            return 0;
        }

        return mInfos.size();
    }

    public class HolerHistorys extends RecyclerView.ViewHolder {
        private final LinearLayout mLlItem;
        private final TextView mTvHistory;

        public HolerHistorys(View itemView) {
            super(itemView);
            mLlItem = itemView.findViewById(R.id.ll_item);
            mTvHistory = itemView.findViewById(R.id.tv_history);
        }

        public void setViewData(int position) {
            if (mInfos != null && mInfos.size() > 0) {
                mTvHistory.setText(mInfos.get(position));
            }
        }
    }

    private ClickItemListener mListener;

    public void setListener(ClickItemListener listener ) {
        mListener = listener;
    }

    public interface ClickItemListener {
        void onClickItemListener(String str);
    }
}
