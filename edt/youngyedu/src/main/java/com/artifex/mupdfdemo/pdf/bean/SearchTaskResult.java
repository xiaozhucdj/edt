package com.artifex.mupdfdemo.pdf.bean;

import android.graphics.RectF;

/**
 * Created by Administrator on 2016/6/29.
 */
public class SearchTaskResult {

    public final String txt;
    public final int   pageNumber;
    public final RectF searchBoxes[];
    static private SearchTaskResult singleton;

    SearchTaskResult(String _txt, int _pageNumber, RectF _searchBoxes[]) {
        txt = _txt;
        pageNumber = _pageNumber;
        searchBoxes = _searchBoxes;
    }

    static public SearchTaskResult get() {
        return singleton;
    }

    static public void set(SearchTaskResult r) {
        singleton = r;
    }

}
