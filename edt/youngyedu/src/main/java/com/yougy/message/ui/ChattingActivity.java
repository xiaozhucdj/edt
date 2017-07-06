package com.yougy.message.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.BookRecommandAttachment;
import com.yougy.message.GlideCircleTransform;
import com.yougy.message.SizeUtil;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityChattingBinding;
import com.yougy.ui.activity.databinding.ItemChattingBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/3/22.
 */

public class ChattingActivity extends MessageBaseActivity {
    ArrayList<IMMessage> messageList = new ArrayList<IMMessage>();
    String id;
    String type;
    String name;
    ArrayList<String> idList;
    ArrayList<String> nameList;
    ChattingAdapter adapter = new ChattingAdapter();
    ActivityChattingBinding binding;
    //用户资料变更监听器
    YXClient.OnThingsChangedListener<Bundle> onUserInfoChangeListener = new YXClient.OnThingsChangedListener<Bundle>() {
        @Override
        public void onThingChanged(Bundle thing) {
            adapter.notifyDataSetChanged();
        }
    };
    //新到消息监听器
    Observer<List<IMMessage>> incommingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            for (IMMessage newMessage : imMessages) {
                Log.v("FH", "接收到新消息" + newMessage + " ssid " + newMessage.getSessionId() + " sstype : " + newMessage.getSessionType());
                if (isMyMessage(newMessage)
                        && (newMessage.getMsgType() == MsgTypeEnum.text || newMessage.getMsgType() == MsgTypeEnum.file
                            || newMessage.getMsgType() == MsgTypeEnum.custom)) {
                    messageList.add(newMessage);
                    if (newMessage.getAttachment() != null && newMessage.getAttachment() instanceof FileAttachment) {
                        NIMClient.getService(MsgService.class).downloadAttachment(newMessage, false);
                    }
                }
            }
            scrollToBottom(100);
            adapter.notifyDataSetChanged();
        }
    };
    //消息发送状态监听器
    Observer<IMMessage> msgSendStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage newMessage) {
            Log.v("FH" , "message 状态更新  sid : " + newMessage.getSessionId() + " sstype: " + newMessage.getSessionType() + " content : " + newMessage.getContent() + "  status : " + newMessage.getStatus() + " attstatus : " + newMessage.getAttachStatus());
            if (isMyMessage(newMessage)){
                adapter.notifyDataSetChanged();
            }
        }
    };

    private boolean isMyMessage(IMMessage message){
        if (message.getSessionType().toString().equals(type) && message.getSessionId().equals(id)) {
            Log.v("FH", message.toString() + "是我的消息");
            return true;
        }
        Log.v("FH", message.toString() + "不是我的消息");
        return false;
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
        if (!NetUtils.isNetConnected()) {
            new ConfirmDialog(getThisActivity(), "当前的wifi没有打开,无法接收新的消息,是否打开wifi?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
                    startActivity(intent);
                }
            }, "打开").show();
        }
        else {
            YXClient.getInstance().getTokenAndLogin(SpUtil.justForTest(), new RequestCallbackWrapper() {
                @Override
                public void onResult(int code, Object result, Throwable exception) {
                    if (code == ResponseCode.RES_SUCCESS){
                        Log.v("FH" , "刷新式登录成功");
                    }
                    else {
                        new ConfirmDialog(getThisActivity(), "已经与消息服务器断开连接(" + code + "),设备无法访问网络,请确保您的网络通畅", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
                                startActivity(intent);
                            }
                        } , "设置网络").show();
                        Log.v("FH" , "刷新式登录失败 code :　" + code);
                    }
                }
            });
        }
    }

    @Override
    public void init() {
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type) || TextUtils.isEmpty(name)){
            UIUtils.showToastSafe("获取消息发送对象失败");
            finish();
        }
    }

    @Override
    public void loadData() {
        IMMessage anchor = null;
        if (type.equals(SessionTypeEnum.Team.toString())){
            anchor = MessageBuilder.createEmptyMessage(id , SessionTypeEnum.Team , System.currentTimeMillis() + 3600000);
        }
        else if (type.equals(SessionTypeEnum.P2P.toString())){
            anchor = MessageBuilder.createEmptyMessage(id , SessionTypeEnum.P2P , System.currentTimeMillis() + 3600000);
        }
        Log.v("FH", "开始查询历史消息,锚点 :" + (anchor == null ? anchor : anchor.getTime()));
        //查询历史消息
        NIMClient.getService(MsgService.class).queryMessageListEx(anchor, QueryDirectionEnum.QUERY_OLD, 9999, true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            Log.v("FH", "获取历史消息成功" + result.size() + "条");
                            UIUtils.showToastSafe("获取历史消息成功 " + result.size());
                            for (IMMessage message : result) {
                                if (message.getMsgType() == MsgTypeEnum.text || message.getMsgType() == MsgTypeEnum.file
                                        || message.getMsgType() == MsgTypeEnum.custom) {
                                    messageList.add(message);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.v("FH", "获取历史消息失败 : " + code + "  " + exception);
                            UIUtils.showToastSafe("获取历史消息失败 : " + code + "  " + exception);
                        }
                    }
                });
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incommingMessageObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(msgSendStatusObserver, true);
    }



    public void initChattingListview(){
        YXClient.getInstance().with(this).addOnUserInfoChangeListener(onUserInfoChangeListener);
        binding.chattingListview.setAdapter(adapter);
        binding.chattingListview.setDividerHeight(0);
        scrollToBottom(100);
        binding.chattingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IMMessage message = messageList.get(position);
                if (message.getAttachment() != null && message.getMsgType() != MsgTypeEnum.custom){
                    if (message.getAttachStatus() != AttachStatusEnum.transferred){
                        NIMClient.getService(MsgService.class).downloadAttachment(message , false);
                    }
                    else {
                        FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
                        openFile("file://" + fileAttachment.getPathForSave() , fileAttachment.getDisplayName());
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
                scrollToBottom(250);
                break;
        }
    }


    private void send(){
        IMMessage message = YXClient.getInstance().sendTextMessage(id , SessionTypeEnum.valueOf(type) , binding.messageEdittext.getText().toString());
        messageList.add(message);
        binding.messageEdittext.setText("");
        adapter.notifyDataSetChanged();
        scrollToBottom(100);
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
        if (type.equals("multip2p")){
            titleTv.setText("发送给" + nameList.get(0) + "等" + idList.size() + "人");
        }
        else {
            titleTv.setText(id + "  " + name);
        }
    }

    private class ChattingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messageList.size();
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
                convertView = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.item_chatting , null);
                AutoUtils.auto(convertView);
                convertView.setTag(DataBindingUtil.bind(convertView));
            }
            ItemChattingBinding chattingItembinding = (ItemChattingBinding) convertView.getTag();
            final IMMessage imMessage = messageList.get(position);
            //是否显示时间
            if (shouldShowTime(imMessage , (position - 1 < 0 ? null : messageList.get(position - 1)))){
                chattingItembinding.timeTv.setVisibility(View.VISIBLE);
                chattingItembinding.timeTv.setText(DateUtils.convertTimeMillis2StrRelativeNow(imMessage.getTime() , false));
            }
            else {
                chattingItembinding.timeTv.setVisibility(View.GONE);
            }

            if (imMessage.getDirect() == MsgDirectionEnum.Out){
                chattingItembinding.leftAvatarImv.setVisibility(View.GONE);
                chattingItembinding.leftMessageBodyLayout.setVisibility(View.GONE);
                chattingItembinding.leftMessageStatusTv.setVisibility(View.GONE);
                chattingItembinding.rightAvatarImv.setVisibility(View.VISIBLE);
                chattingItembinding.rightMessageBodyLayout.setVisibility(View.VISIBLE);
                chattingItembinding.rightMessageStatusTv.setVisibility(View.VISIBLE);
                //显示头像
                String myavatarPath = YXClient.getInstance().getUserAvatarByID(imMessage.getFromAccount());
                Glide.with(ChattingActivity.this)
                        .load(myavatarPath)
                        .placeholder(R.drawable.icon_wenda)
                        .transform(new GlideCircleTransform(ChattingActivity.this))
                        .into(chattingItembinding.rightAvatarImv);

                switch (imMessage.getStatus()){
                    case sending:
                        chattingItembinding.rightMessageStatusTv.setText("正在发送...");
                        break;
                    case success:
                        if (imMessage.getAttachment() != null && imMessage.getMsgType() != MsgTypeEnum.custom){
                            switch (imMessage.getAttachStatus()){
                                case def:
                                    chattingItembinding.rightMessageStatusTv.setText("附件未传送");
                                    break;
                                case transferring:
                                    chattingItembinding.rightMessageStatusTv.setText("正在传送附件...");
                                    break;
                                case transferred:
                                    chattingItembinding.rightMessageStatusTv.setText("发送成功");
                                    break;
                                case fail:
                                    chattingItembinding.rightMessageStatusTv.setText("附件传送失败");
                                    break;
                            }
                        }
                        else {
                            chattingItembinding.rightMessageStatusTv.setText("发送成功");
                        }
                        break;
                    case fail:
                        chattingItembinding.rightMessageStatusTv.setText("发送失败");
                        break;
                }
                if (imMessage.getMsgType() == MsgTypeEnum.text){
                    chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                    chattingItembinding.rightTextTv.setText(imMessage.getContent());
                }
                else if (imMessage.getMsgType() == MsgTypeEnum.file){
                    chattingItembinding.rightTextTv.setVisibility(View.GONE);
                    chattingItembinding.rightFileDialogLayout.setVisibility(View.VISIBLE);
                    chattingItembinding.rightFileNameTv.setText(((FileAttachment)imMessage.getAttachment()).getDisplayName());
                    chattingItembinding.rightFileSizeTv.setText(SizeUtil.convertSizeLong2String(
                            ((FileAttachment)imMessage.getAttachment()).getSize() ,
                            2 ,
                            BigDecimal.ROUND_HALF_UP
                    ));
                }
                else if (imMessage.getMsgType() == MsgTypeEnum.custom){
                    chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                    final BookRecommandAttachment attachment = (BookRecommandAttachment)imMessage.getAttachment();
                    if(attachment != null){
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("向您推荐图书 : 《");
                        if (attachment.bookInfo != null){
                            SpannableString spannableString = new SpannableString(attachment.bookInfo.getBookTitle());
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    UIUtils.showToastSafe("bookinfo : " + attachment.bookInfo.getBookTitle());
                                }
                            } , 0 , spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(spannableString);
                        }
                        spannableStringBuilder.append("》，请点击书名查看图书详情。");
                        if (!TextUtils.isEmpty(attachment.recommand_msg)){
                            spannableStringBuilder.append("\r\n推荐信息 :　" + attachment.recommand_msg);
                        }
                        chattingItembinding.rightTextTv.setText(spannableStringBuilder);
                        chattingItembinding.rightTextTv.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
                else {

                }
            }
            else {
                chattingItembinding.leftAvatarImv.setVisibility(View.VISIBLE);
                chattingItembinding.leftMessageBodyLayout.setVisibility(View.VISIBLE);
                chattingItembinding.leftMessageStatusTv.setVisibility(View.VISIBLE);
                chattingItembinding.rightAvatarImv.setVisibility(View.GONE);
                chattingItembinding.rightMessageBodyLayout.setVisibility(View.GONE);
                chattingItembinding.rightMessageStatusTv.setVisibility(View.GONE);
                //显示头像
                String myavatarPath = YXClient.getInstance().getUserAvatarByID(imMessage.getFromAccount());
                Glide.with(ChattingActivity.this)
                        .load(myavatarPath)
                        .placeholder(R.drawable.icon_wenda)
                        .transform(new GlideCircleTransform(ChattingActivity.this))
                        .into(chattingItembinding.leftAvatarImv);
                switch (imMessage.getStatus()){
                    case sending:
                        chattingItembinding.leftMessageStatusTv.setText("正在接收...");
                        break;
                    case success:
                        if (imMessage.getAttachment() != null && imMessage.getMsgType() != MsgTypeEnum.custom){
                            switch (imMessage.getAttachStatus()){
                                case def:
                                    chattingItembinding.leftMessageStatusTv.setText("附件未接收");
                                    break;
                                case transferring:
                                    chattingItembinding.leftMessageStatusTv.setText("正在接收附件...");
                                    break;
                                case transferred:
                                    chattingItembinding.leftMessageStatusTv.setText("接收成功");
                                    break;
                                case fail:
                                    chattingItembinding.leftMessageStatusTv.setText("附件接收失败");
                                    break;
                            }
                        }
                        else {
                            chattingItembinding.leftMessageStatusTv.setText("接收成功");
                        }
                        break;
                    case fail:
                        chattingItembinding.leftMessageStatusTv.setText("接收失败");
                        break;
                }
                if (imMessage.getMsgType() == MsgTypeEnum.text){
                    chattingItembinding.leftTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.GONE);
                    chattingItembinding.leftTextTv.setText(imMessage.getContent());
                }
                else if (imMessage.getMsgType() == MsgTypeEnum.file){
                    FileAttachment fileAttachment = (FileAttachment)imMessage.getAttachment();
                    chattingItembinding.leftTextTv.setVisibility(View.GONE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileNameTv.setText(
                            StringUtils.cutString(fileAttachment.getDisplayName() , 8)
                    );
                    chattingItembinding.leftFileSizeTv.setText(
                            SizeUtil.convertSizeLong2String(fileAttachment.getSize())
                    );
                    chattingItembinding.leftFileIconImv.setImageResource(getIconResBaseFileName(fileAttachment.getDisplayName()));
                }
                else if (imMessage.getMsgType() == MsgTypeEnum.custom){
                    chattingItembinding.leftTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.GONE);
                    final BookRecommandAttachment attachment = (BookRecommandAttachment)imMessage.getAttachment();
                    if(attachment != null){
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("向您推荐图书 : 《");
                        if (attachment.bookInfo != null){
                            SpannableString spannableString = new SpannableString(attachment.bookInfo.getBookTitle());
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    UIUtils.showToastSafe("bookinfo : " + attachment.bookInfo.getBookTitle());
                                }
                            } , 0 , spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(spannableString);
                        }
                        spannableStringBuilder.append("》，请点击书名查看图书详情。");
                        if (!TextUtils.isEmpty(attachment.recommand_msg)){
                            spannableStringBuilder.append("\r\n推荐信息 :　" + attachment.recommand_msg);
                        }
                        chattingItembinding.leftTextTv.setText(spannableStringBuilder);
                        chattingItembinding.leftTextTv.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
                else {

                }
            }
            return convertView;
        }
    }


    private int getIconResBaseFileName(String fileName){
        if (fileName.endsWith(".pdf"))return R.drawable.icon_pdf;
        if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx"))return R.drawable.icon_ppt;
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx"))return R.drawable.icon_doc;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))return R.drawable.icon_xsl;
        return R.drawable.img_normal_zuoye;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incommingMessageObserver , false);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(msgSendStatusObserver , false);
    }

    private void openFile(String path , String displayName){
        if (displayName.endsWith(".pdf")
                || displayName.endsWith(".ppt") || displayName.endsWith(".pptx")
                || displayName.endsWith(".doc") || displayName.endsWith(".docx")
                ||displayName.endsWith(".xls") || displayName.endsWith(".xlsx")){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(path), "application/msword");
            startActivity(intent);
        }
        else {
            UIUtils.showToastSafe("不支持的文件格式");
        }
    }
}
