package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 地区查询
 */

public class NewQueryAreaRep extends  NewBaseRep {
    private List<Area> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Area> getData() {
        return data;
    }

    public void setData(List<Area> data) {
        this.data = data;
    }

    public static class Area{
        private String areaId;
        private String areaName;
        private String areaParent;
        private int areaLevel;
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

        public String getAreaParent() {
            return areaParent;
        }

        public void setAreaParent(String areaParent) {
            this.areaParent = areaParent;
        }

        public int getAreaLevel() {
            return areaLevel;
        }

        public void setAreaLevel(int areaLevel) {
            this.areaLevel = areaLevel;
        }

        public List<Area> getAreaList() {
            return areaList;
        }

        public void setAreaList(List<Area> areaList) {
            this.areaList = areaList;
        }

        @Override
        public String toString() {
            return areaName;
        }
    }

}

