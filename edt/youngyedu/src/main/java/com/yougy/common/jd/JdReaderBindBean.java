package com.yougy.common.jd;


/*  onyx: {
  {
 "_id": "uuid",
 "name" : "xxxx",
 "role" : "学生或者老师",
 "groups": ["广州市第一中学三年级2班"],
 "phone": "xxxxx",
 "token": "uuid"
}


/*        {
        "count": 1,
        "msg": "success",
        "data": [
        {
        "userId": 1000002054,
        "userAge": null,
        "userGender": "男",
        "userRoleCode": "UB10",
        "gradeDisplay": "小学四年级",
        "userAddress": null,
        "deviceId": "b0f1ec6bd118",
        "deptName": "小学部",
        "userEmail": null,
        "schoolLevel": 1,
        "deptId": 3850101,
        "userGenderCode": "UA01",
        "userBirthday": null,
        "userPhoto": null,
        "schoolId": 38501,
        "userCreateTime": "2019-02-18 14:55:42",
        "classDisplay": "1班",
        "userToken": "keitWppIb3csLVw62rLs+m+zCDG/CzM3ADFCjOzdv98=",
        "userStatus": "启用",
        "userRole": "学生",
        "schoolAdmin": 100049,
        "userMemo": null,
        "schoolName": "朝阳融捷校园",
        "systemCode": "PA01",
        "classId": 3850115,
        "gradeName": "小学四年级",
        "userCitizenId": null,
        "userStatusCode": "UC01",
        "className": "小学四年级1班",
        "gradeId": 3850105,
        "subjectNames": "语文,数学,外语,音乐,体育,美术,品德与社会,科学,综合实践活动,信息技术",
        "deptDisply": "小学部",
        "systemName": "通用",
        "userMobile": null,
        "userExpire": "2099-12-31 00:00:00",
        "userQQ": null,
        "userPassword": "e10adc3949ba59abbe56e057f20f883e",
        "userRealName": "小小科",
        "userName": "1000002054",
        "deviceModelCode": "BN01",
        "systemId": 1,
        "userNum": "1010101",
        "deviceModel": "默认"
        }
        ],
        "code": 200
        }*/


import java.util.Arrays;

public class JdReaderBindBean {
    private String _id;
    private String name;
    private String role;
    private String[] groups;
    private String phone;
    private String token ;
    private String username ;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
        username = _id ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JdReaderBindBean{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", groups=" + Arrays.toString(groups) +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
