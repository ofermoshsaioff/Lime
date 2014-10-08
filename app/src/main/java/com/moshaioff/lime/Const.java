package com.moshaioff.lime;

import java.util.regex.Pattern;

/**
 * Created by ofer on 9/17/14.
 */
public class Const {

    public static final int MAX_CHARS = 180;

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String EXTRA_TEXT = ".EXTRA_TEXT";
    public static final String EXTRA_TAG = ".EXTRA_TAG";

    public static final class JSON {
        public static final String EXTRA_URL = "url";
    }

    public static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
            "(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

}
