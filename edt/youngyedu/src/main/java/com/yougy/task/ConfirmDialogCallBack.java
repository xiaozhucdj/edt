package com.yougy.task;

import javax.security.auth.callback.Callback;

/**
 * Created by lenovo on 2018/6/19.
 */

public interface ConfirmDialogCallBack extends Callback{
    void confirm ();
    void cancel ();
}
