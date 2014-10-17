package com.moshaioff.lime.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.moshaioff.lime.Const;
import com.moshaioff.lime.R;
import com.squareup.picasso.Picasso;

/**
 * Created by ofer on 10/17/14.
 */
public class ImageFullScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen_view);

        ImageView image = (ImageView) findViewById(R.id.image);

        Bundle b = getIntent().getExtras();
        Uri uri = (Uri) b.get(Const.EXTRA_IMAGE_URI);

        Picasso.with(this).load(uri)
                .centerCrop()
                .fit()
                .into(image);
    }
}
