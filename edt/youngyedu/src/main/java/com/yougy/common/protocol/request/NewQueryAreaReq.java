package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 地区查询
 * 仅传m参数，将返回全部地区信息。
 */

public class NewQueryAreaReq extends NewBaseReq {

    /**地区层级*/
    private int depth  = -1;
    /**地区编码*/
    private int areaId = -1;
    /**归属地区*/
    private String areaParent;

    public NewQueryAreaReq() {
        m = "queryArea" ;
        address = "classRoom" ;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setAreaParent(String areaParent) {
        this.areaParent = areaParent;
    }
}
