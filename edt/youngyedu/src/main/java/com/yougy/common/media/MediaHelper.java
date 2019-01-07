package com.yougy.common.media;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.yougy.common.manager.ThreadManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;

import java.io.IOException;

/**
 * void statr()：开始或恢复播放。
 * void stop()：停止播放。
 * void pause()：暂停播放。
 * 　　通过上面三个方法，只要设定好流媒体数据源，即可在应用中播放流媒体资源，为了更好的操作流媒体，MediaPlayer还为我们提供了一些其他的方法，这里列出一些常用的，详细内容参阅官方文档。
 * <p>
 * int getDuration()：获取流媒体的总播放时长，单位是毫秒。
 * int getCurrentPosition()：获取当前流媒体的播放的位置，单位是毫秒。
 * void seekTo(int msec)：设置当前MediaPlayer的播放位置，单位是毫秒。
 * void setLooping(boolean looping)：设置是否循环播放。
 * boolean isLooping()：判断是否循环播放。
 * boolean  isPlaying()：判断是否正在播放。
 * void prepare()：同步的方式装载流媒体文件。
 * void prepareAsync()：异步的方式装载流媒体文件。
 * void release ()：回收流媒体资源。
 * void setAudioStreamType(int streamtype)：设置播放流媒体类型。
 * void setWakeMode(Context context, int mode)：设置CPU唤醒的状态。
 * setNextMediaPlayer(MediaPlayer next)：设置当前流媒体播放完毕，下一个播放的MediaPlayer。
 * 大部分方法的看方法名就可以理解，但是有几个方法需要单独说明一下。
 * <p>
 * 在使用MediaPlayer播放一段流媒体的时候，需要使用prepare()或prepareAsync()方法把流媒体装载进MediaPlayer，才可以调用start()方法播放流媒体。
 * <p>
 * setAudioStreamType()方法用于指定播放流媒体的类型，它传递的是一个int类型的数据，均以常量定义在AudioManager类中， 一般我们播放音频文件，设置为AudioManager.STREAM_MUSIC即可。
 */
public class MediaHelper {
    private String tag = "MediaHelper";
    // 播放音频控件
    private MediaPlayer mMediaPlayer;

    private String mUrl;

    private Activity mActivity;
    //资源是否准备完成
    private boolean isPrepare;
    private long DURATION_LOOP_CUTTER = 1;
    private int mStartTime;
    private int mEndTime;
    private LoopCutterRunnale mLoopCutterRunnale;

    /***
     * 初始化
     */
    public void init(Activity activity) {
        mActivity = activity;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 当流媒体播放完毕的时候回调
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepare = false;
                mp.reset();
                mLoopCutterRunnale.stop();
                if (mListener != null) {
                    mListener.onCompletionPlayerListener();
                }
            }
        });

        // 当播放中发生错误的时候回调
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(mActivity, "播放失败", Toast.LENGTH_SHORT).show();
                isPrepare = false;
                mp.reset();
                mLoopCutterRunnale.stop();
                return false;
            }
        });

        //  当装载流媒体完毕的时候回调
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepare = true;
                mp.start();
                seekTo(mStartTime);
            }
        });

        //  当使用seekTo()设置播放位置的时候回调
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });

        mLoopCutterRunnale = new LoopCutterRunnale();
    }

    private boolean isPlay() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    private void start() {
        try {
            mMediaPlayer.setDataSource(mActivity, Uri.parse(mUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mLoopCutterRunnale.start();
    }

    private void pause() {
        if (isPlay())
            mMediaPlayer.pause();
    }

    private void continuePlay() {
        if (!isPlay())
            mMediaPlayer.start();
    }

    private void seekTo(int position) {
        if (position < 0 || position > mMediaPlayer.getDuration()) return;
        mMediaPlayer.seekTo(position);
    }

    private void changeUrl() {
        isPrepare = false;//进入资源为准备状态
        mMediaPlayer.reset();//初始化
        start();//开始播放
    }

    private int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    private int getDuration() {
        return mMediaPlayer.getDuration();
    }

    private void release() {
        mMediaPlayer.release();
    }

    class LoopCutterRunnale implements Runnable {
        private boolean mIsStart;

        public LoopCutterRunnale() {
            mIsStart = false;
        }

        public void start() {
            if (!mIsStart) {
                mIsStart = true;
                UIUtils.removeCallbacks(this);
                UIUtils.postDelayed(this, DURATION_LOOP_CUTTER);
            }
        }

        public void stop() {
            if (mIsStart) {
                mIsStart = false;
                UIUtils.removeCallbacks(this);
            }
        }

        @Override
        public void run() {
            if (mIsStart) {
                ThreadManager.getShortPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isPrepare && isPlay()) {

                            LogUtils.e(tag, "getCurrentPosition ...." + getCurrentPosition());
                            if (getCurrentPosition() >= mEndTime) {
                                pause();
                                mLoopCutterRunnale.stop();
                                if (mListener != null) {
                                    UIUtils.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mListener.onCompletionPlayerListener();
                                        }
                                    });

                                }
                            }
                        }
                    }
                });
                UIUtils.postDelayed(this, DURATION_LOOP_CUTTER);
            }
        }
    }


    public void player_start(String url, int startTime, int endTime) {

        if (!FileUtils.exists(url)) {
            LogUtils.e(tag, "语音文件不存在 ...."+url);
            return;
        }

        // 第一次播放 mUrl ==""
        LogUtils.e(tag, "url ...." + url);
        if (StringUtils.isEmpty(mUrl)) {
            this.mUrl = url;
            this.mStartTime = startTime;
            this.mEndTime = endTime;
            LogUtils.e(tag, "start ....");
            start();
        } else {
            if (isPrepare()) { // 初始化完成 ，说明当前为暂停状态。
                if (mUrl.equalsIgnoreCase(url)) { //URL 一致 播放和页码没有变化 ，进行继续播放

                    if (endTime == mEndTime) {//当前是用户自动暂停操作
                        LogUtils.e(tag, "continuePlay ....");
                        continuePlay();
                        mLoopCutterRunnale.start();
                    } else {//时间戳暂停
                        this.mStartTime = startTime;
                        this.mEndTime = endTime;
                        continuePlay();
                        LogUtils.e(tag, "seekTo ....");
                        seekTo(startTime);
                        mLoopCutterRunnale.start();
                    }
                } else {// URL不一样 可能出现 音频和页码变化
                    LogUtils.e(tag, "changeUrl ....1");
                    this.mUrl = url;
                    this.mStartTime = startTime;
                    this.mEndTime = endTime;
                    changeUrl();
                    mLoopCutterRunnale.start();
                }

            } else { //未进行初始化 ，说明当前 文件播放完成  或者切换页码了。
                LogUtils.e(tag, "changeUrl ....2");
                this.mUrl = url;
                this.mStartTime = startTime;
                this.mEndTime = endTime;
                changeUrl();
                mLoopCutterRunnale.start();
            }
        }
    }

    public void player_pause() {
        mLoopCutterRunnale.stop();
        pause();
    }

    public void player_reset() {
        isPrepare = false;//进入资源为准备状态
        mMediaPlayer.reset();//初始化
        mLoopCutterRunnale.stop();
    }

    public void player_release() {
        if (mMediaPlayer != null) {
            pause();
            mLoopCutterRunnale.stop();
            release();
        }
    }

    public CompletionPlayerListener mListener;

    public void setListener(CompletionPlayerListener listener) {
        mListener = listener;
    }

    public interface CompletionPlayerListener {
        void onCompletionPlayerListener();
    }
}
