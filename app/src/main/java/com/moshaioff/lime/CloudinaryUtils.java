package com.moshaioff.lime;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.moshaioff.lime.otto.ImageLoadEvent;
import com.moshaioff.lime.otto.OttoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ofer on 10/2/14.
 */
public class CloudinaryUtils {

    public static CloudinaryUtils instance = new CloudinaryUtils();

    private Cloudinary cloudinary;

    public CloudinaryUtils() {
        Map config = new HashMap();
        //TODO - move to config.xml
        config.put("cloud_name", "lime");
        config.put("api_key", "839677175558187");
        config.put("api_secret", "Xvb9KUSW4CYgcHRi9EaE9kFLDhg");
        cloudinary = new Cloudinary(config);
    }

    public static CloudinaryUtils getInstance() {
        return instance;
    }

    public void uploadImage(Uri uri) {
        new UploadImageTask().execute(uri);

    }

    private class UploadImageTask extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... params) {
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(params[0], 256, 256);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                JSONObject jsonObject = cloudinary.uploader().upload(byteArrayInputStream, Cloudinary.emptyMap());
                return jsonObject.getString(Const.JSON.EXTRA_URL);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            OttoUtils.getInstance().getBus().post(new ImageLoadEvent(s));
        }
    }

    public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        ContentResolver contentResolver = LimeApplication.contentResolver();

        try{
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bm;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }
}
