package com.yougy.home.Observable;

/**
 * Created by Administrator on 2016/11/16.
 * 笔记观察者
 */

public interface Observer {

    /**
     * 更新笔记
     *
     * @param noteId    笔记id
     * @param noteStyle 笔记样式
     * @param subject   学科
     * @param noteTile  标题
     */
    void updataNote(long noteId, int noteStyle, String subject, String noteTile);

    /**
     * 删除笔记
     *
     * @param noteId 笔记id
     */
    void removeNote(int noteId);
}
