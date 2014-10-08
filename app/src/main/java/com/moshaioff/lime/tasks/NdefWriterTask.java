package com.moshaioff.lime.tasks;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.widget.Toast;

import com.moshaioff.lime.LimeApplication;

import java.io.UnsupportedEncodingException;

/**
 * Created by ofer on 10/8/14.
 */
public class NdefWriterTask extends AsyncTask<String, Void, Boolean> {

    private Tag tag;

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        boolean isSuccess = true;

        try {
            NdefRecord[] records = { createRecord(params[0])};
            NdefMessage message = new NdefMessage(records);

            // Get an instance of Ndef for the tag.
            Ndef ndef = Ndef.get(tag);

            // Enable I/O
            ndef.connect();

            // Write the message
            ndef.writeNdefMessage(message);

            // Close the connection
            ndef.close();


        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                payload);

        return record;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        String msg;
        if (isSuccess) {
            msg = "NFC Tag updated successfully!";
        } else {
            msg = "Could not write to NFC tag, try to bring the tag a little bit closer!";
        }
        Toast.makeText(LimeApplication.instance, msg, Toast.LENGTH_LONG).show();
    }
}
