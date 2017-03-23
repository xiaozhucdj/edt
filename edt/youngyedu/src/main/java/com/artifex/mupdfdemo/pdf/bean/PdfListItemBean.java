package com.artifex.mupdfdemo.pdf.bean;

/**
 * .
 */
public class PdfListItemBean {

    public enum Type {
        PARENT, DIR, DOC
    }

    final public Type type;
    final public String name;

    public PdfListItemBean(Type t, String n) {
        type = t;
        name = n;
    }
}
