package com.moshaioff.lime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moshaioff.lime.otto.ImageLoadEvent;
import com.moshaioff.lime.otto.OttoUtils;
import com.moshaioff.lime.tasks.NdefWriterTask;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.yoavram.lime.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by ofer on 10/8/14.
 */
public class WriteActivity extends Activity implements View.OnClickListener, TextWatcher {

    // views
    private EditText editText;
    private FrameLayout imageContainer;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button sendButton;
    private ImageView cameraButton;
    private TextView counterText;

    // members
    private File photoFile;
    private Tag tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_view);

        Bundle b = getIntent().getExtras();
        tag = b.getParcelable(Const.EXTRA_TAG);
        String text = b.getString(Const.EXTRA_TEXT);
        OttoUtils.getInstance().getBus().register(this);

        counterText = (TextView) findViewById(R.id.counter_text);
        editText = (EditText) findViewById(R.id.edit_text);
        imageContainer = (FrameLayout) findViewById(R.id.image_container);
        imageView = ((ImageView) findViewById(R.id.image));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        sendButton = (Button) findViewById(R.id.send_button);
        cameraButton = ((ImageView) findViewById(R.id.camera_button));

        sendButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        editText.addTextChangedListener(this);

        //TODO - remove the image urls
        editText.setText(text);
    }

    @Subscribe
    public void answerAvailable(ImageLoadEvent event) {
        String path = event.getPath();
        editText.append('\n'+path+'\n');
        updateCounterText();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            addFileToGallery();

            CloudinaryUtils.getInstance().uploadImage(Uri.fromFile(photoFile));

            imageContainer.setVisibility(View.VISIBLE);
            Picasso.with(this).load(photoFile.getAbsoluteFile())
                    .fit()
                    .centerCrop()
                    .into(imageView);

            progressBar.setVisibility(View.VISIBLE);
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
            counterText.setText(String.valueOf(Const.MAX_CHARS - editText.length()));
        } else {
            counterText.setText(String.valueOf(Const.MAX_CHARS));
        }

    }
}
