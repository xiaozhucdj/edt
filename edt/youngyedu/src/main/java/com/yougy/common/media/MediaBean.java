package com.yougy.common.media;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * {
 * "page": 1,
 * <p>
 * "cutterPageInfos": [{
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000
 * },
 * {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000
 * },
 * {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000
 * },
 * {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000
 * }
 * ],
 * <p>
 * "cutterPageChilds": [
 * <p>
 * {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000,
 * "viewLeft": 200,
 * "viewTop": 300
 * }, {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000,
 * "viewLeft": 200,
 * "viewTop": 300
 * }, {
 * "url": "..mp3",
 * "start": 0,
 * "end": 25000,
 * "viewLeft": 200,
 * "viewTop": 300
 * }
 * <p>
 * ]
 * }
 * <p>
 * }
 */


public class MediaBean {
    /**
     * page : 1
     * cutterPageInfos : [{"url":"..mp3","start":0,"end":25000},{"url":"..mp3","start":0,"end":25000},{"url":"..mp3","start":0,"end":25000},{"url":"..mp3","start":0,"end":25000}]
     * cutterPageChilds : [{"url":"..mp3","start":0,"end":25000,"viewLeft":200,"viewTop":300},{"url":"..mp3","start":0,"end":25000,"viewLeft":200,"viewTop":300},{"url":"..mp3","start":0,"end":25000,"viewLeft":200,"viewTop":300}]
     */

    private int page;
    private List<CutterPageInfosBean> cutterPageInfos;
    private List<CutterPageChildsBean> cutterPageChilds;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<CutterPageInfosBean> getCutterPageInfos() {
        return cutterPageInfos;
    }

    public void setCutterPageInfos(List<CutterPageInfosBean> cutterPageInfos) {
        this.cutterPageInfos = cutterPageInfos;
    }

    public List<CutterPageChildsBean> getCutterPageChilds() {
        return cutterPageChilds;
    }

    public void setCutterPageChilds(List<CutterPageChildsBean> cutterPageChilds) {
        this.cutterPageChilds = cutterPageChilds;
    }

    public static class CutterPageInfosBean implements Comparable<CutterPageInfosBean> {
        /**
         * url : ..mp3
         * start : 0
         * end : 25000
         */

        private String url;
        private int start;
        private int end;
        private int postion;

        public int getPostion() {
            return postion;
        }

        public void setPostion(int postion) {
            this.postion = postion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return "CutterPageInfosBean{" +
                    "url='" + url + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }

        @Override
        public int compareTo(@NonNull CutterPageInfosBean cutterPageInfosBean) {
            return   this.postion - cutterPageInfosBean.postion;
        }
    }

    public static class CutterPageChildsBean {
        /**
         * url : ..mp3
         * start : 0
         * end : 25000
         * viewLeft : 200
         * viewTop : 300
         */

        private String url;
        private int start;
        private int end;
        private int viewLeft;
        private int viewTop;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getViewLeft() {
            return viewLeft;
        }

        public void setViewLeft(int viewLeft) {
            this.viewLeft = viewLeft;
        }

        public int getViewTop() {
            return viewTop;
        }

        public void setViewTop(int viewTop) {
            this.viewTop = viewTop;
        }

        @Override
        public String toString() {
            return "CutterPageChildsBean{" +
                    "url='" + url + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", viewLeft=" + viewLeft +
                    ", viewTop=" + viewTop +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MediaBean{" +
                "page=" + page +
                ", cutterPageInfos=" + cutterPageInfos +
                ", cutterPageChilds=" + cutterPageChilds +
                '}';
    }
}
