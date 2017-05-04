package com.rakesh.demoapplication.pojo;

import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    private int duration;
    private Drawable icon;

    public AppList(String name, int duration, Drawable icon) {
        this.name = name;
        this.duration = duration;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getDuration() {
        return "Duration: " + duration + " Seconds";
    }
}
