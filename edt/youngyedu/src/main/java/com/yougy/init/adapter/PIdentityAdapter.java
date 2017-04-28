package com.yougy.init.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.yougy.init.bean.UserInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by jiangliang on 2017/4/27.
 */

public class PIdentityAdapter  extends PageRecyclerView.PageAdapter<PIdentityHolder>{

    private List<UserInfo.User> infos;

    public PIdentityAdapter(List<UserInfo.User> infos) {
        this.infos = infos;
    }
    @Override
    public int getRowCount() {
        return infos.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return infos.size();
    }

    @Override
    public PIdentityHolder onPageCreateViewHolder(ViewGroup viewGroup, int i) {
        return PIdentityHolder.create(viewGroup);
    }

    @Override
    public void onPageBindViewHolder(PIdentityHolder pIdentityHolder, int i) {
        pIdentityHolder.bindView(infos.get(i));
    }
}

class PIdentityHolder extends RecyclerView.ViewHolder {
    TextView mNameTv;
    TextView mNumberTv;
    LinearLayout mIdentityLayout;

    public PIdentityHolder(View itemView) {
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

    public static PIdentityHolder create(ViewGroup parent) {
        return new PIdentityHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.identity_item, parent, false));
    }

}
