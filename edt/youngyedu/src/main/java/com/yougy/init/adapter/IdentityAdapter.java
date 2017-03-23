package com.yougy.init.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.init.bean.UserInfo;
import com.yougy.ui.activity.R;

import java.util.List;


/**
 * Created by jiangliang on 2016/10/17.
 */
public class IdentityAdapter extends RecyclerView.Adapter<IdentityHolder> {
    private static final String TAG = "IdentityAdapter";
    private OnItemClickListener listener;
    private List<UserInfo.User> infos;

    public IdentityAdapter(List<UserInfo.User> infos) {
        this.infos = infos;
    }

    @Override
    public IdentityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return IdentityHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(IdentityHolder holder, final int position) {
        holder.bindView(infos.get(position));
        holder.mIdentityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

class IdentityHolder extends RecyclerView.ViewHolder {
    TextView mNameTv;
    TextView mNumberTv;
    LinearLayout mIdentityLayout;

    public IdentityHolder(View itemView) {
        super(itemView);
        mNameTv = (TextView) itemView.findViewById(R.id.name);
        mNumberTv = (TextView) itemView.findViewById(R.id.number);
        mIdentityLayout = (LinearLayout) itemView.findViewById(R.id.identity_item_layout);
    }

    public void bindView(UserInfo.User info) {
        mNameTv.setText(info.getUserRealName());
        mNumberTv.setText(info.getUserNumber());
        if (info.isBind()) {
            mIdentityLayout.setEnabled(false);
            mNameTv.setTextColor(mNameTv.getResources().getColor(R.color.marks));
            mNumberTv.setTextColor(mNumberTv.getResources().getColor(R.color.marks));
        } else {
            mIdentityLayout.setEnabled(true);
            mNameTv.setTextColor(mNameTv.getResources().getColor(R.color.text_color_black));
            mNumberTv.setTextColor(mNumberTv.getResources().getColor(R.color.text_color_black));
        }
    }

    public static IdentityHolder create(ViewGroup parent) {
        return new IdentityHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.identity_item, parent, false));
    }

}