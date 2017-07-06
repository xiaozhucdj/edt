package com.yougy.message;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.yougy.shop.bean.BookInfo;

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
    static public final String TEAM_NAME = "team_name";
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


    //用户信息更新监听器列表
    private ArrayList<OnThingsChangedListener<Bundle>> onUserInfoChangeListeners
            = new ArrayList<OnThingsChangedListener<Bundle>>();
    //群信息更新监听器列表
    private ArrayList<OnThingsChangedListener<Bundle>> onTeamInfoChangeListeners
            = new ArrayList<OnThingsChangedListener<Bundle>>();
    //最近联系人更新监听器列表
    private ArrayList<OnThingsChangedListener<List<RecentContact>>> onRecentContactChangeListeners
            = new ArrayList<OnThingsChangedListener<List<RecentContact>>>();
    //我加入的群更新监听器列表
    private ArrayList<OnThingsChangedListener<List<Team>>> onMyTeamListChangeListeners
            = new ArrayList<OnThingsChangedListener<List<Team>>>();
    //群成员变更监听器列表
    private ArrayList<OnThingsChangedListener<Pair<String , List<TeamMember>>>> onTeamMemberChangedListeners
            = new ArrayList<OnThingsChangedListener<Pair<String,List<TeamMember>>>>();

    //在线状态变更观察者
    private Observer<StatusCode> onlineStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            currentOnlineStatus = statusCode;
            Log.v("FH" , "onlineStatus 变更: " + statusCode);
            if (statusCode == StatusCode.PWD_ERROR){
                if (!TextUtils.isEmpty(currentAccount)){
                    getTokenAndLogin(currentAccount , null);
                }
            }
            else if (statusCode == StatusCode.KICKOUT){

            }
        }
    };
    //最近联系人变更观察者
    private Observer<List<RecentContact>> recentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> newRecentContactList) {
            lv("收到最近联系人变更 " + newRecentContactList.size());
            for (RecentContact newRecentContact : newRecentContactList) {
                for (int i = 0 ; i < recentContactList.size() ;) {
                    RecentContact oldContact = recentContactList.get(i);
                    if (oldContact.getContactId().equals(newRecentContact.getContactId())) {
                        recentContactList.remove(oldContact);
                        break;
                    }
                    else {
                        i++;
                    }
                }
                switch (newRecentContact.getSessionType()){
                    case P2P:
                        updateUserInfo(newRecentContact.getContactId() , false);
                        break;
                    case Team:
                        updateTeamInfo(newRecentContact.getContactId() , false);
                        break;
                }
            }
            recentContactList.addAll(0 , newRecentContactList);
            for (OnThingsChangedListener<List<RecentContact>> listener : onRecentContactChangeListeners) {
                listener.onThingChanged(newRecentContactList);
            }
        }
    };
    //群组资料变更观察者
    private Observer<List<Team>> teamUpdateObserver = new Observer<List<Team>>() {
        @Override
        public void onEvent(List<Team> newTeams) {
            lv("收到群组资料变更 " + newTeams.size());
            for (Team newTeam: newTeams) {
                for (int i = 0 ; i < myTeamList.size() ;) {
                    Team oldTeam = myTeamList.get(i);
                    if (oldTeam.getId().equals(newTeam.getId())) {
                        myTeamList.remove(oldTeam);
                        break;
                    }
                    else {
                        i++;
                    }
                }
                teamInfoMap.put(newTeam.getId() , makeTeamInfoBundle(newTeam.getId() , newTeam.getName()));
            }
            myTeamList.addAll(0, newTeams);
            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                listener.onThingChanged(newTeams);
            }
        }
    };
    //自己退群,被移除出群观察者
    private Observer<Team> teamRemoveObserver = new Observer<Team>() {
        @Override
        public void onEvent(final Team quitedTeam) {
            lv("收到退群通知");
            for (int i = 0 ; i < myTeamList.size() ;) {
                Team oldTeam = myTeamList.get(i);
                if (oldTeam.getId().equals(quitedTeam.getId())){
                    myTeamList.remove(oldTeam);
                }
            }
            ArrayList<Team> list = new ArrayList<Team>(){{add(quitedTeam);}};
            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                listener.onThingChanged(list);
            }
        }
    };
    //群成员被移除观察者
    private Observer<TeamMember> teamMemberRemoveObserver = new Observer<TeamMember>(){
        @Override
        public void onEvent(TeamMember teamMember) {
            lv("收到群成员被移除通知" + teamMember);
            getTeamMemberByID(teamMember.getTid());
        }
    };
     //消息过滤器
    private IMMessageFilter messageFilter = new IMMessageFilter() {
        @Override
        public boolean shouldIgnore(IMMessage message) {
            //过滤通知类消息
            if (message.getMsgType() == MsgTypeEnum.notification){
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
        NIMClient.getService(MsgService.class).registerIMMessageFilter(getInstance().messageFilter);
        //注册自定义消息解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(customAttachParser);
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
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        //TODO 从我方服务器上拉取最新的account对应的token,接口暂时未实现,使用假数据
                        subscriber.onNext("123456");
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
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver , false);

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
     * 获取最近联系人列表
     * @return
     */
    public ArrayList<RecentContact> getRecentContactList() {
        return recentContactList;
    }

    /**
     * 通过群id获取群资料bundle
     * @param id 群id
     * @return 获取的bundle内容可以见 {@link YXClient#makeTeamInfoBundle(String, String)},
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
            return bundle.getString(TEAM_NAME);
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
                return returnList;
            }
        }
    }

    /**
     * 发送文字消息
     * @param id 发送的对象id
     * @param typeEnum  发送的对象类型,可以是群或个人
     * @param msg 发送的消息文字
     * @return 实际发送的消息体
     */
    public IMMessage sendTextMessage(String id , SessionTypeEnum typeEnum , String msg){
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
        NIMClient.getService(MsgService.class).sendMessage(message , true);
        return message;
    }

    /**
     * 发送文字消息给多个id(群发)
     * @param idList 目标id列表
     * @param msg 文字消息
     * @return 实际发送的消息体
     */
    public ArrayList<IMMessage> sendTextMessage(ArrayList<String> idList , String msg){
        lv("发送文字消息,对方id=" + idList + " msg=" + msg);
        if (TextUtils.isEmpty(msg)){
            lv("要发送的文字消息内容为空,取消发送");
            return null;
        }
        ArrayList<IMMessage> returnList = new ArrayList<IMMessage>();
        for (String id : idList) {
            IMMessage message = MessageBuilder.createTextMessage(id , SessionTypeEnum.P2P, msg.trim());
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableRoaming = true;
            message.setConfig(config);
            NIMClient.getService(MsgService.class).sendMessage(message , true);
            returnList.add(message);
        }
        return returnList;
    }

    /**
     * 发送图书推荐消息
     * @param id 发送的对象id
     * @param typeEnum  发送的对象类型,可以是群或个人
     * @param bookInfo 推荐的图书信息
     * @param msg 推荐信息
     * @return 实际发送的消息体
     */
    public IMMessage sendBookRecommandMessage(String id , SessionTypeEnum typeEnum , BookInfo bookInfo , String msg){
        lv("发送图书推荐消息,对方id=" + id + " type=" + typeEnum + " bookInfo=" + bookInfo + "  msg= " + msg);
        final IMMessage message;
        switch (typeEnum){
            case P2P:
                message = MessageBuilder.createCustomMessage(id , SessionTypeEnum.P2P, "[图书推荐]" , new BookRecommandAttachment(msg , bookInfo));
                break;
            case Team:
                message = MessageBuilder.createCustomMessage(id , SessionTypeEnum.Team , "[图书推荐]" , new BookRecommandAttachment(msg , bookInfo));
                break;
            default:
                lv("发送对象的type不支持,取消发送,type=" + typeEnum);
                return null;
        }
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableRoaming = true;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).sendMessage(message , true);
        return message;
    }

    /**
     * 发送图书推荐消息给多个ID(群发)
     * @param idList 发送的对象id列表
     * @param typeEnum  发送的对象类型,可以是群或个人
     * @param bookInfo 推荐的图书信息
     * @param msg 推荐信息
     * @return 实际发送的消息体列表
     */
    public ArrayList<IMMessage> sendBookRecommandMessage(ArrayList<String> idList , SessionTypeEnum typeEnum , BookInfo bookInfo , String msg){
        ArrayList<IMMessage> returnList = new ArrayList<IMMessage>();
        lv("发送图书推荐消息,对方id=" + idList + " type=" + typeEnum + " bookInfo=" + bookInfo + "  msg= " + msg);
        for (String id : idList) {
            final IMMessage message;
            switch (typeEnum){
                case P2P:
                    message = MessageBuilder.createCustomMessage(id , SessionTypeEnum.P2P, "[图书推荐]" , new BookRecommandAttachment(msg , bookInfo));
                    break;
                case Team:
                    message = MessageBuilder.createCustomMessage(id , SessionTypeEnum.Team , "[图书推荐]" , new BookRecommandAttachment(msg , bookInfo));
                    break;
                default:
                    lv("发送对象的type不支持,取消发送,type=" + typeEnum);
                    return null;
            }
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableRoaming = true;
            message.setConfig(config);
            NIMClient.getService(MsgService.class).sendMessage(message , true);
            returnList.add(message);
        }
        return returnList;
    }


    /**
     * 获取当前在线状态
     * @return
     */
    public StatusCode getCurrentOnlineStatus(){
        lv("当前在线状态为" + currentOnlineStatus);
        return currentOnlineStatus;
    }

    private void initTeamData(){
        lv("正在初始化我加入的群列表");
        NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver , true);
        NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, true);
        NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(teamMemberRemoveObserver , true);

        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(new RequestCallbackWrapper<List<Team>>() {
                    @Override
                    public void onResult(int code, List<Team> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            lv("获取我加入的群列表成功,获取到" + (result == null ? result : result.size()) + "个加入的群");
                            for (Team team : result) {
                                teamInfoMap.put(team.getId() , makeTeamInfoBundle(team.getId() , team.getName()));
                                getTeamMemberByID(team.getId());
                            }
                            myTeamList.addAll(result);
                            for (OnThingsChangedListener<List<Team>> listener : onMyTeamListChangeListeners) {
                                listener.onThingChanged(result);
                            }
                            teamDataInitFinish = true;
                        } else {
                            lv("获取我加入的群列表失败, code : " + code + "  exception : " + exception);
                            NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver , false);
                            NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, false);
                            NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(teamMemberRemoveObserver , false);
                        }
                    }
                });
    }

    private void initRecentContact() {
        lv("正在初始化最近联系人列表...");
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, true);
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    lv("获取最近联系人列表成功,获取到" + (result == null ? result : result.size()) + "个最近联系人");
                    for (RecentContact newContact : result) {
                        String id = newContact.getContactId();
                        switch (newContact.getSessionType()) {
                            case P2P:
                                updateUserInfo(id , true);
                                break;
                            case Team:
                                updateTeamInfo(id , true);
                                break;
                        }
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

    private void updateTeamMember(final String id){
        NIMClient.getService(TeamService.class).queryMemberList(id).setCallback(new RequestCallback<List<TeamMember>>() {
            @Override
            public void onSuccess(List<TeamMember> param) {
                groupMemberMap.put(id , new Pair<Long, List<TeamMember>>(System.currentTimeMillis() , param));
                Pair pair = new Pair<String, List<TeamMember>>(id , param);
                for (OnThingsChangedListener<Pair<String, List<TeamMember>>> listener : onTeamMemberChangedListeners) {
                    listener.onThingChanged(pair);
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
                    listener.onThingChanged(bundle);
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
                    listener.onThingChanged(tempBundle);
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
                Bundle tempBundle = makeTeamInfoBundle(id, teamName);
                teamInfoMap.put(id, tempBundle);
                for (OnThingsChangedListener<Bundle> listener : onTeamInfoChangeListeners) {
                    listener.onThingChanged(tempBundle);
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

    private synchronized void pullTeamInfo(final String id, RequestCallback<Team> callback) {
        NIMClient.getService(TeamService.class).searchTeam(id)
                .setCallback(callback);
    }


    private void changeTeamInfoFetchingStatus(String id , boolean isFetching){
        Bundle bundle = teamInfoMap.get(id);
        if (isFetching){
            if (bundle == null){
                bundle = makeTeamInfoBundle(null, null);
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
     * @param id 群id
     * @param teamName 群名称
     *
     * @return 群资料bundle,其中包含
     * <p>群id,使用bundle.getString({@link YXClient#ID})
     * <p>群名称 使用bundle.getString({@link YXClient#TEAM_NAME})
     * <p>该条群资料最后更新时间戳 使用bundle.getLong({@link YXClient#LAST_UPDATE})获取
     */
    private Bundle makeTeamInfoBundle(String id , String teamName){
        Bundle bundle = new Bundle();
        bundle.putString(ID , id);
        bundle.putString(TEAM_NAME , teamName);
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
        //用户信息更新监听器列表
        public ArrayList<OnThingsChangedListener<Bundle>> myOnUserInfoChangeListeners
                = new ArrayList<OnThingsChangedListener<Bundle>>();
        //群信息更新监听器列表
        public ArrayList<OnThingsChangedListener<Bundle>> myOnTeamInfoChangeListeners
                = new ArrayList<OnThingsChangedListener<Bundle>>();
        //最近联系人更新监听器列表
        public ArrayList<OnThingsChangedListener<List<RecentContact>>> myOnRecentContactChangeListeners
                = new ArrayList<OnThingsChangedListener<List<RecentContact>>>();
        //我加入的群更新监听器列表
        public ArrayList<OnThingsChangedListener<List<Team>>> myOnMyTeamListChangeListeners
                = new ArrayList<OnThingsChangedListener<List<Team>>>();
        //群成员变更监听器列表
        public ArrayList<OnThingsChangedListener<Pair<String , List<TeamMember>>>> myOnTeamMemberChangedListeners
                = new ArrayList<OnThingsChangedListener<Pair<String,List<TeamMember>>>>();

        /**
         * 添加最近联系人列表变化监听器
         * @param listener 监听器的onEvent的参数为发生变化的最近联系人的列表
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
         * 添加用户资料变化监听器
         * @param listener 监听器的onEvent的参数为发生变化的用户的新的info,
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
         * 添加群资料变化监听器
         * @param listener 监听器的onEvent的参数为发生变化的群的新的info,
         *                 具体内容可以见{@link YXClient#makeTeamInfoBundle(String, String)}
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
         * 添加我加入的群列表变化监听器
         * @param listener 监听器的onEvent的参数为变化的群的list
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
         * @param listener 监听器中的onEvent会返回{@link Pair}类型的的数据,pair.first为成员变化的群的id号,pair.second为新的成员列表
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
    }


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

    /**
     * 对象改变监听器
     * @param <T>
     */
    public interface OnThingsChangedListener<T> {
        void onThingChanged(T thing);
    }

}
