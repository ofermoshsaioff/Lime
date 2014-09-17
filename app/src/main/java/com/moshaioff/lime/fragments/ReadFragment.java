package com.moshaioff.lime.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moshaioff.lime.Const;
import com.squareup.picasso.Picasso;
import com.yoavram.lime.R;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

/**
 * Created by ofer on 9/17/14.
 */
public class ReadFragment extends Fragment {

    // views
    TextView textView;
    ImageView imageView;

    public static ReadFragment newInstance() {
        return new ReadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.read_fragment, null);
        textView = (TextView) root.findViewById(R.id.read_message);
        imageView = (ImageView) root.findViewById(R.id.image);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    public void setText(String result) {
        textView.setText(result);
        String imageUri = extractImageUri(result);
        if (StringUtils.isNotBlank(imageUri)) {
            Picasso.with(getActivity()).load(imageUri)
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
}
