package com.yougy.home;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.yougy.common.utils.FtpUtil;
import com.yougy.common.utils.SpUtil;

/**
 * Created by jiangliang on 2016/12/23.
 */

public class UploadService extends IntentService {
    private static final String TAG = "UploadService";
    public static final String DATABASE_NAME = "leke.db";
    public static final String JOURNAL_NAME = "leke.db-journal";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String path = getDatabasePath(DATABASE_NAME).getAbsolutePath();
        Log.e(TAG, "path is : " + path);
        boolean uploadDb = FtpUtil.uploadFile(path, DATABASE_NAME);
//        String journalPath = getDatabasePath(JOURNAL_NAME).getAbsolutePath();
//        boolean uploadJournal = FtpUtil.uploadFile(journalPath, JOURNAL_NAME);
//        boolean result = uploadDb & uploadJournal;
//        Log.e(TAG, "result is : " + result);
        if (uploadDb) {
            SpUtil.changeContent(false);
        }
    }
}
