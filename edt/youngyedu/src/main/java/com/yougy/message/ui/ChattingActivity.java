package com.yougy.message.ui;

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
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.BookRecommandAttachment;
import com.yougy.message.GlideCircleTransform;
import com.yougy.message.SizeUtil;
import com.yougy.message.YXClient;
import com.yougy.shop.activity.ShopBookDetailsActivity;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityChattingBinding;
import com.yougy.ui.activity.databinding.ItemChattingBinding;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/3/22.
 */

public class ChattingActivity extends MessageBaseActivity implements YXClient.OnErrorListener<IMMessage>{
    ArrayList<IMMessage> messageList = new ArrayList<IMMessage>();
    String id;
    SessionTypeEnum type;
    String name;
    ArrayList<String> idList;
    ArrayList<String> nameList;
    ChattingAdapter adapter = new ChattingAdapter();
    ActivityChattingBinding binding;

    private boolean isMyMessage(IMMessage message){
        if ((message.getSessionType() == type) && message.getSessionId().equals(id)) {
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
        YXClient.checkNetAndRefreshLogin(this , null , null);
    }

    @Override
    public void init() {
        type = getIntent().getStringExtra("type") == null ? null : SessionTypeEnum.valueOf(getIntent().getStringExtra("type"));
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        if (TextUtils.isEmpty(id) || type == null || TextUtils.isEmpty(name)){
            UIUtils.showToastSafe("获取消息发送对象失败");
            finish();
        }
    }

    @Override
    public void loadData() {
        //查询历史消息
        YXClient.getInstance().queryHistoryMsgList(type , id , 9999 , System.currentTimeMillis() + 3600000
                , new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    Log.v("FH", "获取历史消息成功" + result.size() + "条");
                    UIUtils.showToastSafe("获取历史消息成功 " + result.size());
                    for (IMMessage message : result) {
                        Log.v("FH" , "查询到消息为 " +  message.getMsgType() + " " + message.getContent());
                        if (message.getMsgType() == MsgTypeEnum.text || message.getMsgType() == MsgTypeEnum.file
                                || message.getMsgType() == MsgTypeEnum.custom) {
                            messageList.add(message);
                        }
                    }
                    scrollToBottom(300);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.v("FH", "获取历史消息失败 : " + code + "  " + exception);
                    UIUtils.showToastSafe("获取历史消息失败 : " + code + "  " + exception);
                }
            }
        });
        YXClient.getInstance().with(this).addOnNewMessageListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage newMessage) {
                Log.v("FH", "ChattingActivity接收到新消息" + newMessage + " ssid " + newMessage.getSessionId() + " sstype : " + newMessage.getSessionType());
                if (isMyMessage(newMessage)
                        && (newMessage.getMsgType() == MsgTypeEnum.text || newMessage.getMsgType() == MsgTypeEnum.file
                        || newMessage.getMsgType() == MsgTypeEnum.custom)) {
                    messageList.add(newMessage);
                    if (newMessage.getAttachment() != null && newMessage.getAttachment() instanceof FileAttachment) {
                        YXClient.getInstance().downloadAttachment(newMessage, false);
                    }
                }
                scrollToBottom(100);
                adapter.notifyDataSetChanged();
            }
        });
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
            public void onNewMessage(IMMessage message) {
                Log.v("FH" , "ChattingActivity message 状态更新  sid : " + message.getSessionId() + " sstype: " + message.getSessionType() + " content : " + message.getContent() + "  status : " + message.getStatus() + " attstatus : " + message.getAttachStatus());
                if (isMyMessage(message)){
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }



    public void initChattingListview(){
        binding.chattingListview.setAdapter(adapter);
        binding.chattingListview.setDividerHeight(0);
        binding.chattingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IMMessage message = messageList.get(position);
                if (message.getAttachment() != null && message.getMsgType() != MsgTypeEnum.custom){
                    if (message.getAttachStatus() != AttachStatusEnum.transferred){
                        YXClient.getInstance().downloadAttachment(message , false);
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
        YXClient.checkNetAndRefreshLogin(this, new Runnable() {
            @Override
            public void run() {
                IMMessage message = YXClient.getInstance().sendTextMessage(id ,
                        type , binding.messageEdittext.getText().toString() , ChattingActivity.this);
                if (message != null) {
                    messageList.add(message);
                    binding.messageEdittext.setText("");
                    adapter.notifyDataSetChanged();
                    scrollToBottom(200);
                }
            }
        }, null);
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
        titleTv.setText(id + "  " + name);
    }

    /**
     * 发送消息失败会回调到这里
     * @param code
     * @param data
     */
    @Override
    public void onError(int code, IMMessage data) {
        if (code == 802){
            new HintDialog(this , "发送失败:可能您已经不在这个群中").show();
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
                chattingItembinding.rightAvatarImv.setImageResource(R.drawable.icon_student_medium);
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
//                                    chattingItembinding.rightMessageStatusTv.setText("发送成功");
                                    chattingItembinding.rightMessageStatusTv.setText("");
                                    break;
                                case fail:
                                    chattingItembinding.rightMessageStatusTv.setText("附件传送失败");
                                    break;
                            }
                        }
                        else {
//                                    chattingItembinding.rightMessageStatusTv.setText("发送成功");
                            chattingItembinding.rightMessageStatusTv.setText("");
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
                        SpannableString spannableString = new SpannableString(attachment.bookName);
                        spannableString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                Intent intent = new Intent(getThisActivity(), ShopBookDetailsActivity.class);
                                intent.putExtra(ShopGloble.BOOK_ID, Integer.parseInt(attachment.bookId));
                                startActivity(intent);
                            }
                        }, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableString);
                        spannableStringBuilder.append("》，请点击书名查看图书详情。");
                        if (!TextUtils.isEmpty(attachment.recommand_msg)) {
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
                chattingItembinding.leftAvatarImv.setImageResource(R.drawable.icon_teacher_medium);
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
//                                    chattingItembinding.leftMessageStatusTv.setText("接收成功");
                                    chattingItembinding.leftMessageStatusTv.setText("");
                                    break;
                                case fail:
                                    chattingItembinding.leftMessageStatusTv.setText("附件接收失败");
                                    break;
                            }
                        }
                        else {
//                                    chattingItembinding.leftMessageStatusTv.setText("接收成功");
                            chattingItembinding.leftMessageStatusTv.setText("");
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
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append("向您推荐图书 : 《");
                    SpannableString spannableString = new SpannableString(attachment.bookName);
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            Intent intent = new Intent(getThisActivity(), ShopBookDetailsActivity.class);
                            intent.putExtra(ShopGloble.BOOK_ID, Integer.parseInt(attachment.bookId));
                            startActivity(intent);
                        }
                    }, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(spannableString);
                    spannableStringBuilder.append("》，请点击书名查看图书详情。");
                    if (!TextUtils.isEmpty(attachment.recommand_msg)) {
                        spannableStringBuilder.append("\r\n推荐信息 :　" + attachment.recommand_msg);
                    }
                    chattingItembinding.leftTextTv.setText(spannableStringBuilder);
                    chattingItembinding.leftTextTv.setMovementMethod(LinkMovementMethod.getInstance());
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
        final long TIME_INTERVAL = 1000*60*2;//2min
        if (lastMessage != null && thisMessage.getTime() - lastMessage.getTime() < TIME_INTERVAL){
            return false;
        }
        return true;
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
