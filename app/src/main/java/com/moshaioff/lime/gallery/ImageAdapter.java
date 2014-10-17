package com.moshaioff.lime.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ofer on 10/9/14.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<GalleryItem> images = new ArrayList<GalleryItem>();

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public GalleryItem getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        } else {
            imageView = (ImageView) convertView;
        }

        File f = new File(images.get(i).getUri());

        if (f.exists()) {
            Picasso.with(context).load(f)
                    .fit()
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(imageView);
        } else {
            Picasso.with(context).load(images.get(i).getUri())
                    .fit()
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(imageView);
        }

        return imageView;
    }

    public void addImage(GalleryItem item) {
        images.add(item);
    }

    public void clear() {
        images.clear();
    }
}
