package com.yougy.home.Observable;

/**
 * Created by Administrator on 2016/11/16.
 */

/***
 * 被观察者
 */
public interface Observable {

    /**
     * 添加观察者
     * @param observer
     */
    void addObserver(Observer observer);

    /***
     * 移除 观察者
     * @param observer
     */
    void removeObserver(Observer observer);

    /**
     * 修改笔记
     */
    void notifyChange(long noteId, int noteStyle, String subject, String noteTile);

    /**
     * 删除笔记
     * @param noteId
     */
    void notifyDelete(int noteId) ;
}
