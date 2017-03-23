package com.inkscreen.model;

import java.io.Serializable;

/**
 * Created by xcz on 2016/11/21.
 */
public class LeaveInfo implements Serializable{

    String pctAge;

    public LeaveInfo(String pctAge) {
        this.pctAge = pctAge;
    }
    public LeaveInfo() {

    }

    public String getPctAge() {
        return pctAge;
    }

    public void setPctAge(String pctAge) {
        this.pctAge = pctAge;
    }
}
