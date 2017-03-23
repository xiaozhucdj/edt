package com.onyx.android.sdk.data.util;

import android.graphics.Color;

import java.util.HashSet;
import java.util.Set;

public class Config {
    private boolean applyTextColor = false;
    private int textColor = Color.BLACK;
    private boolean fakeBold = true;

    private boolean applyFillColor = false;
    private int fillColor = Color.WHITE;

    private boolean a2 = false;
    private Set<String> skip = new HashSet<>();

    private boolean applyIconColorFilter = false;

    public Config(){

    }

    public boolean isApplyTextColor() {
        return applyTextColor;
    }

    public void setApplyTextColor(boolean applyTextColor) {
        this.applyTextColor = applyTextColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isFakeBold() {
        return fakeBold;
    }

    public void setFakeBold(boolean fakeBold) {
        this.fakeBold = fakeBold;
    }

    public boolean isApplyFillColor() {
        return applyFillColor;
    }

    public void setApplyFillColor(boolean applyFillColor) {
        this.applyFillColor = applyFillColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public boolean isA2() {
        return a2;
    }

    public void setA2(boolean a2) {
        this.a2 = a2;
    }

    public Set<String> getSkip() {
        return skip;
    }

    public void setSkip(Set<String> skip) {
        this.skip = skip;
    }

    public boolean isApplyIconColorFilter() {
        return applyIconColorFilter;
    }

    public void setApplyIconColorFilter(boolean applyIconColorFilter) {
        this.applyIconColorFilter = applyIconColorFilter;
    }
}