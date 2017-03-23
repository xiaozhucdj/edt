package com.artifex.mupdfdemo.pdf.PdfInterface;


import com.artifex.mupdfdemo.pdf.bean.TextWord;

/**
 * Created by Administrator on 2016/6/29.
 * 文本的处理器
 */
public interface TextProcessor {
    void onStartLine();
    void onWord(TextWord word);
    void onEndLine();
}
