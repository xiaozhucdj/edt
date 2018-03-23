package com.yougy.message.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.yougy.message.BookRecommandAttachment;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityRecentContactBinding;
import com.yougy.ui.activity.databinding.ItemRecentContactListBinding;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;


/**
 * Created by FH on 2017/3/21.
 */

public class RecentContactListActivity extends MessageBaseActivity {

    ActivityRecentContactBinding binding;
    ContactAdapter adapter = new ContactAdapter();
    YXClient.OnThingsChangedListener<Bundle> onTeamInfoChangeListener = new YXClient.OnThingsChangedListener<Bundle>() {
        @Override
        public void onThingChanged(Bundle thing , int type) {
            adapter.notifyDataSetChanged();
        }
    };
    YXClient.OnThingsChangedListener<Bundle> onUserInfoChangeListener = new YXClient.OnThingsChangedListener<Bundle>() {
        @Override
        public void onThingChanged(Bundle thing , int type) {
            adapter.notifyDataSetChanged();
        }
    };
    YXClient.OnThingsChangedListener<List<RecentContact>> onRecentContactListChangeListener = new YXClient.OnThingsChangedListener<List<RecentContact>>() {
        @Override
        public void onThingChanged(List<RecentContact> thing , int type) {
            adapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.bind(setLayoutRes(R.layout.activity_recent_contact));
        initListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YXClient.checkNetAndRefreshLogin(this , null , null);
    }

    public void initListView(){
        binding.currentContactList.setAdapter(adapter);
        binding.currentContactList.setDividerHeight(2);
        binding.currentContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecentContact contact = YXClient.getInstance().getRecentContactList().get(position);
                switch (contact.getSessionType()){
                    case P2P:
                        String userName = YXClient.getInstance().getUserNameByID(contact.getContactId());
                        if (userName == null){
                            userName = contact.getContactId();
                        }
                        openActivity(ChattingActivity.class ,
                                "id" , contact.getContactId() ,
                                "type" , contact.getSessionType().toString() ,
                                "name" , userName);
                        break;
                    case Team:
                        String teamName = YXClient.getInstance().getTeamNameByID(contact.getContactId());
                        if (teamName == null){
                            teamName = contact.getContactId();
                        }
                        openActivity(ChattingActivity.class ,
                                "id" , contact.getContactId() ,
                                "type" , contact.getSessionType().toString() ,
                                "name" , teamName);
                        break;

                }

            }
        });
        YXClient.getInstance().with(this).addOnRecentContactListChangeListener(onRecentContactListChangeListener);
        YXClient.getInstance().with(this).addOnUserInfoChangeListener(onUserInfoChangeListener);
        YXClient.getInstance().with(this).addOnTeamInfoChangeListener(onTeamInfoChangeListener);
    }
    @Override
    public void init() {}
    @Override
    public void loadData() {}

    @Override
    protected void initTitleBar(RelativeLayout titleBarLayout, Button leftBtn, TextView titleTv, Button rightBtn) {
        titleTv.setText("我的消息");
        rightBtn.setBackgroundResource(R.drawable.img_contact_list_icon);
        rightBtn.setText("");
        rightBtn.getLayoutParams().width = 28;
        rightBtn.getLayoutParams().height = 28;
    }

    @Override
    public void onTitleBarRightBtnClick(View view) {
        openActivity(ContactListActivity.class);
    }

    private class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return YXClient.getInstance().getRecentContactList().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(RecentContactListActivity.this).inflate(R.layout.item_recent_contact_list, parent , false);
                AutoUtils.auto(convertView);
                convertView.setTag(DataBindingUtil.bind(convertView));
            }
            RecentContact contact = YXClient.getInstance().getRecentContactList().get(position);
            ItemRecentContactListBinding binding = (ItemRecentContactListBinding) convertView.getTag();
            binding.setContact(contact);
            if (contact.getMsgType() == MsgTypeEnum.custom && contact.getAttachment() != null && contact.getAttachment() instanceof BookRecommandAttachment){
                binding.messageInfoTv.setText("[推荐图书]");
            }
            else {
                binding.messageInfoTv.setText(contact.getContent());
            }
            switch (contact.getSessionType()){
                case P2P:
                    String userName = YXClient.getInstance().getUserNameByID(contact.getContactId());
                    binding.contactNameTv.setText(TextUtils.isEmpty(userName) ? contact.getContactId() : userName);
                    binding.avatarImv.setImageResource(R.drawable.icon_teacher_medium);
                    break;
                case Team:
                    String teamName = YXClient.getInstance().getTeamNameByID(contact.getContactId());
                    binding.contactNameTv.setText(TextUtils.isEmpty(teamName) ? contact.getContactId() : teamName);
                    binding.avatarImv.setImageResource(R.drawable.icon_group_medium);
                    break;
            }
            binding.contactNameTv.setText(binding.contactNameTv.getText() + "    未读" + YXClient.getInstance().getUnreadMsgCount(contact.getContactId() , contact.getSessionType()));
            return convertView;
        }
    }

}
