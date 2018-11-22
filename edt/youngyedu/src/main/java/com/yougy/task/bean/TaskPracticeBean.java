package com.yougy.task.bean;


import com.yougy.anwser.Content_new;

import java.util.List;

public class TaskPracticeBean {
    private int practiceId;

    private String practiceName;

    private List<Content_new> mContent_news;

    public TaskPracticeBean(String practiceName, List<Content_new> content_news) {
        this.practiceName = practiceName;
        mContent_news = content_news;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public List<Content_new> getContent_news() {
        return mContent_news;
    }

    public void setContent_news(List<Content_new> content_news) {
        mContent_news = content_news;
    }


}
