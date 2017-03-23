package com.inkscreen.model;

import java.io.Serializable;

/**
 * Created by xcz on 2016/12/7.
 */
public class ChioceABCDInfo implements Serializable {

    String choiceName;
    String choiceContent;
    boolean choiceTag =false;

    boolean rightTag = false;

    public ChioceABCDInfo(String choiceName, String choiceContent) {
        this.choiceName = choiceName;
        this.choiceContent = choiceContent;
    }

    public ChioceABCDInfo() {
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public String getChoiceContent() {
        return choiceContent;
    }

    public void setChoiceContent(String choiceContent) {
        this.choiceContent = choiceContent;
    }


    public boolean isChoiceTag() {
        return choiceTag;
    }

    public void setChoiceTag(boolean choiceTag) {
        this.choiceTag = choiceTag;
    }

    public boolean isRightTag() {
        return rightTag;
    }

    public void setRightTag(boolean rightTag) {
        this.rightTag = rightTag;
    }
}
