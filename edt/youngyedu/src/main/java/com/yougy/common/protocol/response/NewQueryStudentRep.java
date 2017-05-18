package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 * 学生查询
 */

public class NewQueryStudentRep extends NewBaseRep {

    /**
     * count : 1
     * data : [{"userId":1000000001,"userNum":"0001","userName":"student1","userRealName":"袁野","userGender":"","userAge":18,"userPhoto":"photo1","userBirthday":null,"userCitizenId":null,"userQQ":null,"userEmail":"mail@www.domain.com","userMobile":"13800138000","userAddress":"北京市海淀区","userMemo":null,"userStatus":"启用","userExpire":"2099-12-31 00:00:00","schoolId":38803,"schoolName":"人大附中","gradeId":3880302,"gradeName":"初一","classId":3880305,"className":"初一1班","subjectNames":"语文,数学,外语,生物,历史,地理,思想品德,音乐,体育与健康,美术,综合实践活动,信息技术"}]
     */

    private List<User> data;

    public int getCount() {
        return count;
    }

    public List<User> getData() {
        return data;
    }

    public static class User {
        /**
         * userId : 1000000001
         * userNum : 0001
         * userName : student1
         * userRealName : 袁野
         * userGender :
         * userAge : 18
         * userPhoto : photo1
         * userBirthday : null
         * userCitizenId : null
         * userQQ : null
         * userEmail : mail@www.domain.com
         * userMobile : 13800138000
         * userAddress : 北京市海淀区
         * userMemo : null
         * userStatus : 启用
         * userExpire : 2099-12-31 00:00:00
         * schoolId : 38803
         * schoolName : 人大附中
         * gradeId : 3880302
         * gradeName : 初一
         * classId : 3880305
         * className : 初一1班
         * subjectNames : 语文,数学,外语,生物,历史,地理,思想品德,音乐,体育与健康,美术,综合实践活动,信息技术
         */

        private int userId;
        private String userNum;
        private String userName;
        private String userRealName;
        private String userGender;
        private int userAge;
        private String userPhoto;
        private String userBirthday;
        private String userCitizenId;
        private String userQQ;
        private String userEmail;
        private String userMobile;
        private String userAddress;
        private String userMemo;
        private String userStatus;
        private String userExpire;
        private int schoolId;
        private String schoolName;
        private int gradeId;
        private String gradeName;
        private int classId;
        private String className;
        private String subjectNames;
        private String deviceId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserNum() {
            return userNum;
        }

        public void setUserNum(String userNum) {
            this.userNum = userNum;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserRealName() {
            return userRealName;
        }

        public void setUserRealName(String userRealName) {
            this.userRealName = userRealName;
        }

        public String getUserGender() {
            return userGender;
        }

        public void setUserGender(String userGender) {
            this.userGender = userGender;
        }

        public int getUserAge() {
            return userAge;
        }

        public void setUserAge(int userAge) {
            this.userAge = userAge;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public void setUserPhoto(String userPhoto) {
            this.userPhoto = userPhoto;
        }

        public String getUserBirthday() {
            return userBirthday;
        }

        public void setUserBirthday(String userBirthday) {
            this.userBirthday = userBirthday;
        }

        public String getUserCitizenId() {
            return userCitizenId;
        }

        public void setUserCitizenId(String userCitizenId) {
            this.userCitizenId = userCitizenId;
        }

        public String getUserQQ() {
            return userQQ;
        }

        public void setUserQQ(String userQQ) {
            this.userQQ = userQQ;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        public String getUserMemo() {
            return userMemo;
        }

        public void setUserMemo(String userMemo) {
            this.userMemo = userMemo;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getUserExpire() {
            return userExpire;
        }

        public void setUserExpire(String userExpire) {
            this.userExpire = userExpire;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getGradeId() {
            return gradeId;
        }

        public void setGradeId(int gradeId) {
            this.gradeId = gradeId;
        }

        public String getGradeName() {
            return gradeName;
        }

        public void setGradeName(String gradeName) {
            this.gradeName = gradeName;
        }

        public int getClassId() {
            return classId;
        }

        public void setClassId(int classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSubjectNames() {
            return subjectNames;
        }

        public void setSubjectNames(String subjectNames) {
            this.subjectNames = subjectNames;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
    }
}
