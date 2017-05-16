package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 * 仅传m参数，将返回全部教师信息。
 * 教师查询
 */

public class NewQueryTeachertRep extends NewBaseRep {

    /**
     * count : 1
     * data : [{"userId":10000001,"userNum":"0001","userName":"teacher001","userRealName":"费龙","userGender":"","userAge":null,"userPhoto":null,"userBirthday":null,"userCitizenId":null,"userQQ":null,"userEmail":"mail@www.domain.com","userMobile":"13800138000","userAddress":"北京市海淀区","userMemo":null,"userStatus":"启用","userExpire":"2099-12-31 00:00:00","schoolId":"38803","schoolName":"人大附中","gradeId":"3880302","gradeName":"初一","classId":"3880305,3880306","className":"初一1班,初一2班","subjectNames":"初中部语文,初中部数学"}]
     */

    private int count;
    private List<DataBean> data;

    public int getCount() {
        return count;
    }

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {
        /**
         * userId : 10000001
         * userNum : 0001
         * userName : teacher001
         * userRealName : 费龙
         * userGender :
         * userAge : null
         * userPhoto : null
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
         * classId : 3880305,3880306
         * className : 初一1班,初一2班
         * subjectNames : 初中部语文,初中部数学
         */

        private int userId;
        private String userNum;
        private String userName;
        private String userRealName;
        private String userGender;
        private String userAge;
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
        private String schoolId;
        private String schoolName;
        private String gradeId;
        private String gradeName;
        private String classId;
        private String className;
        private String subjectNames;

        public int getUserId() {
            return userId;
        }

        public String getUserNum() {
            return userNum;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserRealName() {
            return userRealName;
        }

        public String getUserGender() {
            return userGender;
        }

        public String getUserAge() {
            return userAge;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public String getUserBirthday() {
            return userBirthday;
        }

        public String getUserCitizenId() {
            return userCitizenId;
        }

        public String getUserQQ() {
            return userQQ;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public String getUserMemo() {
            return userMemo;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public String getUserExpire() {
            return userExpire;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public String getGradeId() {
            return gradeId;
        }

        public String getGradeName() {
            return gradeName;
        }

        public String getClassId() {
            return classId;
        }

        public String getClassName() {
            return className;
        }

        public String getSubjectNames() {
            return subjectNames;
        }
    }
}
