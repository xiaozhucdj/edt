package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 * 用户登录
 */

public class NewLoginRep extends NewBaseRep {


    /**
     * count : 1
     * data : [{"userId":1000000001,"userRole":"学生","userName":"student1","userRealName":"袁野","userGender":"","userAge":18,"userPhoto":"photo1","userBirthday":null,"userCitizenId":null,"userQQ":null,"userEmail":"mail@www.domain.com","userMobile":"13800138000","userAddress":"北京市海淀区","userMemo":null,"userStatus":"启用","userExpire":"2099-12-31 00:00:00"}]
     */

    private List<NewQueryStudentRep.User> data;

    public int getCount() {
        return count;
    }

    public List<NewQueryStudentRep.User> getData() {
        return data;
    }



}
