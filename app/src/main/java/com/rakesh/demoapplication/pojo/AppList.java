package com.rakesh.demoapplication.pojo;

import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    private int duration;
    private Drawable icon;
    private long firstinstall, updatetime;
    private boolean isSytemApp;
    private String packagename;

    public AppList(String name, String packagename, int duration, Drawable icon, long firstinstall, long updatetime
            , boolean isSytemApp) {
        this.name = name;
        this.duration = duration;
        this.icon = icon;
        this.firstinstall = firstinstall;
        this.updatetime = updatetime;
        this.isSytemApp = isSytemApp;
        this.packagename = packagename;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getDuration() {
        return "Duration: " + duration + " Seconds";
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getFirstinstall() {
        return firstinstall;
    }

    public void setFirstinstall(long firstinstall) {
        this.firstinstall = firstinstall;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public boolean isSytemApp() {
        return isSytemApp;
    }

    public void setSytemApp(boolean sytemApp) {
        isSytemApp = sytemApp;
    }
}
