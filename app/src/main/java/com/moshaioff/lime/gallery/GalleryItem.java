package com.moshaioff.lime.gallery;

import android.net.Uri;

/**
 * Created by ofer on 10/9/14.
 */
public class GalleryItem {

    public GalleryItem(Uri imageUri) {
        this.uri = imageUri;
    }
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

}
