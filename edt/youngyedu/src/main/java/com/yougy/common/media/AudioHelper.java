package com.yougy.common.media;

import android.content.Context;
import android.media.AudioManager;

import com.yougy.common.utils.LogUtils;

/**
 * 媒体音量
 */
public class AudioHelper {

    private final AudioManager mAudioManager;
    private final int maxVolume;
    private int stepVolume;
    private int curVolume;

    public AudioHelper(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        LogUtils.e("当前设备最大音量大小：" + maxVolume);

        curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        LogUtils.e("当前设备音量大小：" + curVolume);
        // 每次调整的音量大概为最大音量的1/5
//        stepVolume = maxVolume / 5;
        stepVolume = 1;
    }

    public int getCurVolume() {
        return curVolume;
    }

    public int getMaxVolume() {
        return maxVolume;
    }

    /**
     * 放大 音量
     */
    public int setEnlargeVoice() {
        curVolume += stepVolume;
        if (curVolume >= maxVolume) {
            curVolume = maxVolume;
        }
        adjustVolume();
        return curVolume;
    }

    public int setCutterVoice(int voice) {
        curVolume = voice;
        if (curVolume <= 0) {
            curVolume = 0;
        } else if (curVolume >= maxVolume) {
            curVolume = maxVolume;
        }
        adjustVolume();
        return curVolume;
    }

    /**
     * 减小音量
     */
    public int setReduceVoice() {
        curVolume -= stepVolume;
        if (curVolume <= 0) {
            curVolume = 0;
        }
        adjustVolume();
        return curVolume;
    }

    /**
     * 调整音量
     */
    private void adjustVolume() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }


    public void setMaxVolume() {
        adjustVolumeMax();
    }

    public void adjustVolumeMax() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }
}