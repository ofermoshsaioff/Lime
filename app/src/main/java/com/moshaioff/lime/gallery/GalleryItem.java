package com.moshaioff.lime.gallery;

/**
 * Created by ofer on 10/9/14.
 */
public class GalleryItem {

    public GalleryItem(String imageUri) {
        this.uri = imageUri;
    }
    private String uri;

    public String getUri() {
        return uri;
    }

}
