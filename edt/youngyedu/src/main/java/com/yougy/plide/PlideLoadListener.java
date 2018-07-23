package com.yougy.plide;

/**
 * Created by FH on 2018/7/7.
 */

public abstract class PlideLoadListener {
    public enum STATUS{
        DOWNLOADING,
        DOWNLOAD_SUCCESS,
        OPEN_DOCUMENT_ING,
        OPEN_DOCUMENT_SUCCESS,
        TO_PAGE_ING,
        TO_PAGE_SUCCESS,
        ERROR
    }

    public enum ERROR_TYPE{
        DOWNLOAD_ERROR,
        OPEN_DOCUMENT_ERROR,
        TO_PAGE_ERROR,
        USER_CANCLE,
        UNKNOWN
    }

    public abstract void onLoadStatusChanged(STATUS newStatus , String url , int toPageIndex , int totalPageCount , ERROR_TYPE errorType , String errorMsg);
}
