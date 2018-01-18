package com.yougy.plide;

/**
 * Created by FH on 2018/1/12.
 */

public interface LoadListener {
    void onLoadStatusChanged(LoadController.PDF_STATUS newStatus, float downloadProgress, int totalPage);
}
