package com.moshaioff.lime.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.moshaioff.lime.Const;
import com.moshaioff.lime.R;
import com.moshaioff.lime.gallery.GalleryItem;
import com.moshaioff.lime.gallery.ImageAdapter;
import com.moshaioff.lime.otto.OttoUtils;
import com.moshaioff.lime.otto.ReadNFCEvent;
import com.moshaioff.lime.tasks.NdefReadResult;
import com.moshaioff.lime.tasks.NdefReaderTask;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;


public class ReadActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "Lime";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    // views
    TextView textView;
    GridView gridView;
    Button updateButton;

    //private NfcAdapter mNfcAdapter;
    private Tag tag;
    private int maxLength;
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_view);

        textView = (TextView) findViewById(R.id.read_message);
        gridView = (GridView) findViewById(R.id.image_grid_view);
        updateButton = (Button) findViewById(R.id.edit_button);
        updateButton.setOnClickListener(this);

        imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(this);

        OttoUtils.getInstance().getBus().register(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Subscribe
    public void answerAvailable(ReadNFCEvent event) {
        NdefReadResult result = event.getResullt();
        String text = result.getText();
        maxLength = result.getMaxLength();
        textView.setText(text);
        updateButton.setText(getString(R.string.add_more_stuff));
        updateButton.setEnabled(true);
        String imageUri = extractImageUri(text);
        if (StringUtils.isNotBlank(imageUri)) {
            imageAdapter.clear();
            imageAdapter.addImage(new GalleryItem(imageUri));
            imageAdapter.notifyDataSetChanged();
        }
    }

    private String extractImageUri(String text) {
        if (StringUtils.isNotBlank(text)) {
            Matcher matcher = Const.IMAGE_URL_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }

    @Override
        protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_button:
                startActivity(new Intent(ReadActivity.this, WriteActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Const.EXTRA_TEXT, textView.getText().toString())
                        .putExtra(Const.EXTRA_TAG, tag)
                        .putExtra(Const.EXTRA_TAG_SIZE, maxLength));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_bottom);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(ReadActivity.this, ImageFullScreenActivity.class)
        .putExtra(Const.EXTRA_IMAGE_URI, imageAdapter.getItem(i).getUri()));
    }
}
