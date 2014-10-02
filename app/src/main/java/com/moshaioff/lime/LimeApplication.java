package com.moshaioff.lime;

import android.app.Application;
import android.content.ContentResolver;

/**
 * Created by ofer on 10/2/14.
 */
public class LimeApplication extends Application {

    public static LimeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ContentResolver contentResolver() {
        return instance.getContentResolver();
    }


}
