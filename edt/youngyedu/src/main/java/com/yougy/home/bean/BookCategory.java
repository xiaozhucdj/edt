package com.yougy.home.bean;

/**
 * Created by Administrator on 2016/11/1.
 * 图书分类码 ，例如：一年级，
 * 课本
 * 进行排序
 */
public class BookCategory implements Comparable<BookCategory> {
    /**分类名字*/
    private String categoryName;
    /**分类ID*/
    private int categoryId;
    /**当前条目是否被选中*/
    private boolean isSelect;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int compareTo(BookCategory arg0) {
        return this.getCategoryId().compareTo(arg0.getCategoryId());
    }



    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "BookCategory{" +
                "categoryName='" + categoryName + '\'' +
                ", categoryId=" + categoryId +
                ", isSelect=" + isSelect +
                '}';
    }
}
