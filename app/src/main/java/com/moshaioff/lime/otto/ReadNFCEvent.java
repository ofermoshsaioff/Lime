package com.moshaioff.lime.otto;

import com.moshaioff.lime.tasks.NdefReadResult;

/**
 * Created by ofer on 10/8/14.
 */
public class ReadNFCEvent {

    public ReadNFCEvent(NdefReadResult resullt) {
        this.resullt = resullt;
    }

    public NdefReadResult getResullt() {
        return resullt;
    }

    private NdefReadResult resullt;
}
