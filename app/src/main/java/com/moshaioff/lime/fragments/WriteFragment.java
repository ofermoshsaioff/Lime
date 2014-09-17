package com.moshaioff.lime.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.moshaioff.lime.FileUtils;
import com.moshaioff.lime.MainActivity;
import com.squareup.picasso.Picasso;
import com.yoavram.lime.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by ofer on 9/17/14.
 */
public class WriteFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // views
    private EditText editText;
    private ImageView imageView;
    private Button sendButton;
    private ImageButton cameraButton;
    private File photoFile;

    public static WriteFragment newInstance() {
        return new WriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.write_fragment, null);

        editText = (EditText) root.findViewById(R.id.edit_text);
        imageView = ((ImageView) root.findViewById(R.id.image));
        sendButton = (Button) root.findViewById(R.id.send_button);
        cameraButton = ((ImageButton) root.findViewById(R.id.camera_button));

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_button:
                ((MainActivity) getActivity()).writeNFC(editText.getText().toString());
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

            imageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(photoFile.getAbsoluteFile())
                    .fit()
                    .centerCrop()
                    .into(imageView);
            editText.append(" " + photoFile.getAbsolutePath());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addFileToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void setText(String result) {
        editText.setText(result);
    }
}
