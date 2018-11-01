package com.yougy.common.global;

public class Constant {
    //IICODE=作业类型
    //文档:
    //http://192.168.12.24/web/#/5?page_id=273
    public final static String IICODE_01 = "II01";//问答
    public final static String IICODE_02 = "II02";//课堂作业
    public final static String IICODE_03 = "II03";//家庭作业

    //IHCODE=作业状态
    //文档:
    //http://192.168.12.24/web/#/5?page_id=272
    public final static String IHCODE_01 = "IH01";//未开始
    public final static String IHCODE_02 = "IH02";//作答中
    public final static String IHCODE_03 = "IH03";//未批改
    public final static String IHCODE_04 = "IH04";//批改中
    public final static String IHCODE_05 = "IH05";//已批改
    public final static String IHCODE_51 = "IH51";//未提交

    //IGCODE=作业中的学生的状态
    //文档:
    //http://192.168.12.24/web/#/5?page_id=279
    public final static String IGCODE_01 = "IG01";//待解答
    public final static String IGCODE_02 = "IG02";//答题中
    public final static String IGCODE_03 = "IG03";//已答完
    public final static String IGCODE_04 = "IG04";//已评完
    public final static String IGCODE_05 = "IG05";//已关闭

    //IKCODE=评价方式
    //http://192.168.12.24/web/#/5?page_id=344
    public final static String IKCODE_01 = "IK01";//自评
    public final static String IKCODE_02 = "IK02";//互评(多人)
    public final static String IKCODE_03 = "IK03";//互评(组内)
    public final static String IKCODE_04 = "IK04";//家长评
    public final static String IKCODE_05 = "IK05";//教师评
    public final static String IKCODE_06 = "IK06";//系统评

    //IJCODE=考试范围
    //文档:
    // http://192.168.12.24/web/#/5?page_id=331
    public final static String IJCODE_01 = "IJ01";//个人
    public final static String IJCODE_02 = "IJ02";//群组
    public final static String IJCODE_03 = "IJ03";//班级
    public final static String IJCODE_04 = "IJ04";//年级
    public final static String IJCODE_05 = "IJ05";//全校

    //ICCODE=题目类型
    //文档:
    //http://192.168.12.24/web/#/5?page_id=295
    public final static String ICCODE_01 = "IC01";//填空
    public final static String ICCODE_02 = "IC02";//选择
    public final static String ICCODE_03 = "IC03";//问答
    public final static String ICCODE_04 = "IC04";//判断

}
