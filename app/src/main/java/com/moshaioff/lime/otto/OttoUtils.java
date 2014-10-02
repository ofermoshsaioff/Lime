package com.moshaioff.lime.otto;

import com.squareup.otto.Bus;

/**
 * Created by ofer on 10/2/14.
 */
public class OttoUtils {

    public static OttoUtils instance = new OttoUtils();

    private Bus bus;

    public OttoUtils() {
        bus = new Bus();
    }

    public static OttoUtils getInstance() {
        return instance;
    }

    public Bus getBus() {
        return bus;
    }
}
