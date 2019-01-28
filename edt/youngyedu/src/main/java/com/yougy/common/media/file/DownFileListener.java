package com.yougy.common.media.file;

public interface DownFileListener {

    //正常处理进入阅读界面
    int STATE_NORMAL = 2;
    //没有支持设备的文件
    int STATE_NO_SUPPORT_BOOK = 3;
    int STATE_NO_SUPPORT_AUDIO = 4;
    int STATE_NO_SUPPORT_CONFIG = 5;

    // 解压文件失败 应该是服务器 文件存在问题 否则不会出现该问题。这个时候处理方式 进入阅读就可以，重试也无法解决问题。
    int STATE_ERROR_AUDIO_ZIP = 6;
    int STATE_ERROR_CONFIG_ZIP = 7;
    //服务器没有资源
    int STATE_SERVER_NO_BOOK_SOURCE = 8;
    int STATE_SERVER_NO_AUDIO_SOURCE = 9;
    int STATE_SERVER_NO_CONFIG_SOURCE = 10;

    /**
     * 下载文件的回调 包括成功失败等待特殊情况
     */
    void onDownFileListenerCallBack(int state);
}
