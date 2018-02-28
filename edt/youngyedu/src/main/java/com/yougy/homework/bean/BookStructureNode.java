package com.yougy.homework.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/14.
 */

public class BookStructureNode implements Parcelable {
    private int id;
    private String name;
    private int level;
    private List<BookStructureNode> nodes;

    public int getId() {
        return id;
    }

    public BookStructureNode setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BookStructureNode setName(String name) {
        this.name = name;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public BookStructureNode setLevel(int level) {
        this.level = level;
        return this;
    }

    public List<BookStructureNode> getNodes() {
        return nodes;
    }

    public BookStructureNode setNodes(List<BookStructureNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.level);
        dest.writeList(this.nodes);
    }

    public BookStructureNode() {
    }

    protected BookStructureNode(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.level = in.readInt();
        this.nodes = new ArrayList<BookStructureNode>();
        in.readList(this.nodes, BookStructureNode.class.getClassLoader());
    }

    public static final Parcelable.Creator<BookStructureNode> CREATOR = new Parcelable.Creator<BookStructureNode>() {
        @Override
        public BookStructureNode createFromParcel(Parcel source) {
            return new BookStructureNode(source);
        }

        @Override
        public BookStructureNode[] newArray(int size) {
            return new BookStructureNode[size];
        }
    };
}
