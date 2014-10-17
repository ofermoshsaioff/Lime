package com.moshaioff.lime;

import java.util.regex.Pattern;

/**
 * Created by ofer on 9/17/14.
 */
public class Const {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_GALLERY = 2;

    public static final String MIME_TEXT_PLAIN = "text/plain";

    public static final String CLOUDINARY_FILE_PREFIX = "http://res.cloudinary.com/lime/image/upload";

    public static final String EXTRA_TEXT = ".EXTRA_TEXT";
    public static final String EXTRA_TAG = ".EXTRA_TAG";
    public static final String EXTRA_TAG_SIZE = ".EXTRA_TAG_SIZE";
    public static final String EXTRA_IMAGE_URI = ".EXTRA_IMAGE_URI";

    public static final class JSON {
        public static final String EXTRA_URL = "url";
    }

    public static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
            "(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

}
