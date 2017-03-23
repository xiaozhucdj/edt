package com.yougy.common.protocol.request;


import com.yougy.common.utils.SpUtil;

/**
 *  图书推荐 请求协议
 */
public class PromoteBookRequest {
    /**
     * 请求参数
     * 参数	类型	默认值	是否必须	描述
     userId	int	无	是	用户编码
     bookId	int	无	否	相关图书编码
     categoryId	int	无	否	分类编码
     pageCur	int	1	否	分页页码(>=1)
     pageSize	int	100	否	每页记录数(>=1)
     bookId参数缺失，将根据用户信息推荐图书。
     pageCur与pageSize参数可控制返回查询结果数量。
     */
    private int userId;
    private int bookId = -1;
    private int pageCur = -1;
    private int pageSize  =-1;
    private int categoryId = -1;

    public PromoteBookRequest(){
        userId = SpUtil.getUserId();
    }

    public PromoteBookRequest(int userId, int bookId, int pageCur, int pageSize, int categoryId) {
        this.userId = userId;
        this.bookId = bookId;
        this.pageCur = pageCur;
        this.pageSize = pageSize;
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getPageCur() {
        return pageCur;
    }

    public void setPageCur(int pageCur) {
        this.pageCur = pageCur;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
