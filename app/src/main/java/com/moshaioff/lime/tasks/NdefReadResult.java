package com.moshaioff.lime.tasks;

/**
 * Created by ofer on 10/9/14.
 */
public class NdefReadResult {

    public NdefReadResult(String text, int maxLength) {
        this.text = text;
        this.maxLength = maxLength;
    }

    private String text;
    private int maxLength;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
