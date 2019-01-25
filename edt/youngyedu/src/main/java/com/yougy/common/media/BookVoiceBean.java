package com.yougy.common.media;

import android.support.annotation.NonNull;

import java.util.List;

public class BookVoiceBean {

    private int page;
    private List<VoiceBean> voiceInfos;

    public static class VoiceBean implements Comparable<VoiceBean> {
        private int postion;
        public String url;
        public int start;
        public int end;
        private int viewLeft;
        private int viewTop;

        public int getPostion() {
            return postion;
        }

        public String getUrl() {
            return url;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int getViewLeft() {
            return viewLeft;
        }

        public int getViewTop() {
            return viewTop;
        }

        @Override
        public int compareTo(@NonNull VoiceBean voiceBean) {
            return this.postion - voiceBean.postion;
        }

        @Override
        public String toString() {
            return "VoiceBean{" +
                    "postion=" + postion +
                    ", url='" + url + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", viewLeft=" + viewLeft +
                    ", viewTop=" + viewTop +
                    '}';
        }
    }

    public int getPage() {
        return page;
    }

    public List<VoiceBean> getVoiceInfos() {
        return voiceInfos;
    }
}
