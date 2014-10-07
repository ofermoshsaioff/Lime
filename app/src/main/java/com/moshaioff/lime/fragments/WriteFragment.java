package com.moshaioff.lime.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moshaioff.lime.CloudinaryUtils;
import com.moshaioff.lime.Const;
import com.moshaioff.lime.FileUtils;
import com.moshaioff.lime.MainActivity;
import com.moshaioff.lime.otto.ImageLoadEvent;
import com.moshaioff.lime.otto.OttoUtils;
import com.moshaioff.lime.otto.WriteNFCEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.yoavram.lime.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by ofer on 9/17/14.
 */
public class WriteFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // views
    private EditText editText;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button sendButton;
    private ImageButton cameraButton;
    private TextView counterText;

    // members
    private StringBuilder suffix = new StringBuilder();
    private File photoFile;
    private int charCounter = Const.MAX_CHARS;

    public static WriteFragment newInstance() {
        return new WriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoUtils.getInstance().getBus().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.write_fragment, null);

        editText = (EditText) root.findViewById(R.id.edit_text);
        imageView = ((ImageView) root.findViewById(R.id.image));
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        sendButton = (Button) root.findViewById(R.id.send_button);
        cameraButton = ((ImageButton) root.findViewById(R.id.camera_button));

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        editText.addTextChangedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        counterText = new TextView(getActivity());
        counterText.setTextColor(getResources().getColor(R.color.pastel_orange));
        counterText.setTextSize(14);
        updateCounterText();
        menu.add(0, 1, 1, R.string.counter).setActionView(counterText).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Subscribe
    public void answerAvailable(ImageLoadEvent event) {
        suffix.append(event.getPath() + "\n");
        charCounter -= suffix.length();
        updateCounterText();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button:
                OttoUtils.getInstance().getBus().post(new WriteNFCEvent(editText.getText().toString()));
                break;
            case R.id.camera_button:
                takePhoto();
                break;
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                photoFile = FileUtils.createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            addFileToGallery();

            CloudinaryUtils.getInstance().uploadImage(Uri.fromFile(photoFile));

            imageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(photoFile.getAbsoluteFile())
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
        getActivity().sendBroadcast(mediaScanIntent);
    }

    //TODO - add the suffix text as well
    public void setText(String result) {
        editText.setText(result);
        charCounter -= result.length();
        updateCounterText();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        charCounter = Const.MAX_CHARS - editText.length();
        updateCounterText();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void updateCounterText() {
        counterText.setText(String.valueOf(charCounter));
    }
}
