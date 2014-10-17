package com.moshaioff.lime.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moshaioff.lime.R;
import com.squareup.picasso.Picasso;

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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(context).inflate(R.layout.image_thumbnail_view, null);
        }

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
        ImageView deleteButton = (ImageView) convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(i);
                notifyDataSetChanged();
            }
        });

        Picasso.with(context).load(images.get(i).getUri())
                .fit()
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(thumbnail);

        return convertView;
    }

    public void addImage(GalleryItem item) {
        images.add(item);
    }

    public void clear() {
        images.clear();
    }
}
