package com.yougy.init.bean;


import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/17.
 */

public class AreaInfo extends BaseData {

    private Area area;

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public static class Area {
        private String areaId;
        private String areaName;
        private int count;
        private List<Area> areaList;

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Area> getAreaList() {
            return areaList;
        }

        public void setAreaList(List<Area> areaList) {
            this.areaList = areaList;
        }

        @Override
        public String toString() {
//            if (!TextUtils.isEmpty(areaName) && areaName.length() > 4) {
//                return areaName.substring(0, 4) + "...";
//            }
            return areaName;
        }
    }

    @Override
    public String toString() {
        return "AreaInfo{" +
                "area=" + area +
                '}';
    }
}
