package com.yougy.shop.bean;

import com.google.gson.annotations.SerializedName;
import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/8.
 */

public class HomeData extends BaseData {

    private int categoryCount;
    private List<Category> categoryList;
    private int bookCount;
    private List<BookInfo> bookList;

    public int getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public List<BookInfo> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookInfo> bookList) {
        this.bookList = bookList;
    }

    public static class Category{
        @SerializedName("categoryId")
        private int id;
        @SerializedName("categoryName")
        private String name;
        @SerializedName("categoryDisplay")
        private String display;
        @SerializedName("count")
        private int count;
        @SerializedName("categoryList")
        private List<CategoryInfo> categories;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }


        public List<CategoryInfo> getCategories() {
            return categories;
        }

        public void setCategories(List<CategoryInfo> categories) {
            this.categories = categories;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "display=" + display +
                    ", categories=" + categories +
                    '}';
        }
    }
}
