package com.yougy.home.activity;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;

import java.net.Inet4Address;
import java.net.InetAddress;

public class DeviceYxMsgErrorDialog extends BaseDialog {
    private TextView tv_net_state;
    private TextView tv_out_net_state;
    private TextView tv_yx_state;
    private TextView tv_last_msg;
    private Button btn_close;
    private Button btn_test;
    private TextView title_tv;

    private String title1 = "测试完成 ，请查看，并拍照:";
    private String title2 = "请耐心等待， 测试 大约需要2分钟...";

    public DeviceYxMsgErrorDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public DeviceYxMsgErrorDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_device_yxmsg_error);
        tv_net_state = (TextView) this.findViewById(R.id.tv_net_state);
        tv_out_net_state = (TextView) this.findViewById(R.id.tv_out_net_state);
        tv_yx_state = (TextView) this.findViewById(R.id.tv_yx_state);
        tv_last_msg = (TextView) this.findViewById(R.id.tv_last_msg);

        title_tv = (TextView) this.findViewById(R.id.title_tv);

        btn_close = (Button) this.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btn_test = (Button) this.findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_tv.setText(title2);
                btn_close.setEnabled(false);
                btn_test.setEnabled(false);


                String result = "null";

                if (YXClient.getInstance().getCurrentOnlineStatus() != null) {
                    switch (YXClient.getInstance().getCurrentOnlineStatus().getValue()) {

                        case 0:
                            result = "未定义";
                            break;

                        case 1:
                            result = "未登录/登录失败";
                            break;

                        case 2:
                            result = "网络连接已断开";
                            break;
                        case 3:
                            result = "正在连接服务器";
                            break;

                        case 4:
                            result = "正在登录中";
                            break;

                        case 5:
                            result = "已成功登录";
                            break;
                        case 6:
                            result = "已成功登录";
                            break;

                        case 7:
                            result = "被其他端的登录踢掉";
                            break;

                        case 8:
                            result = "被同时在线的其他端主动踢掉";
                            break;

                        case 9:
                            result = " 被服务器禁止登录";
                            break;


                        case 10:
                            result = "客户端版本错误";
                            break;
                        case 11:
                            result = "用户名或密码错误";
                            break;
                    }
                }
                tv_yx_state.setText("消息状态:" + result);
                tv_last_msg.setText("最后一条消息" + YoungyApplicationManager.lastAnsMsg);


                if (NetUtils.isNetConnected()) {
                    tv_net_state.setText("网络是是否链接:" + "正常");
                    //查询外网：
                    ThreadManager.getSinglePool().execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean result2 = analysisNet();
                            UIUtils.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (result2) {
                                        tv_out_net_state.setText("外网是否可用:" + "正常");
                                    } else {
                                        tv_out_net_state.setText("外网是否可用:" + "无法使用");
                                    }

                                    btn_close.setEnabled(true);
                                    btn_test.setEnabled(true);
                                    title_tv.setText(title1);
                                    UIUtils.showToastSafe("完成测试，请查看测试结果");
                                }
                            });
                        }
                    });
                } else {
                    btn_close.setEnabled(true);
                    btn_test.setEnabled(true);
                    tv_net_state.setText("网络是是否链接:" + "网络已断开");
                    title_tv.setText(title1);
                    UIUtils.showToastSafe("完成测试，请查看测试结果");
                }
            }
        });
    }

    private boolean analysisNet() {
        boolean result = false;
        // 这种方式如果ping不通 会阻塞一分钟左右
        // 也是要放在另一个线程里面ping
        try {
            InetAddress addr = InetAddress.getByName("www.baidu.com");
            if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                result = true;
            } else {
                result = false;
            }
        } catch (Throwable e) {
            result = false;
        }

        return result;
    }

    @Override
    public void show() {
        super.show();
        tv_net_state.setText("网络是是否链接:");
        tv_out_net_state.setText("外网是否可用:");
        tv_yx_state.setText("消息状态state:");
        tv_last_msg.setText("最后一条消息:");
        title_tv.setText("设备信息!");

    }
}
