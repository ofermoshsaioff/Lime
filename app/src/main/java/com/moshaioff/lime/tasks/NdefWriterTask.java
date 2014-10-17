package com.moshaioff.lime.tasks;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.widget.Toast;

import com.moshaioff.lime.LimeApplication;
import com.moshaioff.lime.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by ofer on 10/8/14.
 */
public class NdefWriterTask extends AsyncTask<String, Void, NdefWriteResult> {

    private Tag tag;

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    protected NdefWriteResult doInBackground(String... params) {

        NdefWriteResult result;
        String msg = LimeApplication.instance.getString(R.string.write_success_msg);
        boolean isSuccess = true;
        String text = params[0];

        try {
            NdefRecord[] records = { createRecord(text)};
            NdefMessage message = new NdefMessage(records);

            // Get an instance of Ndef for the tag.
            Ndef ndef = Ndef.get(tag);

            int size = ndef.getMaxSize();
            if (text.length() > size) {
                result = new NdefWriteResult(false, LimeApplication.instance.getString(R.string.message_to_long_error, size));
                return result;
            }

            // Enable I/O
            ndef.connect();

            // Write the message
            ndef.writeNdefMessage(message);

            // Close the connection
            ndef.close();


        } catch (Exception e) {
            msg = LimeApplication.instance.getString(R.string.write_error_msg);
            e.printStackTrace();
            isSuccess = false;
        }
        result = new NdefWriteResult(isSuccess, msg);
        return result;
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
    protected void onPostExecute(NdefWriteResult result) {
        Toast.makeText(LimeApplication.instance, result.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
