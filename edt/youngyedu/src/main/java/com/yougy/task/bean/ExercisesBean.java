package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author: zhang yc
 * @create date: 2018/6/22 15:39
 * @class desc: 练习
 * @modifier: 
 * @modify date: 2018/6/22 15:39
 * @modify desc: 
 */
public class ExercisesBean implements Parcelable{
    private List<String> answers;
    private int exerciseId;
    private int exerciseNumber;
    private int exercisePadingRight;
    private int exercisePadingleft;
    private int exerciseSize;
    private int exerciseTitle;
    private int exerciselineSpacing;

    public ExercisesBean(List<String> answers, int exerciseId, int exerciseNumber, int exercisePadingRight,
                         int exercisePadingleft, int exerciseSize, int exerciseTitle, int exerciselineSpacing) {
        this.answers = answers;
        this.exerciseId = exerciseId;
        this.exerciseNumber = exerciseNumber;
        this.exercisePadingRight = exercisePadingRight;
        this.exercisePadingleft = exercisePadingleft;
        this.exerciseSize = exerciseSize;
        this.exerciseTitle = exerciseTitle;
        this.exerciselineSpacing = exerciselineSpacing;
    }

    protected ExercisesBean(Parcel in) {
        answers = in.createStringArrayList();
        exerciseId = in.readInt();
        exerciseNumber = in.readInt();
        exercisePadingRight = in.readInt();
        exercisePadingleft = in.readInt();
        exerciseSize = in.readInt();
        exerciseTitle = in.readInt();
        exerciselineSpacing = in.readInt();
    }

    public static final Creator<ExercisesBean> CREATOR = new Creator<ExercisesBean>() {
        @Override
        public ExercisesBean createFromParcel(Parcel in) {
            return new ExercisesBean(in);
        }

        @Override
        public ExercisesBean[] newArray(int size) {
            return new ExercisesBean[size];
        }
    };

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    public int getExercisePadingRight() {
        return exercisePadingRight;
    }

    public void setExercisePadingRight(int exercisePadingRight) {
        this.exercisePadingRight = exercisePadingRight;
    }

    public int getExercisePadingleft() {
        return exercisePadingleft;
    }

    public void setExercisePadingleft(int exercisePadingleft) {
        this.exercisePadingleft = exercisePadingleft;
    }

    public int getExerciseSize() {
        return exerciseSize;
    }

    public void setExerciseSize(int exerciseSize) {
        this.exerciseSize = exerciseSize;
    }

    public int getExerciseTitle() {
        return exerciseTitle;
    }

    public void setExerciseTitle(int exerciseTitle) {
        this.exerciseTitle = exerciseTitle;
    }

    public int getExerciselineSpacing() {
        return exerciselineSpacing;
    }

    public void setExerciselineSpacing(int exerciselineSpacing) {
        this.exerciselineSpacing = exerciselineSpacing;
    }

    @Override
    public String toString() {
        return "ExercisesBean{" +
                "answers=" + answers +
                ", exerciseId=" + exerciseId +
                ", exerciseNumber=" + exerciseNumber +
                ", exercisePadingRight=" + exercisePadingRight +
                ", exercisePadingleft=" + exercisePadingleft +
                ", exerciseSize=" + exerciseSize +
                ", exerciseTitle=" + exerciseTitle +
                ", exerciselineSpacing=" + exerciselineSpacing +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(answers);
        dest.writeInt(exerciseId);
        dest.writeInt(exerciseNumber);
        dest.writeInt(exercisePadingRight);
        dest.writeInt(exercisePadingleft);
        dest.writeInt(exerciseSize);
        dest.writeInt(exerciseTitle);
        dest.writeInt(exerciselineSpacing);
    }
}
