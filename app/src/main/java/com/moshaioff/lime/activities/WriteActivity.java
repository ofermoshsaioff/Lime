package com.moshaioff.lime.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moshaioff.lime.Const;
import com.moshaioff.lime.R;
import com.moshaioff.lime.gallery.GalleryItem;
import com.moshaioff.lime.gallery.ImageAdapter;
import com.moshaioff.lime.otto.ImageLoadEvent;
import com.moshaioff.lime.otto.OttoUtils;
import com.moshaioff.lime.tasks.NdefWriterTask;
import com.moshaioff.lime.utils.CloudinaryUtils;
import com.moshaioff.lime.utils.FileUtils;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Created by ofer on 10/8/14.
 * write NFC Activity
 */
public class WriteActivity extends Activity implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    private static final String TAG = WriteActivity.class.getName();
    // views
    private EditText editText;
    private TextView counterText;
    private Button sendButton;
    private ImageView cameraButton;
    private GridView gridView;

    // members
    private NfcAdapter mNfcAdapter;
    private ImageAdapter imageAdapter;
    private Tag tag;
    private File photoFile;
    private int maxLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_view);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        imageAdapter = new ImageAdapter(this);

        Bundle b = getIntent().getExtras();
        tag = b.getParcelable(Const.EXTRA_TAG);
        String text = (String) b.get(Const.EXTRA_TEXT);
        maxLength = (Integer)b.get(Const.EXTRA_TAG_SIZE);
        OttoUtils.getInstance().getBus().register(this);

        counterText = (TextView) findViewById(R.id.counter_text);
        editText = (EditText) findViewById(R.id.edit_text);
        sendButton = (Button) findViewById(R.id.send_button);
        cameraButton = ((ImageView) findViewById(R.id.camera_button));
        gridView = (GridView) findViewById(R.id.image_grid_view);

        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(this);

        sendButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        editText.addTextChangedListener(this);

        editText.setText(text);
        String imageUri = extractImageUri(text);
        if (StringUtils.isNotBlank(imageUri)) {
            imageAdapter.addImage(new GalleryItem(imageUri));
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
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
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (Const.MIME_TEXT_PLAIN.equals(type)) {

                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Toast.makeText(this, getString(R.string.tag_connected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link android.nfc.NfcAdapter} used for the foreground dispatch.
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
            filters[0].addDataType(Const.MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_right);
    }

    @Subscribe
    public void answerAvailable(ImageLoadEvent event) {
        String path = event.getPath();
        editText.append('\n'+path+'\n');
        updateCounterText();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            addFileToGallery();

            CloudinaryUtils.getInstance().uploadImage(Uri.fromFile(photoFile));
            imageAdapter.addImage(new GalleryItem(photoFile.getAbsolutePath()));
            imageAdapter.notifyDataSetChanged();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addFileToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = FileUtils.createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            }
            startActivityForResult(takePictureIntent, Const.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button:
                NdefWriterTask ndefWriterTask = new NdefWriterTask();
                ndefWriterTask.setTag(tag);
                ndefWriterTask.execute(editText.getText().toString());
                break;
            case R.id.camera_button:
                takePhoto();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        updateCounterText();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    public void updateCounterText() {
        if (editText != null) {
            counterText.setText(String.valueOf(maxLength - editText.length()));
        } else {
            counterText.setText(String.valueOf(maxLength));
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(WriteActivity.this, ImageFullScreenActivity.class)
        .putExtra(Const.EXTRA_IMAGE_URI, imageAdapter.getItem(i).getUri()));
    }
}
