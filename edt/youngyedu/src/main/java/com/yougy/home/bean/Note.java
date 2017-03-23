package com.yougy.home.bean;

import android.util.Log;

import com.yougy.common.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiangliang on 2016/8/12.
 */
public class Note extends DataSupport {
    private static final String TAG = "Note";
    //主键ID
    private int id;
    //页码
    private int pageNum;
    //对应书的页码
    private int bookPageNum = -1;
    //20161213添加 笔记类型  ，白纸 ，横线 田字格
    private int noteStyle = -1;

    private byte[] bytes;

    private List<Line> lines;
    //便签集合
    private List<Label> labelList = new ArrayList<>();
    //图片集合
    private List<Photograph> photographList = new ArrayList<>();

    private List<Diagram> diagramList = new ArrayList<>();
    //笔记名称
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getBookPageNum() {
        return bookPageNum;
    }

    public void setBookPageNum(int bookPageNum) {
        this.bookPageNum = bookPageNum;
    }

    public List<Line> getLines() {

        if (bytes != null && (lines == null || lines.size() == 0)) {
            long start = System.currentTimeMillis();
            lines = bytes2Obj(bytes);
            long end = System.currentTimeMillis();
            Log.e("Note", "bytes to obj take time is : " + (end - start));
        }
        if (lines == null) {
            lines = new ArrayList<>();
        }
        return lines;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    private List<Line> bytes2Obj(byte[] bytes) {
        List<Line> persons = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);

            persons = (List<Line>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persons;
    }

    public void obj2Bytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(lines);
            bytes = bos.toByteArray();
            bos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNoLines() {
        return lines == null || lines.size() == 0;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Label> getLabelList() {
        if (labelList.size() == 0) {
            long start = System.currentTimeMillis();
            labelList = DataSupport.where("note_id = ?", String.valueOf(id)).find(Label.class);
            long end = System.currentTimeMillis();
            LogUtils.e(TAG, "get label list take times is : " + (end - start));
        }
        return labelList;
    }

    public void setLabelList(List<Label> labelList) {
        this.labelList = labelList;
    }

    public List<Photograph> getPhotographList() {
        if (photographList.size() == 0) {
            long start = System.currentTimeMillis();
            photographList = DataSupport.where("note_id = ?", String.valueOf(id)).find(Photograph.class);
            long end = System.currentTimeMillis();
            LogUtils.e(TAG, "get photograph list take times is : " + (end - start));
        }
        return photographList;
    }

    public void setPhotographList(List<Photograph> photographList) {
        this.photographList = photographList;
    }

    public List<Diagram> getDiagramList() {
        if (diagramList.size() == 0) {
            long start = System.currentTimeMillis();
            diagramList = DataSupport.where("note_id = ?", String.valueOf(id)).find(Diagram.class);
            long end = System.currentTimeMillis();
            LogUtils.e(TAG, "get diagram list take times is : " + (end - start));
        }
        return diagramList;
    }

    public void setDiagramList(List<Diagram> diagramList) {
        this.diagramList = diagramList;
    }

    public int getNoteStyle() {
        return noteStyle;
    }

    public void setNoteStyle(int noteStyle) {
        this.noteStyle = noteStyle;
    }
}
