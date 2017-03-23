package com.yougy.init.bean;


import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/19.
 */

public class ClassInfo extends BaseData {

    private Org org;

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public static class Org{
        private String orgId;
        private String orgName;
        private String orgDisplay;
        private int count;
        private List<Org> orgList;

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public List<Org> getOrgList() {
            return orgList;
        }

        public void setOrgList(List<Org> orgList) {
            this.orgList = orgList;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getOrgDisplay() {
            return orgDisplay;
        }

        public void setOrgDisplay(String orgDisplay) {
            this.orgDisplay = orgDisplay;
        }

        @Override
        public String toString() {
            return orgName;
        }
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "org=" + org +
                '}';
    }
}
