package com.moshaioff.lime;

import com.yoavram.lime.R;

import java.util.regex.Pattern;

/**
 * Created by ofer on 9/17/14.
 */
public class Const {

    public static final int MAX_CHARS = 180;

    public static final class JSON {
        public static final String EXTRA_URL = "url";
    }

    public static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
            "(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static int[] colorPallette = new int[] {
        R.color.pastel_green, R.color.pastel_dark_green, R.color.pastel_light_green,
        R.color.pastel_beige, R.color.pastel_red, R.color.pastel_orange};
}
