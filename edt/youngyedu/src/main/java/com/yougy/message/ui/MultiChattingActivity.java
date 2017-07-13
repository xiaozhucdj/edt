package com.yougy.message.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.GlideCircleTransform;
import com.yougy.message.ListUtil;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityChattingBinding;
import com.yougy.ui.activity.databinding.ItemChattingBinding;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by FH on 2017/3/22.
 */

/**
 * 选择多个发送对象消息群发activity
 */
public class MultiChattingActivity extends MessageBaseActivity {
    ArrayList<IMMessage> showMessageList = new ArrayList<IMMessage>();
    ArrayList<String> idList;
    ArrayList<String> nameList;
    ChattingAdapter adapter = new ChattingAdapter();
    ActivityChattingBinding binding;
    ArrayList<IMMessage> messageListSuccessed = new ArrayList<IMMessage>();
    ArrayList<IMMessage> messageListFailed = new ArrayList<IMMessage>();

    private IMMessage findFakeMessageBaseRealMessage(IMMessage realMessage){
        HashMap<String , Object> ext = (HashMap<String, Object>) realMessage.getLocalExtension();
        if (ext != null){
            String fakeMessageId = (String) ext.get("fake_message_id");
            if (!TextUtils.isEmpty(fakeMessageId)){
                for (IMMessage fakeMessage : showMessageList) {
                    if (fakeMessage.getUuid().equals(fakeMessageId)){
                        return fakeMessage;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.bind(setLayoutRes(R.layout.activity_chatting));
        initChattingListview();
        initInputEdittext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YXClient.checkNetAndRefreshLogin(this , null , null);
    }

    @Override
    public void init() {
        idList = getIntent().getStringArrayListExtra("idList");
        nameList = getIntent().getStringArrayListExtra("nameList");
        if (idList == null || idList.size() == 0 || nameList == null || nameList.size() == 0){
            UIUtils.showToastSafe("获取消息发送对象失败");
            finish();
        }
        //用户资料变更监听器
        YXClient.getInstance().with(this).addOnUserInfoChangeListener(new YXClient.OnThingsChangedListener<Bundle>() {
            @Override
            public void onThingChanged(Bundle thing, int type) {
                adapter.notifyDataSetChanged();
            }
        });
        //消息发送状态监听器
        YXClient.getInstance().with(this).addOnMsgStatusChangedListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(final IMMessage message) {
                Log.v("FH" , "MultiChattingActivity 状态更新  sid : " + message.getSessionId() + " sstype: " + message.getSessionType() + " content : " + message.getContent() + "  status : " + message.getStatus() + " attstatus : " + message.getAttachStatus());
                IMMessage fakeMessage = findFakeMessageBaseRealMessage(message);
                if (fakeMessage == null){
                    return;
                }
                HashMap<String , Object> fakeExt = (HashMap<String, Object>) fakeMessage.getLocalExtension();
                if (message.getStatus() == MsgStatusEnum.success) {
                    if (!ListUtil.conditionalContains(messageListSuccessed
                            , new ListUtil.ConditionJudger<IMMessage>() {
                                @Override
                                public boolean isMatchCondition(IMMessage nodeInList) {
                                    return nodeInList.isTheSame(message);
                                }
                            })){
                        messageListSuccessed.add(message);
                        fakeExt.put("success" , (Integer)fakeExt.get("success") + 1);
                        fakeMessage.setLocalExtension(fakeExt);
                    }
                }
                else if (message.getStatus() == MsgStatusEnum.fail) {
                    if (!ListUtil.conditionalContains(messageListFailed
                            , new ListUtil.ConditionJudger<IMMessage>() {
                                @Override
                                public boolean isMatchCondition(IMMessage nodeInList) {
                                    return nodeInList.isTheSame(message);
                                }
                            })){
                        messageListFailed.add(message);
                        fakeExt.put("fail" , (Integer)fakeExt.get("fail") + 1);
                        fakeMessage.setLocalExtension(fakeExt);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void loadData() {}

    public void initChattingListview(){
        binding.chattingListview.setAdapter(adapter);
        binding.chattingListview.setDividerHeight(0);
        scrollToBottom(100);
        binding.chattingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IMMessage message = showMessageList.get(position);
                if (message.getAttachment() != null){
                    if (message.getDirect() == MsgDirectionEnum.In && message.getAttachStatus() != AttachStatusEnum.transferred ){
                        YXClient.getInstance().downloadAttachment(message , false);
                    }
                    else {
                        FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
                        openFile("file://" + fileAttachment.getPathForSave());
                    }
                }
            }
        });
    }

    public void initInputEdittext(){
        binding.messageEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                send();
                return true;
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.send_btn:
                send();
                break;
            case R.id.message_edittext:
                scrollToBottom(200);
                break;
        }
    }

    private void send(){
        YXClient.checkNetAndRefreshLogin(this, new Runnable() {
            @Override
            public void run() {
                String msg = binding.messageEdittext.getText().toString().trim();
                final IMMessage fakeMessage = MessageBuilder.createTextMessage("" , SessionTypeEnum.None , msg);
                ArrayList<IMMessage> tempMessageList = YXClient.getInstance().sendTextMessage(idList , msg);
                if (tempMessageList != null){
                    for (IMMessage message: tempMessageList) {
                        message.setLocalExtension(new HashMap<String, Object>(){
                            {
                                put("fake_message_id" , fakeMessage.getUuid());
                            }
                        });
                    }
                    fakeMessage.setLocalExtension(
                            new HashMap<String, Object>(){
                                {
                                    put("total" , idList.size());
                                    put("success" , 0);
                                    put("fail" , 0);
                                }
                            });
                    showMessageList.add(fakeMessage);
                    adapter.notifyDataSetChanged();
                    binding.messageEdittext.setText("");
                    scrollToBottom(200);
                }
            }
        } , null);
    }

    public void scrollToBottom(long delay){
        YougyApplicationManager.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.chattingListview.setSelection(adapter.getCount() - 1);
            }
        } , delay);
    }
    @Override
    protected void initTitleBar(RelativeLayout titleBarLayout, Button leftBtn, TextView titleTv, Button rightBtn) {
        rightBtn.setVisibility(View.GONE);
        titleTv.setText("发送给" + nameList.get(0) + "等" + idList.size() + "人");
    }

    private class ChattingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showMessageList.size();
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
            if (convertView == null){
                convertView = LayoutInflater.from(MultiChattingActivity.this).inflate(R.layout.item_chatting , null);
                AutoUtils.auto(convertView);
                convertView.setTag(DataBindingUtil.bind(convertView));
            }
            ItemChattingBinding chattingItembinding = (ItemChattingBinding) convertView.getTag();
            IMMessage imMessage = showMessageList.get(position);
            //是否显示时间
            if (shouldShowTime(imMessage , (position - 1 < 0 ? null : showMessageList.get(position - 1)))){
                chattingItembinding.timeTv.setVisibility(View.VISIBLE);
                chattingItembinding.timeTv.setText(DateUtils.convertTimeMillis2StrRelativeNow(imMessage.getTime() , false));
            }
            else {
                chattingItembinding.timeTv.setVisibility(View.GONE);
            }

            //显示头像
            String myavatarPath = YXClient.getInstance().getUserAvatarByID(imMessage.getFromAccount());
            Glide.with(MultiChattingActivity.this)
                    .load(myavatarPath)
                    .placeholder(R.drawable.icon_wenda)
                    .transform(new GlideCircleTransform(MultiChattingActivity.this))
                    .into(chattingItembinding.rightAvatarImv);

            chattingItembinding.leftAvatarImv.setVisibility(View.GONE);
            chattingItembinding.leftMessageBodyLayout.setVisibility(View.GONE);
            chattingItembinding.leftMessageStatusTv.setVisibility(View.GONE);
            chattingItembinding.rightAvatarImv.setVisibility(View.VISIBLE);
            chattingItembinding.rightMessageBodyLayout.setVisibility(View.VISIBLE);
            chattingItembinding.rightMessageStatusTv.setVisibility(View.VISIBLE);
            HashMap<String , Object> ext = (HashMap<String, Object>) imMessage.getLocalExtension();
            int total = (int) ext.get("total");
            int success = (int) ext.get("success");
            int fail = (int) ext.get("fail");
            if (total == success){
                chattingItembinding.rightMessageStatusTv.setText("全部发送成功");
            }
            else if (fail == total){
                chattingItembinding.rightMessageStatusTv.setText("全部发送失败");
            }
            else if (fail + success == total){
                chattingItembinding.rightMessageStatusTv.setText("发送完毕,有" + fail + "个失败");
            }
            else {
                chattingItembinding.rightMessageStatusTv.setText("正在发送" + success + "个已成功");
            }

            if (imMessage.getMsgType() == MsgTypeEnum.text) {
                chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                chattingItembinding.rightTextTv.setText(imMessage.getContent());
            }
            else {

            }
            return convertView;
        }
    }

    /**
     * 根据与上一条消息的间隔判断是否要显示时间文字
     * @return
     */
    private boolean shouldShowTime(IMMessage thisMessage , IMMessage lastMessage){
        final long TIME_INTERVAL = 1000*60*5;//5min
        if (lastMessage != null && thisMessage.getTime() - lastMessage.getTime() < TIME_INTERVAL){
            return false;
        }
        return true;
    }

    private void openFile(String path){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "application/msword");
        startActivity(intent);
    }
}
