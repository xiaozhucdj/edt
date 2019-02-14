package com.yougy.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.view.NoteBookView;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoadAnswer {

    private Context mContext;
    private HandlerThread mHandlerThread = new HandlerThread("load_answer");
    public LoadAnswer(Activity activity) {
        mContext = activity.getApplicationContext();
        init();
    }
    public LoadAnswer(AppCompatActivity appCompatActivity) {
        mContext = appCompatActivity.getApplicationContext();
        init();
    }

    private int currentPage;
    private int currentSelectPosition;
    private NoteBookView2 mNoteBookView2;
    private NoteBookView mNoteBookView;
    public void setNoteView (NoteBookView2 noteView, int position, int page, String url) {
        mNoteBookView2 = noteView;
        currentPage = page;
        currentSelectPosition = position;
        mCurrentAnswerUrl = url;
        mHandler.removeCallbacks(mLoadAnswerRunnable);
        mHandler.post(mLoadAnswerRunnable);
    }

    private void setNoteView (NoteBookView2 noteView) {
        mNoteBookView2 = noteView;
        mHandler.removeCallbacks(mLoadAnswerRunnable);
        mHandler.post(mLoadAnswerRunnable);
    }

    private void setNoteView (NoteBookView noteView) {
        mNoteBookView = noteView;
        mHandler.removeCallbacks(mLoadAnswerRunnable);
        mHandler.post(mLoadAnswerRunnable);
    }

    protected List<StageTaskBean.StageScene> mStageScenes = new ArrayList<>();
    protected List<StageTaskBean.StageScene.SceneContent> mSceneContents = new ArrayList<>();
    public void loadAnswer (NoteBookView2 noteBookView2, StageTaskBean stageTaskBean, int currentSelectPosition, int currentPage) {
        mStageScenes.clear();
        if (stageTaskBean.getStageScene()!=null) {
            mStageScenes.addAll(stageTaskBean.getStageScene());
        }
        LogUtils.i("TaskTest state stageTaskBean = " + mStageScenes.size());
        for (int i = 0; i < mStageScenes.size(); i++) {
            StageTaskBean.StageScene stageScene = mStageScenes.get(i);
            if (stageScene.getSceneContents() != null && stageScene.getSceneContents().size() > 0) {
                mSceneContents.clear();
                mSceneContents.addAll(stageScene.getSceneContents());
            }
            LogUtils.i("TaskTest mSceneContents = " + mSceneContents.size());
            for (int j = 0; j < mSceneContents.size(); j++) {
                StageTaskBean.StageScene.SceneContent sceneContent = mSceneContents.get(j);
                String bucket = sceneContent.getBucket();
                String remote = sceneContent.getRemote();

                String[] split = remote.split("_");
                int length = split.length;
                String[] split1 = split[length - 1].split("\\.");
                int page = -1;
                int question = -1;
                try {
                    page = Integer.parseInt(split1[0]);
                    question = Integer.parseInt(split[length-2]);
                } catch (Exception e) {
                    LogUtils.e("" + e.getMessage());
                }

                LogUtils.i("TaskTest length = " + length + "   page = " + page + "  question = " + question
                        + "   currentPage = " + currentPage + "   currentSelectPosition = " + currentSelectPosition);
                if (page == currentPage && question == currentSelectPosition) {
                    mCurrentAnswerUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + remote;
                    setNoteView(noteBookView2);
                }
            }
        }
    }


    public void loadAnswer (NoteBookView noteBookView2, StageTaskBean stageTaskBean, int currentSelectPosition, int currentPage) {
        mStageScenes.clear();
        if (stageTaskBean.getStageScene()!=null) {
            mStageScenes.addAll(stageTaskBean.getStageScene());
        }
        LogUtils.i("TaskTest state stageTaskBean = " + mStageScenes.size());
        for (int i = 0; i < mStageScenes.size(); i++) {
            StageTaskBean.StageScene stageScene = mStageScenes.get(i);
            if (stageScene.getSceneContents() != null && stageScene.getSceneContents().size() > 0) {
                mSceneContents.clear();
                mSceneContents.addAll(stageScene.getSceneContents());
            }
            LogUtils.i("TaskTest mSceneContents = " + mSceneContents.size());
            for (int j = 0; j < mSceneContents.size(); j++) {
                StageTaskBean.StageScene.SceneContent sceneContent = mSceneContents.get(j);
                String bucket = sceneContent.getBucket();
                String remote = sceneContent.getRemote();
                LogUtils.i("TaskTest remote = " + remote);
                try {
                    String[] split = remote.split("_");
                    int length = split.length;
                    String[] split1 = split[length - 1].split("\\.");
                    int page = -1;
                    int question = -1;
                    try {
                        page = Integer.parseInt(split1[0]);
                        question = Integer.parseInt(split[length-2]);
                    } catch (Exception e) {
                        LogUtils.e("" + e.getMessage());
                    }

                    LogUtils.i("TaskTest length = " + length + "   page = " + page + "  question = " + question
                            + "   currentPage = " + currentPage + "   currentSelectPosition = " + currentSelectPosition);
                    if (page == currentPage && question == currentSelectPosition) {
                        mCurrentAnswerUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + remote;
                        setNoteView(noteBookView2);
                    }
                } catch (Exception e) {
                    LogUtils.e("remote split exception!");
                }
            }
        }
    }

    public void removeCallback () {
        mHandler.removeCallbacks(mLoadAnswerRunnable);
    }

    private void init () {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LOAD_ANSWER:
                        LogUtils.d("TaskTest drawBitmap LOAD_ANSWER.");
                        Bitmap bitmap = (Bitmap) msg.obj;
                        if (bitmap != null) {
                            LogUtils.d("TaskTest drawBitmap.");
                            UIUtils.runInMainThread(() ->{
                                if (mNoteBookView2!=null) {
                                    mNoteBookView2.clearAll();
                                    mNoteBookView2.drawBitmap(bitmap);
                                } else if (mNoteBookView != null) {
                                    mNoteBookView.clear();
                                    mNoteBookView.drawBitmap(bitmap);
                                }
                            });
                        }
                        break;
                }
            }
        };
    }


    public static final int LOAD_ANSWER = 1;
    private Handler mHandler;
    private String mCurrentAnswerUrl = "";
    private LoadAnswerRunnable mLoadAnswerRunnable = new LoadAnswerRunnable();
    public class LoadAnswerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                LogUtils.i("TaskTest mCurrentAnswerUrl = " + mCurrentAnswerUrl);
                Bitmap bitmap = Glide.with(mContext.getApplicationContext()).load(mCurrentAnswerUrl)
                        .asBitmap().into(900, 920).get();
                Message message = new Message();
                message.what = LOAD_ANSWER;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
