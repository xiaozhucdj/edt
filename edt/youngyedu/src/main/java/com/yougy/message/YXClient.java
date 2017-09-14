package com.yougy.message;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by FH on 2017/4/7.
 */

/**
 * 对云信api的封装,实际上是一个完整的云信的Client,只是没有界面,提供注册listener和多种get方法与外界的UI界面交互.
 */
public class YXClient {
    Context mContext;

    static public final String ID = "id";
    static public final String USER_NAME = "user_name";
    static public final String USER_AVATAR = "user_avatar";
    static public final String LAST_UPDATE = "last_update";
    static public final String TEAM = "team";
    static public final String IS_FETCHING = "is_fetching";

    private final long UPDATE_THRESHOLD = 1000*60*60;

    private static volatile YXClient instance = null;
    HashMap<String , EmptyFragment> emptyFragmentMap = new HashMap<String, EmptyFragment>();
    private ArrayList<RecentContact> recentContactList = new ArrayList<RecentContact>();
    private HashMap<String, Bundle> userInfoMap = new HashMap<String, Bundle>();
    private HashMap<String, Bundle> teamInfoMap = new HashMap<String, Bundle>();
    private HashMap<String , Pair<Long , List<TeamMember>>> groupMemberMap = new HashMap<String, Pair<Long, List<TeamMember>>>();
    private ArrayList<Team> myTeamList = new ArrayList<Team>();
    private String currentAccount = null;
    private StatusCode currentOnlineStatus = null;
    private boolean recentContactInitFinish = false;
    private boolean teamDataInitFinish = false;


    //全局所有的用户信息更新监听器列表
    private ArrayList<OnThingsChangedListener<Bundle>> onUserInfoChangeListeners
            = new ArrayList<OnThingsChangedListener<Bundle>>();
    //全局所有的群信息更新监听器列表
    private ArrayList<OnThingsChangedListener<Bundle>> onTeamInfoChangeListeners
            = new ArrayList<OnThingsChangedListener<Bundle>>();
    //全局所有的最近联系人更新监听器列表
    private ArrayList<OnThingsChangedListener<List<RecentContact>>> onRecentContactChangeListeners
            = new ArrayList<OnThingsChangedListener<List<RecentContact>>>();
    //全局所有的我加入的群更新监听器列表
    private ArrayList<OnThingsChangedListener<List<Team>>> onMyTeamListChangeListeners
            = new ArrayList<OnThingsChangedListener<List<Team>>>();
    //全局所有的群成员变更监听器列表
    private ArrayList<OnThingsChangedListener<Pair<String , List<TeamMember>>>> onTeamMemberChangedListeners
            = new ArrayList<OnThingsChangedListener<Pair<String,List<TeamMember>>>>();
    //全局所有的自定义新到消息监听器列表
    private ArrayList<OnMessageListener> onNewMessageListenerList = new ArrayList<OnMessageListener>();
    //全局所有的自定义消息发送状态监听器列表
    private ArrayList<OnMessageListener> onMsgStatusChangedListenerList = new ArrayList<OnMessageListener>();

    //命令型消息监听器,用来通知命令型(不需要在消息列表中显示)的消息,例如开始问答,结束问答等
    private OnMessageListener onCommandCustomMsgListener;

    //新到消息监听器
    Observer<List<IMMessage>> incommingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            //学生端处理逻辑:老师可以给多个学生使用群发送群消息,但是对学生屏蔽群的存在,群消息过来以后会被转换成p2p消息,
            //并且显示在与那个老师的p2p对话的时间线上
            //所以此处的处理逻辑是收到群消息后不调用UI注册的新到消息监听器,而是先手动建立一条p2p消息,内容与群消息一致,然后把新建的p2p消息通知到UI注册的消息监听器中.
            //并且把新建的p2p消息插入云信SDK的本地数据库,这样就方便以后查询p2p历史记录时,可以直接一次性的把群消息和p2p消息都查出来.
            //插入数据库后SDK的最近联系人列表也会自动更新,不需要手动通知.
            for (IMMessage newMessage : imMessages) {
                Log.v("FH", "接收到新消息" + newMessage + " ssid " + newMessage.getSessionId() + " sstype : " + newMessage.getSessionType() + "  content : " + newMessage.getContent() + "  msgType : " + newMessage.getMsgType() + " attach : " + newMessage.getAttachment());
                if ((newMessage.getMsgType()) == MsgTypeEnum.custom){
                    if (newMessage.getAttachment() == null){
                        // 解析有问题的自定义消息attachment为空,滤掉这类消息
                        continue;
                    }
                    else if (newMessage.getAttachment() instanceof AskQuestionAttachment
                            || newMessage.getAttachment() instanceof EndQuestionAttachment){
                        if (onCommandCustomMsgListener != null){
                            onCommandCustomMsgListener.onNewMessage(newMessage);
                        }
                        continue;
                    }
                    else {

                    }
                }
                if (newMessage.getSessionType() == SessionTypeEnum.Team) {
                    Log.v("FH" , "接到的是Team消息 , 新建同样内容消息后修改为p2p消息插入本地数据库");
                    IMMessage localMessage = null;
                    switch (newMessage.getMsgType()){
                        case file:
                            localMessage = MessageBuilder.createFileMessage(newMessage.getFromAccount() , SessionTypeEnum.P2P , null , null);
                            localMessage.setAttachment(newMessage.getAttachment());
                            localMessage.setAttachStatus(newMessage.getAttachStatus());
                            break;
                        case custom:
                            localMessage = MessageBuilder.createCustomMessage(newMessage.getFromAccount() , SessionTypeEnum.P2P, "[图书推荐]" , newMessage.getAttachment());
                            localMessage.setAttachStatus(newMessage.getAttachStatus());
                            break;
                        case text:
                            localMessage = MessageBuilder.createTextMessage(newMessage.getFromAccount(), SessionTypeEnum.P2P , newMessage.getContent());
                            break;
                        default:
                            Log.v("FH" , "未知类型 " + newMessage.getMsgType() + "新建消息失败");
                    }
                    if (localMessage != null){
                        Log.v("FH" , "开始插入数据库");
                        localMessage.setFromAccount(newMessage.getFromAccount());
                        localMessage.setDirect(MsgDirectionEnum.In);
                        localMessage.setStatus(MsgStatusEnum.success);

                        NIMClient.getService(MsgService.class).saveMessageToLocalEx(localMessage , false , newMessage.getTime())
                        .setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                Log.v("FH" , "插入数据库成功");
                            }
                            @Override
                            public void onFailed(int code) {
                                Log.v("FH" , "插入数据库失败 : " + code);
                            }
                            @Override
                            public void onException(Throwable exception) {
                                Log.v("FH" , "插入数据库失败 : " + exception.getMessage());
                                exception.printStackTrace();
                            }
                        });
                        for (OnMessageListener listener : onNewMessageListenerList) {
                            listener.onNewMessage(localMessage);
                        }
                    }
                }
                else {
                    for (OnMessageListener listener : onNewMessageListenerList) {
                        listener.onNewMessage(newMessage);
                    }
                }
            }
        }
    };

    //系统通知监听器
    Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            lv("收到系统通知 : " + systemMessage);
        }
    };

    //自定义通知接收器
    Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            lv("收到自定义通知" + " content : " + customNotification.getContent()
            + " from : " + customNotification.getFromAccount()
                    + " sstype : " + customNotification.getSessionType()
                    + " ssid : " + customNotification.getSessionId()
                    + " time : " + customNotification.getTime());
        }
    };

    //消息状态改变监听器
    Observer<IMMessage> msgSendStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage newMessage) {
            lv("message 状态更新  sid : " + newMessage.getSessionId() + " sstype: " + newMessage.getSessionType() + " content : " + newMessage.getContent() + "  status : " + newMessage.getStatus() + " attstatus : " + newMessage.getAttachStatus());
            for (OnMessageListener listener : onMsgStatusChangedListenerList) {
                listener.onNewMessage(newMessage);
            }
        }
    };

    //在线状态变更观察者
    private Observer<StatusCode> onlineStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            currentOnlineStatus = statusCode;
            Log.v("FH" , "onlineStatus 变更: " + statusCode);
            if (statusCode == StatusCode.PWD_ERROR){
//                if (!TextUtils.isEmpty(currentAccount)){
//                    getTokenAndLogin(currentAccount , null);
//                }
            }
            else if (statusCode == StatusCode.KICKOUT){

            }
        }
    };
    //最近联系人变更观察者
    private Observer<List<RecentContact>> recentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> newRecentContactList) {
            //学生端处理逻辑,由于学生端需要屏蔽群的概念,而此处收到的联系人变更通知是包含群消息引起的变动通知,
            //就是说收到群消息导致最近联系人变动,此处也会收到通知.
            //因此需要手动过滤群消息导致的联系人变动,并且把变动后的最近联系人列表中的群消息项删除,再通知到UI注册的监听器.
            //另外,学生端10位id的其他学生和6位id的管理员都不需要显示在最近通话中,此处滤掉
            lv("收到最近联系人变更(包含群) " + newRecentContactList.size() + "个");
            for (int i = newRecentContactList.size() - 1 ; i >= 0 ; i--) {
                RecentContact newRecentContact = newRecentContactList.get(i);
                if (newRecentContact.getSessionType() == SessionTypeEnum.Team){
                    newRecentContactList.remove(newRecentContact);
                    continue;
                }
                if (newRecentContact.getContactId().length() == 6
                        || (newRecentContact.getContactId().length() == 10 && !newRecentContact.getContactId().equals(SpUtil.getUserId()))){
                    //另外,学生端10位id的其他学生和6位id的管理员都不需要显示在最近通话中,此处滤掉
                    newRecentContactList.remove(newRecentContact);
                    continue;
                }
                for (int j = 0 ; j < recentContactList.size() ;) {
                    RecentContact oldContact = recentContactList.get(j);
                    if (oldContact.getContactId().equals(newRecentContact.getContactId())) {
                        recentContactList.remove(oldContact);
                        break;
                    }
                    else {
                        j++;
                    }
                }
                switch (newRecentContact.getSessionType()){
                    case P2P:
                        updateUserInfo(newRecentContact.getContactId() , false);
                        break;
                }
            }
            recentContactList.addAll(0 , newRecentContactList);
            for (OnThingsChangedListener<List<RecentContact>> listener : onRecentContactChangeListeners) {
                listener.onThingChanged(recentContactList , ALL);
            }
        }
    };
    //群组资料变更观察者
    private Observer<List<Team>> teamUpdateObserver = new Observer<List<Team>>() {
        @Override
        public void onEvent(List<Team> newTeams) {
            //收到群组资料变更,参数为变更的群组
            lv("收到群组资料变更 变更的群个数 " + newTeams.size());
            for (final Team newTeam: newTeams) {
                if (ListUtil.conditionalRemove(myTeamList, new ListUtil.ConditionJudger<Team>() {
                    @Override
                    public boolean isMatchCondition(Team nodeInList) {
                        return nodeInList.getId().equals(newTeam.getId());
                    }
                }) != 0){
                    myTeamList.addAll(0, newTeams);
                }
                Bundle bundle = makeTeamInfoBundle(newTeam);
                teamInfoMap.put(newTeam.getId() , bundle);
                for (OnThingsChangedListener<Bundle> listener: onTeamInfoChangeListeners) {
                    listener.onThingChanged(bundle , ALL);
                }
            }
            myTeamList.addAll(0, newTeams);
        }
    };
    //自己退群,被移除出群观察者
    private Observer<Team> teamRemoveObserver = new Observer<Team>() {
        @Override
        public void onEvent(final Team quitedTeam) {
            lv("收到退群通知");
            lv("收到退群通知 id " + quitedTeam.getId());
            ListUtil.conditionalRemove(myTeamList, new ListUtil.ConditionJudger<Team>() {
                @Override
                public boolean isMatchCondition(Team nodeInList) {
                    return nodeInList.getId().equals(quitedTeam.getId());
                }
            });
            groupMemberMap.remove(quitedTeam.getId());
            teamInfoMap.remove(quitedTeam.getId());
            List<Team> list = new ArrayList<Team>(){{
                add(quitedTeam);
            }};
            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                listener.onThingChanged(list , DELETE);
            }
        }
    };
    // 群成员资料变化观察者通知。群组添加新成员，成员资料变化会收到该通知。
    // 返回的参数为有更新的群成员资料列表。
    private Observer<List<TeamMember>> teamMemberUpdateObserver = new Observer<List<TeamMember>>() {
        @Override
        public void onEvent(List<TeamMember> members) {
            lv("收到群成员资料变化观察者通知 数量 " + members.size());
            for (final TeamMember newMember : members) {
                if (newMember.getAccount().length() == 6 || (newMember.getAccount().length() == 10 && !newMember.getAccount().equals(SpUtil.getUserId() + ""))){
                    //需要屏蔽群中其他学生10位ID,和管理员6位ID的成员变化
                    continue;
                }
                if (getTeamMemberByID(newMember.getTid()) != null){
                    Pair<Long , List<TeamMember>> pair = groupMemberMap.get(newMember.getTid());
                    pair.first = System.currentTimeMillis();
                    if (ListUtil.conditionalContains(pair.sencond, new ListUtil.ConditionJudger<TeamMember>() {
                        @Override
                        public boolean isMatchCondition(TeamMember nodeInList) {
                            return nodeInList.getAccount().equals(newMember.getAccount());
                        }
                    })){
                        ListUtil.conditionalRemove(pair.sencond, new ListUtil.ConditionJudger<TeamMember>() {
                            @Override
                            public boolean isMatchCondition(TeamMember nodeInList) {
                                return nodeInList.getAccount().equals(newMember.getAccount());
                            }
                        });
                        pair.sencond.add(newMember);
                        for (OnThingsChangedListener<Pair<String , List<TeamMember>>> listener : onTeamMemberChangedListeners) {
                            listener.onThingChanged(new Pair<>(newMember.getTid() , pair.sencond) , ALL);
                        }
                    }
                    else {
                        pair.sencond.add(newMember);
                        for (OnThingsChangedListener<Pair<String, List<TeamMember>>> listener : onTeamMemberChangedListeners) {
                            listener.onThingChanged(new Pair<>(newMember.getTid(), (List<TeamMember>)new ArrayList<TeamMember>() {{add(newMember);}})
                                    , NEW);
                        }
                    }
                }
            }
        }
    };
    //群成员被移除观察者
    private Observer<TeamMember> teamMemberRemoveObserver = new Observer<TeamMember>(){
        @Override
        public void onEvent(final TeamMember teamMember) {
            lv("收到群成员被移除通知" + teamMember);
            if (teamMember.getAccount().length() == 6 || (teamMember.getAccount().length() == 10 && !teamMember.getAccount().equals(SpUtil.getUserId()+""))){
                //需要屏蔽群中其他学生10位ID,和管理员6位ID的成员变化
                return;
            }
            if (getTeamMemberByID(teamMember.getTid()) != null){
                Pair<Long , List<TeamMember>> pair = groupMemberMap.get(teamMember.getTid());
                pair.first = System.currentTimeMillis();
                ListUtil.conditionalRemove(pair.sencond, new ListUtil.ConditionJudger<TeamMember>() {
                    @Override
                    public boolean isMatchCondition(TeamMember nodeInList) {
                        return nodeInList.getAccount().equals(teamMember.getAccount());
                    }
                });
                List<TeamMember> list = new ArrayList<TeamMember>(){{
                    add(teamMember);
                }};
                for (OnThingsChangedListener<Pair<String, List<TeamMember>>> listener : onTeamMemberChangedListeners) {
                    listener.onThingChanged(new Pair<String, List<TeamMember>>(teamMember.getTid() , list) , DELETE);
                }
            }
        }
    };
    //消息过滤器
    private IMMessageFilter messageFilter = new IMMessageFilter() {
        @Override
        public boolean shouldIgnore(IMMessage message) {
            lv("过滤器收到信息!message type :　" + message.getMsgType() +
                    " content : " + message.getContent() +
                    " attach : " + message.getAttachment() +
                    ((message.getAttachment() != null && message.getAttachment() instanceof NotificationAttachment) ? "attType : " + ((NotificationAttachment)message.getAttachment()).getType() : ""));
            //过滤通知类消息
            if (message.getMsgType() == MsgTypeEnum.notification){
                if (message.getAttachment() != null && message.getAttachment() instanceof MemberChangeAttachment){
                    MemberChangeAttachment memberChangeAttachment = (MemberChangeAttachment) message.getAttachment();
                    if (memberChangeAttachment.getType() == NotificationType.InviteMember){
                        ArrayList<String> target = memberChangeAttachment.getTargets();
                        for (String id : target) {
                            if (id.equals(SpUtil.justForTest())){
                                NIMClient.getService(TeamService.class).queryTeam(message.getSessionId()).setCallback(new RequestCallbackWrapper<Team>() {
                                    @Override
                                    public void onResult(int code, final Team result, Throwable exception) {
                                        if (code == ResponseCode.RES_SUCCESS){
                                            myTeamList.add(result);
                                            for (OnThingsChangedListener<List<Team>> listener: onMyTeamListChangeListeners){
                                                listener.onThingChanged(new ArrayList<Team>(){{add(result);}}, NEW);
                                            }
                                        }
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }
    };
    //自定义消息解析器
    private CustomAttachParser customAttachParser = new CustomAttachParser();

    /**
     * 获得一个与给定activity绑定的{@link YXClient.ListenerManager}
     * 用于添加和移除listener,绑定activity后,add的listener会自动在activity的onDestroy生命周期中注销,防止内存泄漏.
     * 因此如没有特殊需要,add的listener不用手动remove.
     *
     * @param activity 需要绑定的activity
     * @return 与给定activity生命周期绑定的ListenerManager,使用此manager 添加的listener,不需要手动remove.
     * 它们会在activity onDestroy()的时候自动remove.
     */
    public synchronized ListenerManager with (final Activity activity){
        lv("call with activity=" + activity);
        EmptyFragment emptyFragment = emptyFragmentMap.get(activity.toString());
        if (emptyFragment != null){
            lv("emptyFragment 已经存在:" + emptyFragment.toString());
            lv("返回manager:" + emptyFragment.lifeCycle.getManager());
            return emptyFragment.lifeCycle.getManager();
        }
        final ListenerManager manager = new ListenerManager();
        LifeCycle lifeCycle = new LifeCycle() {
            @Override
            void onStart() {}
            @Override
            void onResume() {}
            @Override
            void onPause() {}
            @Override
            void onStop() {}
            @Override
            void onDestroy() {
                lv("onDestroy-----lifeCycle manage=" + getManager().toString());
                while(getManager().myOnMyTeamListChangeListeners.size() > 0){
                    getManager().removeOnMyTeamListChangeListener(getManager().myOnMyTeamListChangeListeners.get(0));
                }
                while(getManager().myOnTeamMemberChangedListeners.size() > 0){
                    getManager().removeOnTeamMemberChangeListener(getManager().myOnTeamMemberChangedListeners.get(0));
                }
                while(getManager().myOnTeamInfoChangeListeners.size() > 0){
                    getManager().removeOnTeamInfoChangeListener(getManager().myOnTeamInfoChangeListeners.get(0));
                }
                while(getManager().myOnRecentContactChangeListeners.size() > 0){
                    getManager().removeOnRecentContactListChangeListener(getManager().myOnRecentContactChangeListeners.get(0));
                }
                while(getManager().myOnUserInfoChangeListeners.size() > 0){
                    getManager().removeOnUserInfoChangeListener(getManager().myOnUserInfoChangeListeners.get(0));
                }
                while(getManager().myOnNewMessageListenerList.size() > 0){
                    getManager().removeOnNewMessageListener(getManager().myOnNewMessageListenerList.get(0));
                }
                emptyFragmentMap.remove(activity.toString());
            }
        }.setManager(manager);

        emptyFragment = new EmptyFragment().setlifeCycle(lifeCycle);
        lv("emptyFragment 不存在 新建emptyFragment:" + emptyFragment);
        emptyFragmentMap.put(activity.toString() , emptyFragment);
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(emptyFragment , "empty");
        transaction.commit();
        lv("返回manager:" + manager.toString());
        return manager;
    }


    /**
     * 获取单例的实例
     * @return
     */
    public static YXClient getInstance() {
        if (instance == null) {
            synchronized (YXClient.class) {
                if (instance == null) {
                    instance = new YXClient();
                }
            }
        }
        return instance;
    }

    /**
     * 设置命令型消息监听器,用于接收命令型(不需要在消息列表中显示)消息的消息,例如开始问答,结束问答等
     * @param onCommandCustomMsgListener
     * @return
     */
    public YXClient setOnCommandCustomMsgListener(OnMessageListener onCommandCustomMsgListener) {
        this.onCommandCustomMsgListener = onCommandCustomMsgListener;
        return this;
    }

    /**
     * 初始化NimClient,必须在application的onCreate中调用(所有进程的onCreate中都要调用,否则会报错),必须放在所有的云信操作前调用.
     *
     * @param application
     */
    public static void initNimClient(Application application) {
        NIMClient.init(application , null , null);
    }

    /**
     * 初始化云信配置,注册全局性的处理器和解析器等
     * 初始化并不会登录,所有数据的同步和会在登录成功后获取.登录建议使用{@link YXClient#getTokenAndLogin(String, RequestCallbackWrapper)}
     * @param context 上下文环境
     */
    public void initOption(Context context){
        mContext = context;
        //注册消息过滤器
        NIMClient.getService(MsgService.class).registerIMMessageFilter(messageFilter);
        //注册自定义消息解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(customAttachParser);
        //注册新到消息观察者
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incommingMessageObserver , true);
        //注册消息发送状态改变观察者
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(msgSendStatusObserver, true);
        //注册系统通知观察者
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver , true);
        //注册自定义通知观察者
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver , true);
    }

    /**
     * 使用用户名密码登录云信
     * @param account 用户名
     * @param token 密码,由于多端登录的存在,改密码可能随时被变更,
     *              建议使用{@link YXClient#getTokenAndLogin(String, RequestCallbackWrapper)}获取最新密码登录.
     *              如果密码错误,会自动联网获取最新密码,并且重新登录.
     * @param callback 登录结果回调,如果为null,则不处理回调
     */
    public void login(String account , String token , final RequestCallbackWrapper callback){
        Log.v("FH" , "yx login " + account + "  " + token);
        currentAccount = account;
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver , true);
        LoginInfo loginInfo = new LoginInfo(account , token);
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(new RequestCallback() {
            @Override
            public void onSuccess(Object param) {
                lv("登录成功!" + param);
                if (!recentContactInitFinish){
                    initRecentContact();
                }
                if (!teamDataInitFinish){
                    initTeamData();
                }
                if (callback != null){
                    callback.onSuccess(param);
                }
            }
            @Override
            public void onFailed(int code) {
                lv("登录失败!code=" + code);
                if (callback != null){
                    callback.onFailed(code);
                }
            }
            @Override
            public void onException(Throwable exception) {
                lv("登录失败!exception=" + exception);
                if (callback != null){
                    callback.onException(exception);
                }
            }
        });
    }

    /**
     * 从我方服务器上拉取账号对应的云信token,并且使用该token登录云信
     *
     * @param account 要登录的账号
     * @param callback 登录结果回调,如果为null,则不处理回调
     */
    public void getTokenAndLogin(final String account , final RequestCallbackWrapper callback){
        if (account.equals("10000200") || account.equals("10000201") || account.equals("1000001714")){
            NetWorkManager.queryToken(account).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    try {
                        List<LinkedTreeMap> result = (List<LinkedTreeMap>) o;
                        String token = (String) result.get(0).get("token");
                        login(account , token , callback);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        if (callback != null){
                            callback.onException(e);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    Log.v("FH" , "获取token失败 : " + throwable.getMessage());
                    if (callback != null){
                        callback.onException(throwable);
                    }
                }
            });
        }
        else {
            Observable
                    .create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
                            //TODO 从我方服务器上拉取最新的account对应的token,接口暂时未实现,使用假数据
                            subscriber.onNext(SpUtil.getUserId() + "");
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String token) {
                            if (!TextUtils.isEmpty(token)){
                                login(account , token , callback);
                            }
                        }
                    });
        }
    }

    /**
     * 云信账号登出
     */
    public void logout(){
        reInitData();
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 把本实例下的所有数据全部初始化,监听器等等全部注销
     */
    public void reInitData(){
        //数据全部初始化,之前的数据全部销毁
        onRecentContactChangeListeners.clear();
        onTeamInfoChangeListeners.clear();
        onUserInfoChangeListeners.clear();
        onMyTeamListChangeListeners.clear();
        onTeamMemberChangedListeners.clear();
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, false);
        NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver , false);
        NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, false);
        NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(teamMemberRemoveObserver , false);
        NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(teamMemberUpdateObserver , false);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver , false);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incommingMessageObserver , false);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(msgSendStatusObserver, false);
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver , false);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver , false);

        recentContactList.clear();
        userInfoMap.clear();
        teamInfoMap.clear();
        groupMemberMap.clear();
        myTeamList.clear();

        teamDataInitFinish = false;
        recentContactInitFinish = false;
        currentAccount = null;
        currentOnlineStatus = null;
    }


    /**
     * 查询历史消息,只能查询给定锚点时间点之前的消息
     * @param sessionType 会话类型,可以为Team或者P2P
     * @param sessionId 会话ID(群ID或者其他用户ID)
     * @param limit 查询条数最大值
     * @param anchorTime 查询锚点时间,本方法只会查询给定锚点时间点之前的消息
     * @param callback 回调,查询结果会在callback中异步返回
     */
    public void queryHistoryMsgList (SessionTypeEnum sessionType , String sessionId, int  limit , long anchorTime
            , final RequestCallback<List<IMMessage>> callback){
        IMMessage anchor = MessageBuilder.createEmptyMessage(sessionId , sessionType, anchorTime);
        lv("开始查询历史消息 sstype : " + sessionType + " ssid : " + sessionId + " limit : " + limit + " anchorTime : " + anchorTime);
        NIMClient.getService(MsgService.class).queryMessageListEx(anchor, QueryDirectionEnum.QUERY_OLD, limit , true)
                .setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        // 解析有问题的自定义消息attachment为空,滤掉这类消息
                        // FIXME 在此处过滤消息会导致查询到的消息数目与给定的limit消息数目不符,暂时找不到更好的解决办法,期待以后修复
                        ListUtil.conditionalRemove(param, new ListUtil.ConditionJudger<IMMessage>() {
                            @Override
                            public boolean isMatchCondition(IMMessage nodeInList) {
                                return nodeInList.getMsgType() == MsgTypeEnum.custom
                                        && nodeInList.getAttachment() == null;
                            }
                        });
                        callback.onSuccess(param);
                    }
                    @Override
                    public void onFailed(int code) {
                        callback.onFailed(code);

                    }
                    @Override
                    public void onException(Throwable exception) {
                        callback.onException(exception);
                    }
                });
    }

    /**
     * 正常情况收到消息后附件会自动下载。如果下载失败，可调用该接口重新下载
     *
     * @param msg   附件所在的消息体
     * @param thumb 下载缩略图还是原文件。为true时，仅下载缩略图。<br>
     *              该参数仅对图片和视频类消息有效
     * @return AbortableFuture 调用跟踪。可设置回调函数，可中止下载操作
     */
    public AbortableFuture<Void> downloadAttachment(IMMessage msg, boolean thumb){
        return NIMClient.getService(MsgService.class).downloadAttachment(msg, thumb);
    }

    /**
     * 获取最近联系人列表
     * @return
     */
    public ArrayList<RecentContact> getRecentContactList() {
        return recentContactList;
    }

    /**
     * 通过群id获取群资料bundle
     * @param id 群id
     * @return 获取的bundle内容可以见 {@link YXClient#makeTeamInfoBundle(Team)},
     * 如果之前没有获取过该群的info,会立刻返回null,并且向sdk查询最新的群info,异步查询的结果通过onTeamInfoChangedListener通知.
     * 可以通过{@link ListenerManager#addOnTeamInfoChangeListener(OnThingsChangedListener)}绑定监听器
     */
    public Bundle getTeamInfo(String id){
        Bundle bundle = teamInfoMap.get(id);
        updateTeamInfo(id , false);
        lv("调用getTeamInfo , 返回" + bundle);
        return bundle;
    }

    /**
     * 通过群id获取群名称
     * @param id 群id
     * @return 群名称.
     * 如果之前没有获取过该群的name,会立刻返回null,并且向sdk查询最新的群name,异步查询的结果通过onTeamInfoChangedListener通知.
     * 可以通过{@link ListenerManager#addOnTeamInfoChangeListener(OnThingsChangedListener)}绑定监听器
     */
    public String getTeamNameByID(String id){
        Bundle bundle = getTeamInfo(id);
        if (bundle != null){
            return bundle.getSerializable(TEAM) == null ? null : ((Team)bundle.getSerializable(TEAM)).getName();
        }
        return null;
    }

    /**
     * 通过用户id获取用户资料bundle
     * @param id 用户id
     * @return 获取的bundle内容可以见 {@link YXClient#makeUserInfoBundle(String, String, String)}
     * 如果之前没有获取过该用户的info,会立刻返回null,并且向sdk查询最新的用户info,异步查询的结果通过onUserInfoChangedListener通知.
     * 可以通过{@link ListenerManager#addOnUserInfoChangeListener(OnThingsChangedListener)}绑定监听器
     */
    public Bundle getUserInfo(String id){
        Bundle bundle = userInfoMap.get(id);
        updateUserInfo(id , false);
        lv("调用getUserInfo , 返回" + bundle);
        return bundle;
    }

    /**
     * 通过用户id获取用户名称
     * @param id 用户id
     * @return 如果之前没有获取过该用户的name,会立刻返回null,并且向sdk查询最新的info,异步查询的结果通过onUserInfoChangedListener通知.
     * 可以通过{@link ListenerManager#addOnUserInfoChangeListener(OnThingsChangedListener)}绑定监听器
     */
    public String getUserNameByID(String id){
        Bundle bundle = getUserInfo(id);
        if (bundle != null){
            return bundle.getString(USER_NAME);
        }
        return null;
    }

    /**
     * 通过用户id获取用户头像path
     * @param id 用户id
     * @return 如果之前没有获取过该用户的avatar,会立刻返回null,并且向sdk查询最新的info,异步查询的结果通过onUserInfoChangedListener通知.
     * 可以通过{@link ListenerManager#addOnUserInfoChangeListener(OnThingsChangedListener)}绑定监听器
     */
    public String getUserAvatarByID(String id){
        Bundle bundle = getUserInfo(id);
        if (bundle != null){
            return bundle.getString(USER_AVATAR);
        }
        return null;
    }


    /**
     * 获取我加入的所有群的列表
     * @return
     */
    public ArrayList<Team> getMyTeamList(){
        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(new RequestCallbackWrapper<List<Team>>() {
                    @Override
                    public void onResult(int code, List<Team> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            lv("获取我加入的群列表成功,获取到" + (result == null ? result : result.size()) + "个加入的群");
                            myTeamList.clear();
                            myTeamList.addAll(result);
                            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                                listener.onThingChanged(myTeamList , ALL);
                            }
                        }
                    }
                });
        return myTeamList;
    }

    /**
     * 根据群id,查询群成员列表
     * @param id 群id
     * @return 如果之前没有查询过,会立即返回null.并且会异步向sdk查询该群成员列表,查询到的结果会通过已经注册的onTeamMemberChangedListener通知
     * 具体请见{@link ListenerManager#addOnTeamMemberChangeListener(OnThingsChangedListener)}
     */
    public List<TeamMember> getTeamMemberByID(final String id){
        lv("查询群id=" + id + "的成员列表");
        Pair<Long, List<TeamMember>> pair = groupMemberMap.get(id);
        if (pair == null){
            lv("之前没有查询过该群的成员列表,查询服务器 群id=" + id);
            updateTeamMember(id);
            return null;
        }
        else {
            long lastUpdateTimemill = pair.first;
            List<TeamMember> returnList = pair.sencond;
            if (System.currentTimeMillis() - lastUpdateTimemill > UPDATE_THRESHOLD){
                lv("该群成员列表过期,查询服务器 群id=" + id);
                updateTeamMember(id);
                return null;
            }
            else {
                lv("查到本机存有群id=" + id + "的群成员列表 size :" + returnList.size());
                return returnList;
            }
        }
    }






    public void sendQuestion(String id , SessionTypeEnum typeEnum , String msg){
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(id);
        notification.setSessionType(typeEnum);
        JSONObject json = new JSONObject();
        try {
            json.put("id" , "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notification.setContent(msg);
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }








    /**
     * 发送文字消息
     * @param id 发送的对象id
     * @param typeEnum  发送的对象类型,可以是群或个人
     * @param msg 发送的消息文字
     * @return 实际发送的消息体
     */
    public IMMessage sendTextMessage(String id , SessionTypeEnum typeEnum , String msg , final OnErrorListener<IMMessage> onErrorListener){
        lv("发送文字消息,对方id=" + id + " type=" + typeEnum + " msg=" + msg);
        if (TextUtils.isEmpty(msg)){
            lv("要发送的文字消息内容为空,取消发送");
            return null;
        }
        final IMMessage message;
        switch (typeEnum){
            case P2P:
                message = MessageBuilder.createTextMessage(id , SessionTypeEnum.P2P, msg.trim());
                break;
            case Team:
                message = MessageBuilder.createTextMessage(id , SessionTypeEnum.Team , msg.trim());
                break;
            default:
                lv("发送对象的type不支持,取消发送,type=" + typeEnum);
                return null;
        }
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableRoaming = true;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).sendMessage(message , true).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                lv("发送消息的回调 : code " + code);
                if (code == 802){
                    if (onErrorListener != null){
                        onErrorListener.onError(code , message);
                    }
                }
            }
        });
        return message;
    }

    /**
     * 发送文字消息给多个id(群发)
     * @param idList 目标id列表
     * @param msg 文字消息
     * @return 实际发送的消息体
     */
    public ArrayList<IMMessage> sendTextMessage(ArrayList<String> idList, String msg
            , final OnErrorListener<IMMessage> onErrorListener){
        lv("发送文字消息,对方id=" + idList + " msg=" + msg);
        if (TextUtils.isEmpty(msg)){
            lv("要发送的文字消息内容为空,取消发送");
            return null;
        }
        ArrayList<IMMessage> returnList = new ArrayList<IMMessage>();
        for (String id : idList) {
            final IMMessage message = MessageBuilder.createTextMessage(id , SessionTypeEnum.P2P, msg.trim());
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableRoaming = true;
            message.setConfig(config);
            NIMClient.getService(MsgService.class).sendMessage(message , true).setCallback(new RequestCallbackWrapper<Void>() {
                @Override
                public void onResult(int code, Void result, Throwable exception) {
                    if (code == 802){
                        onErrorListener.onError(code , message);
                    }
                }
            });
            returnList.add(message);
        }
        return returnList;
    }



    /**
     * 检查WiFi是否打开,并且尝试刷新式登录
     * @param activity 上下文环境,必须是activity,用于获取window在出现错误的时候弹出dialog提示.
     * @param onRefreshSuccessRunnable wifi打开并且刷新式登录成功后的回调
     * @param keyPointController 关键节点控制器,可以自己定义在检查前,成功时,失败时的动作.
     *                           如果为null,则会按默认逻辑,使用{@link CheckNetDefaultKPController}
     *                           检查成功时调用顺序:keyPointController.before-->keyPointController.onSuccess-->onRefreshSuccessRunnable.run
     */
    public static void checkNetAndRefreshLogin(Activity activity , final Runnable onRefreshSuccessRunnable , KeyPointController<Object , Object , Integer> keyPointController) {
        final KeyPointController mKeyPointController;
        if (keyPointController == null) {
            mKeyPointController = new CheckNetDefaultKPController(activity , false);
        } else {
            mKeyPointController = keyPointController;
        }
        mKeyPointController.before(null);
        if (!NetUtils.isNetConnected()) {
            mKeyPointController.onFail(-999);
            return;
        }
        YXClient.getInstance().getTokenAndLogin(String.valueOf(SpUtil.getUserId()), new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    Log.v("FH", "刷新式登录成功");
                    mKeyPointController.onSuccess(null);
                    if (onRefreshSuccessRunnable != null){
                        onRefreshSuccessRunnable.run();
                    }
                } else {
                    Log.v("FH", "刷新式登录失败 code :　" + code);
                    mKeyPointController.onFail(code);
                }
            }
        });
    }

    /**
     * 获取当前在线状态
     * @return
     */
    public StatusCode getCurrentOnlineStatus(){
        lv("当前在线状态为" + currentOnlineStatus);
        return currentOnlineStatus;
    }

    /**
     * 注册各种关于群成员结构信息变动的全局监听器,并且获取我的群列表
     */
    private void initTeamData(){
        lv("正在初始化我加入的群列表");
        // 注册群资料变动观察者
        NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver , true);
        // 注册群组被移除的观察者。在退群，被踢，群被解散时会收到该通知。
        NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, true);
        // 群成员资料变化观察者通知。群组添加新成员，成员资料变化会收到该通知。
        NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(teamMemberUpdateObserver , true);
        // 注册移除群成员的观察者通知。
        NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(teamMemberRemoveObserver , true);

        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(new RequestCallbackWrapper<List<Team>>() {
                    @Override
                    public void onResult(int code, List<Team> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            lv("获取我加入的群列表成功,获取到" + (result == null ? result : result.size()) + "个加入的群");
                            for (Team team : result) {
                                teamInfoMap.put(team.getId() , makeTeamInfoBundle(team));
                                getTeamMemberByID(team.getId());
                            }
                            myTeamList.addAll(result);
                            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                                listener.onThingChanged(result , ALL);
                            }
                            teamDataInitFinish = true;
                        } else {
                            lv("获取我加入的群列表失败, code : " + code + "  exception : " + exception);
                            //注销群相关的监听器
                            NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver , false);
                            NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, false);
                            NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(teamMemberUpdateObserver , false);
                            NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(teamMemberRemoveObserver , false);
                        }
                    }
                });
    }

    /**
     * 注册最近联系人列表变动监听器,获取最近联系人列表
     */
    private void initRecentContact() {
        lv("正在初始化最近联系人列表...");
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, true);
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    lv("获取最近联系人列表成功,获取到" + (result == null ? result : result.size()) + "个最近联系人(包含群)");
                    for (RecentContact newContact : result) {
                        if (newContact.getSessionType() == SessionTypeEnum.Team){
                            //滤掉群消息的最近联系人
                            continue;
                        }
                        String id = newContact.getContactId();
                        if (id.length() == 10 || id.length() == 6){
                            //学生端10位id的其他学生和6位id的管理员都不需要显示在最近通话中,此处滤掉
                            continue;
                        }
                        updateUserInfo(id , true);
                        recentContactList.add(newContact);
                    }
                    recentContactInitFinish = true;
                } else {
                    lv("获取最近联系人列表失败, code : " + code + "  exception : " + exception);
                    NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, false);
                }
            }
        });
    }

    /**
     * 从云端获取群成员列表,并且通知UI注册的监听器,通知的是全体成员的列表(ALL),列表以Pair<群id , List<{@link TeamMember}>>的格式通知
     * @param id 群id
     */
    private void updateTeamMember(final String id){
        NIMClient.getService(TeamService.class).queryMemberList(id).setCallback(new RequestCallback<List<TeamMember>>() {
            @Override
            public void onSuccess(List<TeamMember> param) {
                lv("从服务器查询群成员列表成功 : size : " + param.size() + "  群id : " + (param.size() == 0 ? null : param.get(0).getTid()));
                //群成员列表需要滤除10位的其他学生的号还有6位的管理员号
                ListUtil.conditionalRemove(param,
                        new ListUtil.ConditionJudger<TeamMember>() {
                            @Override
                            public boolean isMatchCondition(TeamMember nodeInList) {
                                return nodeInList.getAccount().length() == 6
                                        || (nodeInList.getAccount().length() == 10 && !nodeInList.getAccount().equals(SpUtil.getUserId()+""));
                            }
                        }
                );
                groupMemberMap.put(id , new Pair<Long, List<TeamMember>>(System.currentTimeMillis() , param));
                Pair pair = new Pair<String, List<TeamMember>>(id , param);
                for (OnThingsChangedListener<Pair<String, List<TeamMember>>> listener : onTeamMemberChangedListeners) {
                    listener.onThingChanged(pair , ALL);
                }
            }
            @Override
            public void onFailed(int code) {}
            @Override
            public void onException(Throwable exception) {}
        });
    }

    private void updateUserInfo(final String id){
        Bundle bundle = userInfoMap.get(id);
        if (bundle == null || bundle.getString(id) == null) {
            lv("发现之前没有载入过该用户资料,开始从sdk数据库获取用户资料...id=" + id);
            NimUserInfo nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(id);
            if (nimUserInfo != null) {
                if (bundle != null){
                    boolean isFetching = bundle.getBoolean(IS_FETCHING);
                    bundle = makeUserInfoBundle(id , nimUserInfo.getName() , nimUserInfo.getAvatar());
                    bundle.putBoolean(IS_FETCHING , isFetching);
                }
                else {
                    bundle = makeUserInfoBundle(id , nimUserInfo.getName() , nimUserInfo.getAvatar());
                }
                userInfoMap.put(id , bundle);
                lv("从sdk载入用户资料成功,id=" + id + " userName=" + nimUserInfo.getName() + " userAvatarPath=" + nimUserInfo.getAvatar());
                for (OnThingsChangedListener<Bundle> listener : onUserInfoChangeListeners) {
                    listener.onThingChanged(bundle , ALL);
                }
            }
            else {
                lv("从sdk载入用户资料失败,id=" + id);
            }
        }
        lv("开始后台网络更新用户资料 id=" + id);
        changeUserInfoFetchingStatus(id, true);
        pullUserInfo(id, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                String userName = param.get(0).getName();
                String userAvatarPath = param.get(0).getAvatar();
                lv("后台网络更新用户资料成功,id=" + id + " userName=" + userName + " userAvatarPath=" + userAvatarPath);
                Bundle tempBundle = makeUserInfoBundle(id, userName, userAvatarPath);
                userInfoMap.put(id, tempBundle);
                for (OnThingsChangedListener<Bundle> listener : onUserInfoChangeListeners) {
                    listener.onThingChanged(tempBundle , ALL);
                }
                changeUserInfoFetchingStatus(id, false);
            }

            @Override
            public void onFailed(int code) {
                lv("后台网络更新用户资料失败,id=" + id + " code=" + code);
                changeUserInfoFetchingStatus(id, false);
            }

            @Override
            public void onException(Throwable exception) {
                lv("后台网络更新用户资料失败,id=" + id + " exception=" + exception);
                changeUserInfoFetchingStatus(id, false);
            }
        });
    }
    private void updateUserInfo(final String id , boolean forceUpdate) {
        lv("调用更新用户资料 id=" + id + " forceUpdate=" + forceUpdate);
        if (TextUtils.isEmpty(id)) {
            lv("调用更新用户资料,失败,因为id为空");
            return;
        }
        if (forceUpdate){
            lv("要求强制更新用户资料 id=" + id);
            updateUserInfo(id);
        }
        else {
            Bundle bundle = userInfoMap.get(id);
            if (bundle == null){
                lv("用户资料不存在于本地,需要更新 id=" + id);
                updateUserInfo(id);
            }
            else if (bundle.getBoolean(IS_FETCHING)){
                lv("已经有其他的请求正在更新这个用户数据,本次请求取消...id=" + id);
            }
            else {
                long lastUpdate = bundle.getLong(LAST_UPDATE , -1);
                if (lastUpdate < 0 || System.currentTimeMillis() - lastUpdate > UPDATE_THRESHOLD){
                    lv("用户资料已经过期,需要更新 id=" + id);
                    updateUserInfo(id);
                }
                else {
                    lv("用户资料没有过期,不需要更新 id=" + id);
                }
            }
        }
    }

    private void pullUserInfo(final String id, RequestCallback<List<NimUserInfo>> callback) {
        NIMClient.getService(UserService.class)
                .fetchUserInfo(new ArrayList<String>() {{
                    add(id);
                }})
                .setCallback(callback);
    }

    /**
     * 生成一个用户资料bundle
     *
     * @param id 用户id
     * @param userName 用户名称
     * @param avatarPath 用户头像路径
     *
     * @return 用户资料bundle,其中包含
     * <p>用户id,使用bundle.getString({@link YXClient#ID})
     * <p>用户名称 使用bundle.getString({@link YXClient#USER_NAME})
     * <p>用户头像路径 使用bundle.getString({@link YXClient#USER_AVATAR})
     * <p>该条用户资料最后更新时间戳 使用bundle.getLong({@link YXClient#LAST_UPDATE})获取
     */
    private Bundle makeUserInfoBundle(String id , String userName , String avatarPath){
        Bundle bundle = new Bundle();
        bundle.putString(ID , id);
        bundle.putString(USER_NAME , userName);
        bundle.putString(USER_AVATAR , avatarPath);
        bundle.putLong(LAST_UPDATE , System.currentTimeMillis());
        return bundle;
    }

    private void changeUserInfoFetchingStatus(String id , boolean isFetching){
        Bundle bundle = userInfoMap.get(id);
        if (isFetching){
            if (bundle == null){
                bundle = makeUserInfoBundle(null , null , null);
                userInfoMap.put(id , bundle);
            }
            bundle.putBoolean(IS_FETCHING , isFetching);
        }
        else {
            if (bundle != null){
                if (bundle.getString(ID) == null) bundle.remove(id);
                else {
                    bundle.putBoolean(IS_FETCHING , isFetching);
                }
            }
        }
    }

    private void updateTeamInfo(final String id){
        lv("开始后台网络更新群资料 id=" + id);
        changeTeamInfoFetchingStatus(id , true);
        pullTeamInfo(id, new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team param) {
                String teamName = param.getName();
                lv("后台网络更新群资料成功,id=" + id + " teamName=" + teamName);
                Bundle tempBundle = makeTeamInfoBundle(param);
                teamInfoMap.put(id, tempBundle);
                for (OnThingsChangedListener<Bundle> listener : onTeamInfoChangeListeners) {
                    listener.onThingChanged(tempBundle , ALL);
                }
                changeTeamInfoFetchingStatus(id, false);
            }
            @Override
            public void onFailed(int code) {
                lv("后台网络更新群资料失败,id=" + id + " code=" + code);
                changeTeamInfoFetchingStatus(id, false);
            }
            @Override
            public void onException(Throwable exception) {
                lv("后台网络更新群资料失败,id=" + id + " exception=" + exception);
                changeTeamInfoFetchingStatus(id, false);
            }
        });
    }
    private void updateTeamInfo(final String id , boolean forceUpdate) {
        lv("调用更新群资料 id=" + id + " forceUpdate=" + forceUpdate);
        if (TextUtils.isEmpty(id)) {
            lv("调用更新群资料,失败,因为id为空");
            return;
        }
        if (forceUpdate){
            lv("要求强制更新群资料 id=" + id);
            updateTeamInfo(id);
        }
        else{
            Bundle bundle = teamInfoMap.get(id);
            if (bundle == null){
                lv("群资料不存在于本地,需要更新 id=" + id);
                updateTeamInfo(id);
            }
            else if (bundle.getBoolean(IS_FETCHING)){
                lv("已经有其他的请求正在更新这个群数据,本次请求取消...id=" + id);
            }
            else {
                long lastUpdate = bundle.getLong(LAST_UPDATE , -1);
                if (lastUpdate < 0 || System.currentTimeMillis() - lastUpdate > UPDATE_THRESHOLD){
                    lv("群资料已经过期,需要更新 id=" + id);
                    updateTeamInfo(id);
                }
                else {
                    lv("群资料没有过期,不需要更新 id=" + id);
                }
            }
        }
    }

    /**
     * 提供直接查询群信息功能.
     * 注意
     * 注意
     * 注意
     * 直接调用本方法将不会触发各种监听器,也不会缓存到本地的缓存中,如果需要触发各种业务相关的监听器,可以使用
     * {@link YXClient#getTeamInfo(String)}
     * @param id
     * @param callback
     */
    public synchronized void pullTeamInfo(final String id, RequestCallback<Team> callback) {
        NIMClient.getService(TeamService.class).searchTeam(id)
                .setCallback(callback);
    }


    private void changeTeamInfoFetchingStatus(String id , boolean isFetching){
        Bundle bundle = teamInfoMap.get(id);
        if (isFetching){
            if (bundle == null){
                bundle = makeTeamInfoBundle(null);
                teamInfoMap.put(id , bundle);
            }
            bundle.putBoolean(IS_FETCHING , isFetching);
        }
        else {
            if (bundle != null){
                if (bundle.getString(ID) == null) bundle.remove(id);
                else {
                    bundle.putBoolean(IS_FETCHING , isFetching);
                }
            }
        }
    }

    /**
     * 生成一个群资料bundle
     *
     * @param team 群资料
     *
     * @return 群资料bundle,其中包含
     * <p>群信息 使用bundle.getSerializable({@link YXClient#TEAM})
     * <p>该条群资料最后更新时间戳 使用bundle.getLong({@link YXClient#LAST_UPDATE})获取
     */
    private Bundle makeTeamInfoBundle(Team team){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TEAM , team);
        bundle.putLong(LAST_UPDATE , System.currentTimeMillis());
        return bundle;
    }

    private void lv(String msg) {
        Log.v("FHH", msg);
    }

    /**
     * YxClient中的监听器管理器,会自动释放
     */
    public class ListenerManager {
        //本Manage管理的用户信息更新监听器列表
        public ArrayList<OnThingsChangedListener<Bundle>> myOnUserInfoChangeListeners
                = new ArrayList<OnThingsChangedListener<Bundle>>();
        //本Manage管理的群信息更新监听器列表
        public ArrayList<OnThingsChangedListener<Bundle>> myOnTeamInfoChangeListeners
                = new ArrayList<OnThingsChangedListener<Bundle>>();
        //本Manage管理的最近联系人更新监听器列表
        public ArrayList<OnThingsChangedListener<List<RecentContact>>> myOnRecentContactChangeListeners
                = new ArrayList<OnThingsChangedListener<List<RecentContact>>>();
        //本Manage管理的我加入的群更新监听器列表
        public ArrayList<OnThingsChangedListener<List<Team>>> myOnMyTeamListChangeListeners
                = new ArrayList<OnThingsChangedListener<List<Team>>>();
        //本Manage管理的群成员变更监听器列表
        public ArrayList<OnThingsChangedListener<Pair<String , List<TeamMember>>>> myOnTeamMemberChangedListeners
                = new ArrayList<OnThingsChangedListener<Pair<String,List<TeamMember>>>>();
        //本Manage管理的自定义的新到消息监听器列表
        public ArrayList<OnMessageListener> myOnNewMessageListenerList = new ArrayList<OnMessageListener>();
        //本Manage管理的自定义的消息发送状态监听器列表
        public ArrayList<OnMessageListener> myOnMsgStatusChangedListenerList = new ArrayList<OnMessageListener>();
        /**
         * 添加最近联系人列表变化监听器
         * @param listener 监听器通知UI的内容类型为ALL,即为变动后的整个最近联系人列表
         */
        public void addOnRecentContactListChangeListener(OnThingsChangedListener<List<RecentContact>> listener){
            onRecentContactChangeListeners.add(listener);
            myOnRecentContactChangeListeners.add(listener);
        }

        /**
         * 移除最近联系人列表变化监听器
         * @param listener
         */
        public void removeOnRecentContactListChangeListener(OnThingsChangedListener<List<RecentContact>> listener){
            onRecentContactChangeListeners.remove(listener);
            myOnRecentContactChangeListeners.remove(listener);
        }

        /**
         * 添加用户资料变化监听器,
         * @param listener 目前监听器通知的内容类型为ALL.通知列表中只有一个变动后的用户资料Bundle
         *                 具体内容可以见{@link YXClient#makeUserInfoBundle(String, String, String)}
         */
        public void addOnUserInfoChangeListener(OnThingsChangedListener<Bundle> listener){
            onUserInfoChangeListeners.add(listener);
            myOnUserInfoChangeListeners.add(listener);
        }

        /**
         * 移除用户资料变化监听器
         * @param listener
         */
        public void removeOnUserInfoChangeListener(OnThingsChangedListener<Bundle> listener){
            onUserInfoChangeListeners.remove(listener);
            myOnUserInfoChangeListeners.remove(listener);
        }

         /**
         * 添加群资料变化监听器,
         * @param listener 目前监听器通知的内容类型为ALL.通知列表中只有一个变动后的群资料Bundle
         *                 具体内容可以见{@link YXClient#makeTeamInfoBundle(Team)}
         */
        public void addOnTeamInfoChangeListener(OnThingsChangedListener<Bundle> listener){
            onTeamInfoChangeListeners.add(listener);
            myOnTeamInfoChangeListeners.add(listener);
        }

        /**
         * 移除群资料变化监听器
         * @param listener
         */
        public void removeOnTeamInfoChangeListener(OnThingsChangedListener<Bundle> listener){
            onTeamInfoChangeListeners.remove(listener);
            myOnTeamInfoChangeListeners.remove(listener);
        }

        /**
         * 添加我加入的群列表变化监听器,
         * @param listener 监听器通知的内容类型有整个群列表(ALL),群列表增加(NEW),群列表减少(DELETE)
         * 内容分别为整个群列表,增加的群的list,减少的群的list
         */
        public void addOnMyTeamListChangeListener(OnThingsChangedListener<List<Team>> listener){
            onMyTeamListChangeListeners.add(listener);
            myOnMyTeamListChangeListeners.add(listener);
        }

        /**
         * 移除我加入的群列表变化监听器
         * @param listener
         */
        public void removeOnMyTeamListChangeListener(OnThingsChangedListener<List<Team>> listener){
            onMyTeamListChangeListeners.remove(listener);
            myOnMyTeamListChangeListeners.remove(listener);
        }

        /**
         * 添加群成员变化监听器,
         * @param listener 监听器通知的内容类型有整个群的成员列表全部(ALL),新增成员的list(NEW),减少成员的list(DELETE)
         * 返回的数据格式为{@link Pair}类型的的数据,pair.first为成员变化的群的id号,pair.second为上述对应的列表
         */
        public void addOnTeamMemberChangeListener(OnThingsChangedListener<Pair<String, List<TeamMember>>> listener){
            onTeamMemberChangedListeners.add(listener);
            myOnTeamMemberChangedListeners.add(listener);
        }



        /**
         * 移除群成员变化监听器
         * @param listener
         */
        public void removeOnTeamMemberChangeListener(OnThingsChangedListener<Pair<String, List<TeamMember>>> listener){
            onTeamMemberChangedListeners.remove(listener);
            myOnTeamMemberChangedListeners.remove(listener);
        }

        /**
         * 添加新到消息监听器
         * @param listener 通知内容为新到的{@link IMMessage},不会通知群消息(已屏蔽)
         */
        public void addOnNewMessageListener(OnMessageListener listener){
            onNewMessageListenerList.add(listener);
            myOnNewMessageListenerList.add(listener);
        }
        /**
         * 移除新到消息监听器
         * @param listener
         */
        public void removeOnNewMessageListener(OnMessageListener listener){
            onNewMessageListenerList.remove(listener);
            myOnNewMessageListenerList.remove(listener);
        }

        /**
         * 添加消息发送状态变化监听器
         * @param listener 通知内容为新到的{@link IMMessage},包含所有类型
         */
        public void addOnMsgStatusChangedListener(OnMessageListener listener){
            onMsgStatusChangedListenerList.add(listener);
            myOnMsgStatusChangedListenerList.add(listener);
        }

        /**
         * 移除消息发送状态变化监听器
         * @param listener
         */
        public void removeOnMsgStatusChangedListener(OnMessageListener listener){
            onMsgStatusChangedListenerList.remove(listener);
            myOnMsgStatusChangedListenerList.remove(listener);
        }
    }

    /**
     * 为了实现使用{@link YXClient#with(Activity)}函数来达到UI注册各种监听器时,监听器的生命周期会根据activity的生命周期同步
     * ,会在activity Destory的时候自动注销的功能.
     * 需要使用一个空的fragment添加到activity中以获取activity的回调节点.
     */
    public static class EmptyFragment extends Fragment {
        public LifeCycle lifeCycle;

        public EmptyFragment setlifeCycle(LifeCycle lifeCycle) {
            this.lifeCycle = lifeCycle;
            return this;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (lifeCycle != null){
                lifeCycle.onStart();
            }
        }
        @Override
        public void onResume() {
            super.onResume();
            if (lifeCycle != null){
                lifeCycle.onResume();
            }
        }
        @Override
        public void onPause() {
            super.onPause();
            if (lifeCycle != null){
                lifeCycle.onPause();
            }
        }
        @Override
        public void onStop() {
            super.onStop();
            if (lifeCycle != null){
                lifeCycle.onStop();
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            if (lifeCycle != null){
                lifeCycle.onDestroy();
            }
        }
    }

    public abstract class LifeCycle{
        private ListenerManager manager;
        public ListenerManager getManager() {
            return manager;
        }
        public LifeCycle setManager(ListenerManager manager) {
            this.manager = manager;
            return this;
        }

        abstract void onStart();
        abstract void onResume();
        abstract void onPause();
        abstract void onStop();
        abstract void onDestroy();
    }

    //OnThingsChangedListener用常量
    public static final int NEW = 1;//代表变化为新增元素,传递的thing是新增的那部分元素
    public static final int DELETE = 2;//代表变化为删除元素,传递的thing是删除的那部分元素
    public static final int ALL = 3;//代表变化为数据整体改变,传递的thing是变化后的数据整体

    /**
     * 对象改变监听器
     * @param <T>
     */
    public interface OnThingsChangedListener<T> {
        void onThingChanged(T thing , int type);
    }

    public interface OnMessageListener {
        void onNewMessage(IMMessage message);
    }

    public interface KeyPointController<A , B , C>{
        void before(A data);
        void onSuccess(B data);
        void onFail(C data);
    }

    /**
     * 错误监听器
     * @param <D>
     */
    public interface OnErrorListener<D>{
        void onError(int code , D data);
    }

    /**
     * 检查网络时默认的处理逻辑
     * 检查前使用HintDialog提示(showLoadingDialog为true的情况下),成功后无动作,失败后用ConfirmDialog提示用户
     *
     */
    public static class CheckNetDefaultKPController implements KeyPointController<Object, Object, Integer>  {
        Activity activity;
        boolean showLoadingDialog;
        public CheckNetDefaultKPController(Activity activity , boolean showLoadingDialog) {
            this.activity = activity;
            this.showLoadingDialog = showLoadingDialog;
            hintDialog = new HintDialog(activity, "请稍候,检测网络状态中...");
        }

        HintDialog hintDialog ;
        @Override
        public void before(Object data) {
            if (showLoadingDialog){
                hintDialog.show();
            }
        }

        @Override
        public void onSuccess(Object data) {
            if (hintDialog.isShowing()){
                hintDialog.dismiss();
            }
        }

        @Override
        public void onFail(Integer data) {
            if (hintDialog.isShowing()){
                hintDialog.dismiss();
            }
            if (data == -999) {
                hintDialog.dismiss();
                new ConfirmDialog(activity, "当前的wifi没有打开,无法接收新的消息,是否打开wifi?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
                        activity.startActivity(intent);
                        dialog.dismiss();
                    }
                }, "打开").show();
            } else {
                new ConfirmDialog(activity, "已经与消息服务器断开连接(" + data + "),是否重连?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkNetAndRefreshLogin(activity , null , CheckNetDefaultKPController.this);
                        dialog.dismiss();
                    }
                }, "重新连接").show();
            }
        }
    };

}
