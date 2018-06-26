package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author: zhang yc
 * @create date: 2018/6/22 14:32
 * @class desc: 任务
 * @modifier: 
 * @modify date: 2018/6/22 14:32
 * @modify desc: 
 */
public class TaskBean implements Parcelable{

    private String bookSection;//章节
    private String startTime;
    private String endtime;
    private int signatureState;//是否已经签名
    private int signatureType; //是否需要签名
    private int studyDataCount;//学习资料
    private List<StudyDataBean> studyDataList;
    private int submit;//是否提交
    private TaskContentBean taskContentBean;
    private int taskExercisesCount;
    private List<ExercisesBean> taskExercisesList;
    private int taskId;
    private String taskTitle;//任务名

    public TaskBean(String bookSection, String startTime, String endtime, int signatureState,
                    int signatureType, int studyDataCount, List<StudyDataBean> studyDataList,
                    int submit, TaskContentBean taskContentBean, int taskExercisesCount,
                    List<ExercisesBean> taskExercisesList, int taskId, String taskTitle) {
        this.bookSection = bookSection;
        this.startTime = startTime;
        this.endtime = endtime;
        this.signatureState = signatureState;
        this.signatureType = signatureType;
        this.studyDataCount = studyDataCount;
        this.studyDataList = studyDataList;
        this.submit = submit;
        this.taskContentBean = taskContentBean;
        this.taskExercisesCount = taskExercisesCount;
        this.taskExercisesList = taskExercisesList;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
    }

    public String getBookSection() {
        return bookSection;
    }

    public void setBookSection(String bookSection) {
        this.bookSection = bookSection;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getSignatureState() {
        return signatureState;
    }

    public void setSignatureState(int signatureState) {
        this.signatureState = signatureState;
    }

    public int getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(int signatureType) {
        this.signatureType = signatureType;
    }

    public int getStudyDataCount() {
        return studyDataCount;
    }

    public void setStudyDataCount(int studyDataCount) {
        this.studyDataCount = studyDataCount;
    }

    public List<StudyDataBean> getStudyDataList() {
        return studyDataList;
    }

    public void setStudyDataList(List<StudyDataBean> studyDataList) {
        this.studyDataList = studyDataList;
    }

    public int getSubmit() {
        return submit;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

    public TaskContentBean getTaskContentBean() {
        return taskContentBean;
    }

    public void setTaskContentBean(TaskContentBean taskContentBean) {
        this.taskContentBean = taskContentBean;
    }

    public int getTaskExercisesCount() {
        return taskExercisesCount;
    }

    public void setTaskExercisesCount(int taskExercisesCount) {
        this.taskExercisesCount = taskExercisesCount;
    }

    public List<ExercisesBean> getTaskExercisesList() {
        return taskExercisesList;
    }

    public void setTaskExercisesList(List<ExercisesBean> taskExercisesList) {
        this.taskExercisesList = taskExercisesList;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    protected TaskBean(Parcel in) {
        bookSection = in.readString();
        startTime = in.readString();
        endtime = in.readString();
        signatureState = in.readInt();
        signatureType = in.readInt();
        studyDataCount = in.readInt();
        submit = in.readInt();
        taskExercisesCount = in.readInt();
        taskId = in.readInt();
        taskTitle = in.readString();
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "bookSection='" + bookSection + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", signatureState=" + signatureState +
                ", signatureType=" + signatureType +
                ", studyDataCount=" + studyDataCount +
                ", studyDataList=" + studyDataList +
                ", submit=" + submit +
                ", taskContentBean=" + taskContentBean +
                ", taskExercisesCount=" + taskExercisesCount +
                ", taskExercisesList=" + taskExercisesList +
                ", taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                '}';
    }

    public static final Creator<TaskBean> CREATOR = new Creator<TaskBean>() {
        @Override
        public TaskBean createFromParcel(Parcel in) {
            return new TaskBean(in);
        }

        @Override
        public TaskBean[] newArray(int size) {
            return new TaskBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookSection);
        dest.writeString(startTime);
        dest.writeString(endtime);
        dest.writeInt(signatureState);
        dest.writeInt(signatureType);
        dest.writeInt(studyDataCount);
        dest.writeInt(submit);
        dest.writeInt(taskExercisesCount);
        dest.writeInt(taskId);
        dest.writeString(taskTitle);
    }



   /* private String taskName;
    private String chapter;
    private int materials;
    private int exercises;
    private boolean sign;
    private String taskState;

    public TaskBean(String taskName, String chapter, int materials, int exercises, boolean sign, String taskState) {
        this.taskName = taskName;
        this.chapter = chapter;
        this.materials = materials;
        this.exercises = exercises;
        this.sign = sign;
        this.taskState = taskState;
    }

    protected TaskBean(Parcel in) {
        taskName = in.readString();
        chapter = in.readString();
        materials = in.readInt();
        exercises = in.readInt();
        sign = in.readByte() != 0;
        taskState = in.readString();
    }

    public static final Creator<TaskBean> CREATOR = new Creator<TaskBean>() {
        @Override
        public TaskBean createFromParcel(Parcel in) {
            return new TaskBean(in);
        }

        @Override
        public TaskBean[] newArray(int size) {
            return new TaskBean[size];
        }
    };

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public int getMaterials() {
        return materials;
    }

    public void setMaterials(int materials) {
        this.materials = materials;
    }

    public int getExercises() {
        return exercises;
    }

    public void setExercises(int exercises) {
        this.exercises = exercises;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskName);
        dest.writeString(chapter);
        dest.writeInt(materials);
        dest.writeInt(exercises);
        dest.writeString(taskState);

    }*/
}
