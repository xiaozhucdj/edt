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
public class NewMediaHelper {
    // 播放音频控件
    private MediaPlayer mMediaPlayer;
    private String mUrl;
    private Activity mActivity;
    //资源是否准备完成
    private boolean isPrepare;
    private int mVoicePs = 1;

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
                if (mListener != null) {
                    mListener.onCompletionPlayerListener(mVoicePs);
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
                return false;
            }
        });

        //  当装载流媒体完毕的时候回调
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepare = true;
                mp.start();
            }
        });
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
    }

    //暂停
    public void pause() {
        if (isPlay())
            mMediaPlayer.pause();
    }

    // 继续播放
    public void continuePlay() {
        if (!isPlay())
            mMediaPlayer.start();
    }

    public void reset() {
        isPrepare = false;
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }

    }

    private void changeUrl() {
        isPrepare = false;//进入资源为准备状态
        mMediaPlayer.reset();//初始化
        start();//开始播放
    }


    public void start(String url, int voicePs) {

        mVoicePs = voicePs;

        if (StringUtils.isEmpty(url) || !FileUtils.exists(url)) {
            UIUtils.showToastSafe("播放错误 ，语音文件不存在");
            return;
        }

        if (StringUtils.isEmpty(mUrl)) {
            this.mUrl = url;
            start();
            LogUtils.e("start ....... mUrl === null");
        } else {
            this.mUrl = url;
            LogUtils.e(" changeUrl...........................");
            changeUrl();
        }

    }

    public CompletionPlayerListener mListener;

    public void setCompletionListener(CompletionPlayerListener listener) {
        mListener = listener;
    }

    public interface CompletionPlayerListener {
        void onCompletionPlayerListener(int voicePs);
    }

    public void playerRelease() {
        if (mMediaPlayer != null) {
            pause();
            mMediaPlayer.release();
        }
    }

}