package com.moshaioff.lime;

import java.util.regex.Pattern;

/**
 * Created by ofer on 9/17/14.
 */
public class Const {

    public static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
            "(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
}
