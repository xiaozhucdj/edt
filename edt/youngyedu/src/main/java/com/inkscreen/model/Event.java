package com.inkscreen.model;

/**
 *
 */
public class Event<T> {

    private int requestCode;
    private T target;

    public Event(int requestCode, T target) {
        this.target = target;
        this.requestCode = requestCode;
    }
    public Event(int requestCode) {
        this.requestCode = requestCode;
    }
    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
