package com.moshaioff.lime;

import android.net.Uri;
import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.moshaioff.lime.otto.ImageLoadEvent;
import com.moshaioff.lime.otto.OttoUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
                InputStream inputStream = LimeApplication.contentResolver().openInputStream(params[0]);
                JSONObject jsonObject = cloudinary.uploader().upload(inputStream, Cloudinary.emptyMap());
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



}
