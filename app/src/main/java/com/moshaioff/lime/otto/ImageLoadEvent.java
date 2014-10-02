package com.moshaioff.lime.otto;

/**
 * Created by ofer on 10/2/14.
 */
public class ImageLoadEvent {

    String path;

    public ImageLoadEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
