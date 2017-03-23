package com.inkscreen.model;

import java.io.Serializable;

/**
 * Created by xcz on 2016/11/29.
 */
public class UserInfo implements Serializable {

    public Ret ret;

    public static class Ret{
        boolean needSetTextbook;
        boolean thirdPartyRegister;

        public User user;

        public static class User{

            public Account account;
            public static class Account{

            }
            String avatarId;
            String avatarUrl;
            int balance;
            long birthday;
            boolean fan;
            boolean follow;
            boolean friend;
            String id;
            public Info info;
            public  static class Info{

            }
            String introduce;
            boolean me;
            String minAvatarUrl;
            String name;
            String nickname;
            int point;

           public  S s;
            public static class S{

            }
            public  School school;
            public static class School{

            }
            String schoolId;
            String sex;
            String sexName;

            public  Sp sp;
            public static class Sp{

            }

            String  type;
            public  UserState userState;
            public static class UserState{

            }
            String userTypeName;

            public Account getAccount() {
                return account;
            }

            public void setAccount(Account account) {
                this.account = account;
            }

            public String getAvatarId() {
                return avatarId;
            }

            public void setAvatarId(String avatarId) {
                this.avatarId = avatarId;
            }

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public int getBalance() {
                return balance;
            }

            public void setBalance(int balance) {
                this.balance = balance;
            }

            public long getBirthday() {
                return birthday;
            }

            public void setBirthday(long birthday) {
                this.birthday = birthday;
            }

            public boolean isFan() {
                return fan;
            }

            public void setFan(boolean fan) {
                this.fan = fan;
            }

            public boolean isFollow() {
                return follow;
            }

            public void setFollow(boolean follow) {
                this.follow = follow;
            }

            public boolean isFriend() {
                return friend;
            }

            public void setFriend(boolean friend) {
                this.friend = friend;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Info getInfo() {
                return info;
            }

            public void setInfo(Info info) {
                this.info = info;
            }

            public String getIntroduce() {
                return introduce;
            }

            public void setIntroduce(String introduce) {
                this.introduce = introduce;
            }

            public boolean isMe() {
                return me;
            }

            public void setMe(boolean me) {
                this.me = me;
            }

            public String getMinAvatarUrl() {
                return minAvatarUrl;
            }

            public void setMinAvatarUrl(String minAvatarUrl) {
                this.minAvatarUrl = minAvatarUrl;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getPoint() {
                return point;
            }

            public void setPoint(int point) {
                this.point = point;
            }

            public S getS() {
                return s;
            }

            public void setS(S s) {
                this.s = s;
            }

            public School getSchool() {
                return school;
            }

            public void setSchool(School school) {
                this.school = school;
            }

            public String getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(String schoolId) {
                this.schoolId = schoolId;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getSexName() {
                return sexName;
            }

            public void setSexName(String sexName) {
                this.sexName = sexName;
            }

            public Sp getSp() {
                return sp;
            }

            public void setSp(Sp sp) {
                this.sp = sp;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public UserState getUserState() {
                return userState;
            }

            public void setUserState(UserState userState) {
                this.userState = userState;
            }

            public String getUserTypeName() {
                return userTypeName;
            }

            public void setUserTypeName(String userTypeName) {
                this.userTypeName = userTypeName;
            }
        }


        public boolean isNeedSetTextbook() {
            return needSetTextbook;
        }

        public void setNeedSetTextbook(boolean needSetTextbook) {
            this.needSetTextbook = needSetTextbook;
        }

        public boolean isThirdPartyRegister() {
            return thirdPartyRegister;
        }

        public void setThirdPartyRegister(boolean thirdPartyRegister) {
            this.thirdPartyRegister = thirdPartyRegister;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    int ret_code;
    String ret_msg;
    String ret_token;


    public Ret getRet() {
        return ret;
    }

    public void setRet(Ret ret) {
        this.ret = ret;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getRet_token() {
        return ret_token;
    }

    public void setRet_token(String ret_token) {
        this.ret_token = ret_token;
    }
}
