package com.artifex.mupdfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

import com.artifex.mupdfdemo.pdf.alert.MuPDFAlert;
import com.artifex.mupdfdemo.pdf.alert.MuPDFAlertInternal;
import com.artifex.mupdfdemo.pdf.bean.Annotation;
import com.artifex.mupdfdemo.pdf.bean.BitmapHolder;
import com.artifex.mupdfdemo.pdf.bean.LinkInfo;
import com.artifex.mupdfdemo.pdf.bean.PassClickResult;
import com.artifex.mupdfdemo.pdf.bean.PassClickResultChoice;
import com.artifex.mupdfdemo.pdf.bean.PassClickResultText;
import com.artifex.mupdfdemo.pdf.bean.TextChar;
import com.artifex.mupdfdemo.pdf.bean.TextWord;
import com.artifex.mupdfdemo.pdf.enumType.WidgetType;

import java.util.ArrayList;


/**
 * JNI 接口 ,核心类 对PDF的操作
 */
public class MuPDFCore {
    static {
        System.loadLibrary("mupdf");
    }

    //File
    private int numPages = -1;
    private float pageWidth;
    private float pageHeight;
    private long globals;
    private byte fileBuffer[];
    private String file_format;

    //Native method


    /**打开PDF 文件*/
    private native long openFile(String filename);
    private native long openBuffer();
    /**内部文件的格式*/
    private native String fileFormatInternal();

    /**返回总PDF页数*/
    private native int countPagesInternal();
    /**跳转到指定的页数*/
    private native void gotoPageInternal(int localActionPageNum);

    /** 获取 当前页数的 宽度*/
    private native float getPageWidth();
    /** 获取 当前页数的 高度*/
    private native float getPageHeight();

    /** 画 这一页的内容*/
    private native void drawPage(Bitmap bitmap, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH);

    /**更新 当前页*/
    private native void updatePageInternal(Bitmap bitmap, int page, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH);

    /**搜索当前页 文本内容，返回 一个矩形数组*/
    private native RectF[] searchPage(String text);

    private native TextChar[][][][] text();

    private native byte[] textAsHtml();

    /**添加 标记 注解*/
    private native void addMarkupAnnotationInternal(PointF[] quadPoints, int type);

    /**添加链接*/
    private native void addInkAnnotationInternal(PointF[][] arcs);

    /**删除 注解*/
    private native void deleteAnnotationInternal(int annot_index);

    /**允许一个内部的点击事件*/
    private native int passClickEventInternal(int page, float x, float y);


    /**设置一个 有焦点的 选择框*/
    private native void setFocusedWidgetChoiceSelectedInternal(String[] selected);


    private native String[] getFocusedWidgetChoiceSelected();

    private native String[] getFocusedWidgetChoiceOptions();

    private native int setFocusedWidgetTextInternal(String text);

    private native String getFocusedWidgetTextInternal();

    private native int getFocusedWidgetTypeInternal();

    private native LinkInfo[] getPageLinksInternal(int page);

    private native RectF[] getWidgetAreasInternal(int page);

    private native Annotation[] getAnnotationsInternal(int page);

    private native OutlineItem[] getOutlineInternal();

    private native boolean hasOutlineInternal();

    private native boolean needsPasswordInternal();

    private native boolean authenticatePasswordInternal(String password);

    private native MuPDFAlertInternal waitForAlertInternal();

    private native void replyToAlertInternal(MuPDFAlertInternal alert);

    private native void startAlertsInternal();

    private native void stopAlertsInternal();

    private native void destroying();

    private native boolean hasChangesInternal();

    private native void saveInternal();

    public static native boolean javascriptSupported();



    // 对外提供了 一些 公共的 并且 同步的 操作

    public MuPDFCore(Context context, String filename) throws Exception {
        // 获取文件长度
        globals = openFile(filename);
        // 抛出异常
        if (globals == 0) {
            throw new Exception("无法 打开当前的 PDF fileName == "+filename);
        }
        file_format = fileFormatInternal();
    }

    public MuPDFCore(Context context, byte buffer[]) throws Exception {
        fileBuffer = buffer;
        globals = openBuffer();
        if (globals == 0) {
//            throw new Exception(context.getString(R.string.cannot_open_buffer));
        }
        file_format = fileFormatInternal();
    }

    public int countPages() {
        if (numPages < 0)
            numPages = countPagesSynchronized();

        return numPages;
    }

    public String fileFormat() {
        return file_format;
    }

    private synchronized int countPagesSynchronized() {
        return countPagesInternal();
    }

    /* Shim function */
    private void gotoPage(int page) {
        if (page > numPages - 1)
            page = numPages - 1;
        else if (page < 0)
            page = 0;
        gotoPageInternal(page);
        this.pageWidth = getPageWidth();
        this.pageHeight = getPageHeight();
    }

    public synchronized PointF getPageSize(int page) {
        gotoPage(page);
        return new PointF(pageWidth, pageHeight);
    }

    public MuPDFAlert waitForAlert() {
        MuPDFAlertInternal alert = waitForAlertInternal();
        return alert != null ? alert.toAlert() : null;
    }

    public void replyToAlert(MuPDFAlert alert) {
        replyToAlertInternal(new MuPDFAlertInternal(alert));
    }

    public void stopAlerts() {
        stopAlertsInternal();
    }

    public void startAlerts() {
        startAlertsInternal();
    }

    public synchronized void onDestroy() {
        destroying();
        globals = 0;
    }

    public synchronized Bitmap drawPage(int page, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH) {
        gotoPage(page);
        Bitmap bm = Bitmap.createBitmap(patchW, patchH, Bitmap.Config.ARGB_8888);
        drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH);
        return bm;
    }

    public synchronized Bitmap updatePage(BitmapHolder h, int page, int pageW, int pageH, int patchX, int patchY, int patchW, int patchH) {
        Bitmap bm = null;
        Bitmap old_bm = h.getBm();

        if (old_bm == null)
            return null;

        bm = old_bm.copy(Bitmap.Config.ARGB_8888, false);
        old_bm = null;

        updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH);
        return bm;
    }

    public synchronized PassClickResult passClickEvent(int page, float x, float y) {
        boolean changed = passClickEventInternal(page, x, y) != 0;

        switch (WidgetType.values()[getFocusedWidgetTypeInternal()]) {
            case TEXT:
                return new PassClickResultText(changed, getFocusedWidgetTextInternal());
            case LISTBOX:
            case COMBOBOX:
                return new PassClickResultChoice(changed, getFocusedWidgetChoiceOptions(), getFocusedWidgetChoiceSelected());
            default:
                return new PassClickResult(changed);
        }

    }

    public synchronized boolean setFocusedWidgetText(int page, String text) {
        boolean success;
        gotoPage(page);
        success = setFocusedWidgetTextInternal(text) != 0;

        return success;
    }

    public synchronized void setFocusedWidgetChoiceSelected(String[] selected) {
        setFocusedWidgetChoiceSelectedInternal(selected);
    }

    public synchronized LinkInfo[] getPageLinks(int page) {
        return getPageLinksInternal(page);
    }

    public synchronized RectF[] getWidgetAreas(int page) {
        return getWidgetAreasInternal(page);
    }

    public synchronized Annotation[] getAnnoations(int page) {
        return getAnnotationsInternal(page);
    }

    public synchronized RectF[] searchPage(int page, String text) {
        gotoPage(page);
        return searchPage(text);
    }

    public synchronized byte[] html(int page) {
        gotoPage(page);
        return textAsHtml();
    }

    public synchronized TextWord[][] textLines(int page) {
        gotoPage(page);
        TextChar[][][][] chars = text();

        // The text of the page held in a hierarchy (blocks, lines, spans).
        // Currently we don't need to distinguish the blocks level or
        // the spans, and we need to collect the text into words.
        ArrayList<TextWord[]> lns = new ArrayList<TextWord[]>();

        for (TextChar[][][] bl : chars) {
            for (TextChar[][] ln : bl) {
                ArrayList<TextWord> wds = new ArrayList<TextWord>();
                TextWord wd = new TextWord();

                for (TextChar[] sp : ln) {
                    for (TextChar tc : sp) {
                        if (tc.c != ' ') {
                            wd.Add(tc);
                        } else if (wd.w.length() > 0) {
                            wds.add(wd);
                            wd = new TextWord();
                        }
                    }
                }

                if (wd.w.length() > 0)
                    wds.add(wd);

                if (wds.size() > 0)
                    lns.add(wds.toArray(new TextWord[wds.size()]));
            }
        }

        return lns.toArray(new TextWord[lns.size()][]);
    }

    public synchronized void addMarkupAnnotation(int page, PointF[] quadPoints, Annotation.Type type) {
        gotoPage(page);
        addMarkupAnnotationInternal(quadPoints, type.ordinal());
    }

    public synchronized void addInkAnnotation(int page, PointF[][] arcs) {
        gotoPage(page);
        addInkAnnotationInternal(arcs);
    }

    public synchronized void deleteAnnotation(int page, int annot_index) {
        gotoPage(page);
        deleteAnnotationInternal(annot_index);
    }

    public synchronized boolean hasOutline() {
        return hasOutlineInternal();
    }

    public synchronized OutlineItem[] getOutline() {
        return getOutlineInternal();
    }

    public synchronized boolean needsPassword() {
        return needsPasswordInternal();
    }

    public synchronized boolean authenticatePassword(String password) {
        return authenticatePasswordInternal(password);
    }

    public synchronized boolean hasChanges() {
        return hasChangesInternal();
    }

    public synchronized void save() {
        saveInternal();
    }
}


