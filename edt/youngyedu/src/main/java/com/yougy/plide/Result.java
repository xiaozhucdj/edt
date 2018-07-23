package com.yougy.plide;

/**
 * Created by Administrator on 2018/7/17.
 */

public class Result<T> {
    int resultCode;
    String errorMsg;
    T data ;
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
