package com.yougy.common.media;

import android.content.Context;
import android.media.AudioManager;

/**
 *  媒体音量
 */
public class AudioHelper {

    private final AudioManager mAudioManager;
    private final int maxVolume;
    private int stepVolume;
    private int curVolume;

    public AudioHelper(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 初始化音量大概为最大音量的1/2
        curVolume = maxVolume / 2;
        // 每次调整的音量大概为最大音量的1/6
        stepVolume = maxVolume / 6;
    }


    /**
     * 放大 音量
     */
    public void setEnlargeVoice() {
        curVolume += stepVolume;
        if (curVolume >= maxVolume) {
            curVolume = maxVolume;
        }
        adjustVolume();
    }

    /**
     * 减小音量
     */
    public void setReduceVoice() {
        curVolume -= stepVolume;
        if (curVolume <= 0) {
            curVolume = 0;
        }
        adjustVolume();
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