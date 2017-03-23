package com.inkscreen.model;

import java.io.Serializable;

/**
 * Created by xcz on 2016/11/17.
 */
public class Subject  implements Serializable {
    String topic;
    Boolean titelflag = false;
    Boolean writeorblacktag = false;
    String result;
    Boolean doflag = false;
    Boolean emendTag = false;
    public Subject(String topic, Boolean titelflag,Boolean writeorblacktag,Boolean doflag,Boolean emendTag) {
        this.topic = topic;
        this.titelflag = titelflag;
        this.writeorblacktag = writeorblacktag;
        this.doflag = doflag;
        this.emendTag =emendTag;
    }

    public Subject() {
    }

    public Boolean getEmendTag() {
        return emendTag;
    }

    public void setEmendTag(Boolean emendTag) {
        this.emendTag = emendTag;
    }

    public Boolean getDoflag() {
        return doflag;
    }

    public void setDoflag(Boolean doflag) {
        this.doflag = doflag;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Boolean getTitelflag() {
        return titelflag;
    }

    public void setTitelflag(Boolean titelflag) {
        this.titelflag = titelflag;
    }

    public Boolean getWriteorblacktag() {
        return writeorblacktag;
    }

    public void setWriteorblacktag(Boolean writeorblacktag) {
        this.writeorblacktag = writeorblacktag;
    }
}
