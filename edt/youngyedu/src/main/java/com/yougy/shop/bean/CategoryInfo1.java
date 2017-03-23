package com.yougy.shop.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jiangliang on 2017/3/17.
 */

public class CategoryInfo1 extends BaseData {
    @SerializedName("data")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public static class Category{

        private int categoryId;
        private String categoryName;
        private int categoryLevel;
        private int categoryParent;
        private String categoryDisplay;
        @SerializedName("categoryList")
        private List<CategoryInfo> childs;

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public int getCategoryLevel() {
            return categoryLevel;
        }

        public void setCategoryLevel(int categoryLevel) {
            this.categoryLevel = categoryLevel;
        }

        public int getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryParent(int categoryParent) {
            this.categoryParent = categoryParent;
        }

        public String getCategoryDisplay() {
            return categoryDisplay;
        }

        public void setCategoryDisplay(String categoryDisplay) {
            this.categoryDisplay = categoryDisplay;
        }

        public List<CategoryInfo> getChilds() {
            return childs;
        }

        public void setChilds(List<CategoryInfo> childs) {
            this.childs = childs;
        }
    }


}
