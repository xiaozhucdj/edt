package com.yougy.message.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.WriteHomeWorkActivity;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.attachment.BookRecommandAttachment;
import com.yougy.message.MyEdittext;
import com.yougy.message.SizeUtil;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.HomeworkRemindAttachment;
import com.yougy.message.attachment.TaskRemindAttachment;
import com.yougy.shop.activity.ShopBookDetailsActivity;
import com.yougy.shop.globle.ShopGloble;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityChattingBinding;
import com.yougy.ui.activity.databinding.ItemChattingBinding;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/3/22.
 */

public class ChattingActivity extends MessageBaseActivity implements YXClient.OnErrorListener<IMMessage> {
    ArrayList<IMMessage> messageList = new ArrayList<IMMessage>();
    String id;
    SessionTypeEnum type;
    String name;
    ArrayList<String> idList;
    ArrayList<String> nameList;
    ChattingAdapter adapter = new ChattingAdapter();
    ActivityChattingBinding binding;

    private boolean isMyMessage(IMMessage message) {
        if ((message.getSessionType() == type) && message.getSessionId().equals(id)) {
            LogUtils.e("FH", message.toString() + "是我的消息");
            return true;
        }
        LogUtils.e("FH", message.toString() + "不是我的消息");
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.bind(setLayoutRes(R.layout.activity_chatting));
        initChattingListview();
        initInputEdittext();
        YoungyApplicationManager.IN_CHATTING = true;
        binding.messageEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToBottom(250);
            }
        });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        YXClient.getInstance().checkIfNotLoginThenDoIt(this , null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YoungyApplicationManager.IN_CHATTING = false;
    }

    @Override
    public void init() {
        type = getIntent().getStringExtra("type") == null ? null : SessionTypeEnum.valueOf(getIntent().getStringExtra("type"));
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        if (TextUtils.isEmpty(id) || type == null || TextUtils.isEmpty(name)) {
            UIUtils.showToastSafe("获取消息发送对象失败");
            finish();
        }
        YXClient.getInstance().clearUnreadMsgCount(id, type);
        YXClient.getInstance().callOnRecentContactChangeLiseners();
    }

    @Override
    public void loadData() {
        //查询历史消息
        YXClient.getInstance().queryHistoryMsgList(type, id, 9999, System.currentTimeMillis() + 3600000
                , new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            UIUtils.showToastSafe("获取历史消息成功 " + result.size());
                            for (IMMessage message : result) {
                                LogUtils.e("FH", "查询到消息为 " + message.getMsgType() + " " + message.getContent());
                                if (message.getMsgType() == MsgTypeEnum.text || message.getMsgType() == MsgTypeEnum.file
                                        || message.getMsgType() == MsgTypeEnum.custom) {
                                    messageList.add(message);
                                }
                            }
                            scrollToBottom(300);
                            adapter.notifyDataSetChanged();
                        } else {
                            LogUtils.e("FH", "获取历史消息失败 : " + code + "  " + exception);
                            UIUtils.showToastSafe("获取历史消息失败 : " + code + "  " + exception);
                        }
                    }
                });
        YXClient.getInstance().with(this).addOnNewMessageListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage newMessage) {
                LogUtils.e("FH", "ChattingActivity接收到新消息" + newMessage + " ssid " + newMessage.getSessionId() + " sstype : " + newMessage.getSessionType());
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
                LogUtils.e("FH", "ChattingActivity message 状态更新  sid : " + message.getSessionId() + " sstype: " + message.getSessionType() + " content : " + message.getContent() + "  status : " + message.getStatus() + " attstatus : " + message.getAttachStatus());
                if (isMyMessage(message)) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    public void initChattingListview() {
        binding.chattingListview.setAdapter(adapter);
        binding.chattingListview.setDividerHeight(0);
        binding.chattingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IMMessage message = messageList.get(position);
                if (message.getAttachment() != null && message.getMsgType() != MsgTypeEnum.custom) {
                    if (message.getAttachStatus() != AttachStatusEnum.transferred) {
                        YXClient.getInstance().downloadAttachment(message, false);
                    } else {
                        FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
                        openFile("file://" + fileAttachment.getPathForSave(), fileAttachment.getDisplayName());
                    }
                }
            }
        });
    }

    public void initInputEdittext() {
        binding.messageEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                send();
                return true;
            }
        });
        binding.messageEdittext.setSoftInputListener(new MyEdittext.SoftInputListener() {

            @Override
            public void onBack() {
                binding.messageEdittext.clearFocus();
            }

            @Override
            public void onFocusChanged(boolean focused) {
                if (focused) {
                    if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
                        binding.bottomBarLayout.setPadding(0, 0, 0, 500);
                    } else {
                        binding.bottomBarLayout.setPadding(0, 0, 0, 320);
                    }
                    scrollToBottom(100);
                } else {
                    binding.bottomBarLayout.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    private void send() {
        binding.bottomBarLayout.setPadding(0, 0, 0, 0);
        binding.messageEdittext.clearFocus();
        YXClient.getInstance().checkIfNotLoginThenDoIt(this, new RequestCallback() {
            @Override
            public void onSuccess(Object param) {
                if (!TextUtils.isEmpty(binding.messageEdittext.getText()) && binding.messageEdittext.getText().toString().startsWith("test")){
                    YXClient.getInstance().sendTestMessage(id, type, "109010001", "2", new ArrayList<String>(){{
                        add("1420");
                        add("1421");
                    }}, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            LogUtils.v("发送测试消息成功");
                        }

                        @Override
                        public void onFailed(int code) {
                            LogUtils.v("发送测试消息失败" + code);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            LogUtils.v("发送测试消息失败" + exception.getMessage());
                            exception.printStackTrace();
                        }
                    });
                    return;
                }
                IMMessage message = YXClient.getInstance().sendTextMessage(id,
                        type, binding.messageEdittext.getText().toString(), ChattingActivity.this);
                if (message != null) {
                    messageList.add(message);
                    binding.messageEdittext.setText("");
                    adapter.notifyDataSetChanged();
                    scrollToBottom(200);
                }
            }

            @Override
            public void onFailed(int code) {
                ToastUtil.showCustomToast(getApplicationContext() , "连接消息服务器失败!");
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }


    public void scrollToBottom(long delay) {
        YoungyApplicationManager.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.chattingListview.setSelection(adapter.getCount() - 1);
            }
        }, delay);
    }

    @Override
    protected void initTitleBar(RelativeLayout titleBarLayout, Button leftBtn, TextView titleTv, Button rightBtn) {
        rightBtn.setVisibility(View.GONE);
        titleTv.setText(name);
    }

    /**
     * 发送消息失败会回调到这里
     *
     * @param code
     * @param data
     */
    @Override
    public void onError(int code, IMMessage data) {
        if (code == 802) {
            new HintDialog(this, "发送失败:可能您已经不在这个群中").show();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.item_chatting, null);
                AutoUtils.auto(convertView);
                convertView.setTag(DataBindingUtil.bind(convertView));
            }
            ItemChattingBinding chattingItembinding = (ItemChattingBinding) convertView.getTag();
            final IMMessage imMessage = messageList.get(position);
            //是否显示时间
            if (shouldShowTime(imMessage, (position - 1 < 0 ? null : messageList.get(position - 1)))) {
                chattingItembinding.timeTv.setVisibility(View.VISIBLE);
                chattingItembinding.timeTv.setText(DateUtils.convertTimeMillis2StrRelativeNow(imMessage.getTime(), false));
            } else {
                chattingItembinding.timeTv.setVisibility(View.GONE);
            }

            if (imMessage.getDirect() == MsgDirectionEnum.Out) {
                chattingItembinding.leftAvatarImv.setVisibility(View.GONE);
                chattingItembinding.leftMessageBodyLayout.setVisibility(View.GONE);
                chattingItembinding.leftMessageStatusTv.setVisibility(View.GONE);
                chattingItembinding.rightAvatarImv.setVisibility(View.VISIBLE);
                chattingItembinding.rightMessageBodyLayout.setVisibility(View.VISIBLE);
                chattingItembinding.rightMessageStatusTv.setVisibility(View.VISIBLE);
                //显示头像
                switch (SpUtils.getSex()) {
                    case "女":
                        chattingItembinding.rightAvatarImv.setImageResource(R.drawable.icon_avatar_student_famale_74px);
                        break;
                    case "男":
                        chattingItembinding.rightAvatarImv.setImageResource(R.drawable.icon_avatar_student_male_74px);
                        break;
                }
                switch (imMessage.getStatus()) {
                    case sending:
                        chattingItembinding.rightMessageStatusTv.setText("正在发送...");
                        break;
                    case success:
                        if (imMessage.getAttachment() != null && imMessage.getMsgType() != MsgTypeEnum.custom) {
                            switch (imMessage.getAttachStatus()) {
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
                        } else {
//                                    chattingItembinding.rightMessageStatusTv.setText("发送成功");
                            chattingItembinding.rightMessageStatusTv.setText("");
                        }
                        break;
                    case fail:
                        chattingItembinding.rightMessageStatusTv.setText("发送失败");
                        break;
                }
                if (imMessage.getMsgType() == MsgTypeEnum.text) {
                    chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                    chattingItembinding.rightTextTv.setText(imMessage.getContent());
                } else if (imMessage.getMsgType() == MsgTypeEnum.file) {
                    chattingItembinding.rightTextTv.setVisibility(View.GONE);
                    chattingItembinding.rightFileDialogLayout.setVisibility(View.VISIBLE);
                    chattingItembinding.rightFileNameTv.setText(((FileAttachment) imMessage.getAttachment()).getDisplayName());
                    chattingItembinding.rightFileSizeTv.setText(SizeUtil.convertSizeLong2String(
                            ((FileAttachment) imMessage.getAttachment()).getSize(),
                            2,
                            BigDecimal.ROUND_HALF_UP
                    ));
                } else if (imMessage.getMsgType() == MsgTypeEnum.custom) {
                    if (imMessage.getAttachment() instanceof BookRecommandAttachment) {
                        chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                        chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                        final BookRecommandAttachment attachment = (BookRecommandAttachment) imMessage.getAttachment();
                        if (attachment != null) {
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
                    } else if (imMessage.getAttachment() instanceof HomeworkRemindAttachment) {
                        chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                        chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                        final HomeworkRemindAttachment attachment = (HomeworkRemindAttachment) imMessage.getAttachment();
                        if (attachment != null) {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                            spannableStringBuilder.append("今天的家庭作业还没有完成哦！请点击：");
                            SpannableString spannableString = new SpannableString(attachment.examName);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {
                                    jumpHomework(attachment);
                                }
                            }, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(spannableString);
                            spannableStringBuilder.append("尽快完成作业作答！");
                            chattingItembinding.rightTextTv.setText(spannableStringBuilder);
                            chattingItembinding.rightTextTv.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    } else if (imMessage.getAttachment() instanceof TaskRemindAttachment) {
                        chattingItembinding.rightTextTv.setVisibility(View.VISIBLE);
                        chattingItembinding.rightFileDialogLayout.setVisibility(View.GONE);
                        receiveTaskRemind(chattingItembinding,imMessage);
                    }
                }
            } else {
                chattingItembinding.leftAvatarImv.setVisibility(View.VISIBLE);
                chattingItembinding.leftMessageBodyLayout.setVisibility(View.VISIBLE);
                chattingItembinding.leftMessageStatusTv.setVisibility(View.VISIBLE);
                chattingItembinding.rightAvatarImv.setVisibility(View.GONE);
                chattingItembinding.rightMessageBodyLayout.setVisibility(View.GONE);
                chattingItembinding.rightMessageStatusTv.setVisibility(View.GONE);
                //显示头像
                GenderEnum userGender = YXClient.getInstance().getUserGenderByID(id);
                if (userGender == null) {
                    chattingItembinding.leftAvatarImv.setImageBitmap(null);
                } else if (userGender == GenderEnum.MALE) {
                    chattingItembinding.leftAvatarImv.setImageResource(R.drawable.icon_avatar_teacher_male_76px);
                } else {
                    chattingItembinding.leftAvatarImv.setImageResource(R.drawable.icon_avatar_teacher_famale_74px);
                }
                switch (imMessage.getStatus()) {
                    case sending:
                        chattingItembinding.leftMessageStatusTv.setText("正在接收...");
                        break;
                    case success:
                        if (imMessage.getAttachment() != null && imMessage.getMsgType() != MsgTypeEnum.custom) {
                            switch (imMessage.getAttachStatus()) {
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
                        } else {
//                                    chattingItembinding.leftMessageStatusTv.setText("接收成功");
                            chattingItembinding.leftMessageStatusTv.setText("");
                        }
                        break;
                    case fail:
                        chattingItembinding.leftMessageStatusTv.setText("接收失败");
                        break;
                }
                if (imMessage.getMsgType() == MsgTypeEnum.text) {
                    chattingItembinding.leftTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.GONE);
                    chattingItembinding.leftTextTv.setText(imMessage.getContent());
                } else if (imMessage.getMsgType() == MsgTypeEnum.file) {
                    FileAttachment fileAttachment = (FileAttachment) imMessage.getAttachment();
                    chattingItembinding.leftTextTv.setVisibility(View.GONE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileNameTv.setText(
                            StringUtils.cutString(fileAttachment.getDisplayName(), 8)
                    );
                    chattingItembinding.leftFileSizeTv.setText(
                            SizeUtil.convertSizeLong2String(fileAttachment.getSize())
                    );
                    chattingItembinding.leftFileIconImv.setImageResource(getIconResBaseFileName(fileAttachment.getDisplayName()));
                } else if (imMessage.getMsgType() == MsgTypeEnum.custom) {
                    chattingItembinding.leftTextTv.setVisibility(View.VISIBLE);
                    chattingItembinding.leftFileDialogLayout.setVisibility(View.GONE);
                    LogUtils.e(tag,"attach ment is : " + imMessage.getAttachment());
                    if (imMessage.getAttachment() instanceof BookRecommandAttachment) {
                        final BookRecommandAttachment attachment = (BookRecommandAttachment) imMessage.getAttachment();
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
                    } else if (imMessage.getAttachment() instanceof HomeworkRemindAttachment) {
                        final HomeworkRemindAttachment attachment = (HomeworkRemindAttachment) imMessage.getAttachment();
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("今天的家庭作业还没有完成哦！请点击：");
                        SpannableString spannableString = new SpannableString(attachment.examName);
                        spannableString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                jumpHomework(attachment);
                            }
                        }, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableString);
                        spannableStringBuilder.append("尽快完成作业作答！");
                        chattingItembinding.leftTextTv.setText(spannableStringBuilder);
                        chattingItembinding.leftTextTv.setMovementMethod(LinkMovementMethod.getInstance());
                    }else if (imMessage.getAttachment() instanceof TaskRemindAttachment){
                        receiveTaskRemind(chattingItembinding,imMessage);
                    }

                }
            }
            return convertView;
        }
        private void receiveTaskRemind(ItemChattingBinding chattingItembinding,IMMessage imMessage){
            final TaskRemindAttachment attachment = (TaskRemindAttachment) imMessage.getAttachment();
            if (attachment != null) {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append("今天的任务还没有完成哦！请点击：");
                SpannableString ss = new SpannableString(attachment.taskName);
                ss.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent intent = new Intent(ChattingActivity.this,TaskDetailStudentActivity.class);
                        intent.putExtra(TaskRemindAttachment.KEY_TASK_ID,attachment.taskId);
                        intent.putExtra(TaskRemindAttachment.KEY_DRAMA_ID,attachment.dramaId);
                        intent.putExtra(TaskRemindAttachment.KEY_TASK_NAME,attachment.taskName);
                        intent.putExtra(TaskRemindAttachment.IS_SIGN,attachment.isSign);
                        intent.putExtra(TaskRemindAttachment.SCENE_STATUS_CODE,attachment.sceneStatusCode);
                        startActivity(intent);
                    }
                }, 0, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ssb.append(ss);
                ssb.append("。尽快完成任务！");
                chattingItembinding.leftTextTv.setText(ssb);
                chattingItembinding.leftTextTv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }


    /**
     * 判断此消息提醒的作业是否已经提交，若未完成，则打开作业
     *
     * @param attachment
     */
    private void jumpHomework(HomeworkRemindAttachment attachment) {
        if (attachment == null) {
            return;
        }
        NetWorkManager.queryReply(Integer.parseInt(attachment.examId), SpUtils.getUserId(), null)
                .subscribe(new Action1<List<QuestionReplySummary>>() {
                    @Override
                    public void call(List<QuestionReplySummary> questionReplySummaries) {
                        LogUtils.d("questionReplySummaries size = " + questionReplySummaries.size());
                        if (questionReplySummaries.size() == 0) {
                            Intent intent = new Intent(getThisActivity(), WriteHomeWorkActivity.class);
                            intent.putExtra("examId", attachment.examId);
                            intent.putExtra("examName", attachment.examName);
                            intent.putExtra("isTimerWork", attachment.isTimeWork);
                            intent.putExtra("lifeTime", attachment.lifeTime);
                            intent.putExtra("isStudentCheck", attachment.isStudentCheck);
                            if ("onClass".equals(attachment.examOccasion)) {
                                intent.putExtra("isOnClass", true);
                            } else {
                                intent.putExtra("isOnClass", false);
                            }
                            intent.putExtra("teacherID", attachment.teacherId);
                            startActivity(intent);
                        } else {
                            ToastUtil.showCustomToast(ChattingActivity.this, "作业已经提交！");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        LogUtils.d("questionReplySummaries size = " + throwable.getMessage());
                    }
                });
    }


    private int getIconResBaseFileName(String fileName) {
        if (fileName.endsWith(".pdf")) return R.drawable.icon_pdf;
        if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) return R.drawable.icon_ppt;
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) return R.drawable.icon_doc;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) return R.drawable.icon_xsl;
        return R.drawable.img_normal_zuoye;
    }

    /**
     * 根据与上一条消息的间隔判断是否要显示时间文字
     *
     * @return
     */
    private boolean shouldShowTime(IMMessage thisMessage, IMMessage lastMessage) {
        final long TIME_INTERVAL = 1000 * 60 * 2;//2min
        return !(lastMessage != null && thisMessage.getTime() - lastMessage.getTime() < TIME_INTERVAL);
    }

    private void openFile(String path, String displayName) {
        if (displayName.endsWith(".pdf")
                || displayName.endsWith(".ppt") || displayName.endsWith(".pptx")
                || displayName.endsWith(".doc") || displayName.endsWith(".docx")
                || displayName.endsWith(".xls") || displayName.endsWith(".xlsx")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(path), "application/msword");
            startActivity(intent);
        } else {
            UIUtils.showToastSafe("不支持的文件格式");
        }
    }
}
