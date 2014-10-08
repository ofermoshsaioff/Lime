package com.moshaioff.lime;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moshaioff.lime.otto.OttoUtils;
import com.moshaioff.lime.otto.ReadNFCEvent;
import com.moshaioff.lime.tasks.NdefReaderTask;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.yoavram.lime.R;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;


public class ReadActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "Lime";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    // views
    TextView textView;
    ImageView imageView;
    Button updateButton;

    private NfcAdapter mNfcAdapter;
    private Tag tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_view);

        textView = (TextView) findViewById(R.id.read_message);
        imageView = (ImageView) findViewById(R.id.image);
        updateButton = (Button) findViewById(R.id.edit_button);
        updateButton.setOnClickListener(this);

        OttoUtils.getInstance().getBus().register(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Subscribe
    public void answerAvailable(ReadNFCEvent event) {
        String result = event.getResullt();
        textView.setText(result);
        updateButton.setText(getString(R.string.add_more_stuff));
        updateButton.setEnabled(true);
        String imageUri = extractImageUri(result);
        if (StringUtils.isNotBlank(imageUri)) {
            Picasso.with(this).load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(imageView);

            imageView.setVisibility(View.VISIBLE);
        }
    }

    private String extractImageUri(String text) {
        Matcher matcher = Const.IMAGE_URL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
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
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
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

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {

        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_button:
                //TODO - check if tag == null, nowhere to write to..
                startActivity(new Intent(ReadActivity.this, WriteActivity.class)
                .putExtra(Const.EXTRA_TEXT, textView.getText())
                .putExtra(Const.EXTRA_TAG, tag));
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_bottom);
                break;
        }
    }
}
