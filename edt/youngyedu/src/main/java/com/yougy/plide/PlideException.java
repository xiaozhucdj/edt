package com.yougy.plide;

/**
 * Created by FH on 2018/1/11.
 */

public class PlideException extends Exception{
    private PlideLoadListener.ERROR_TYPE mErrorType;
    public PlideException(String message) {
        super(message);
    }
    public PlideException(String message , PlideLoadListener.ERROR_TYPE errorType) {
        super(message);
        mErrorType = errorType;
    }

    public PlideLoadListener.ERROR_TYPE getErrorType() {
        return mErrorType;
    }
}
