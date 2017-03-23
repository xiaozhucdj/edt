package com.onyx.android.sdk.ui.view.tagview;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    private List<T> tagData;
    private OnDataChangedListener onDataChangedListener;
    private HashSet<Integer> checkedPositionList = new HashSet<>();

    public TagAdapter(List<T> data) {
        tagData = data;
    }

    public TagAdapter(T[] data) {
        tagData = new ArrayList<T>(Arrays.asList(data));
    }

    public interface OnDataChangedListener {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener) {
        onDataChangedListener = listener;
    }

    public void setSelectedList(int... pos) {
        for (int i = 0; i < pos.length; i++)
            checkedPositionList.add(pos[i]);
        notifyDataChanged();
    }

    public void setSelectedList(Set<Integer> set) {
        checkedPositionList.clear();
        checkedPositionList.addAll(set);
        notifyDataChanged();
    }

    HashSet<Integer> getPreCheckedList() {
        return checkedPositionList;
    }


    public int getCount() {
        return tagData == null ? 0 : tagData.size();
    }

    public void notifyDataChanged() {
        if (onDataChangedListener != null) {
            onDataChangedListener.onChanged();
        }
    }

    public T getItem(int position) {
        return tagData.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);

    public boolean setSelected(int position, T t) {
        return false;
    }
}