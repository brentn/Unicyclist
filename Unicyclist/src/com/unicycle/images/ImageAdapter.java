package com.unicycle.images;

import java.io.File;
import java.util.List;

import com.unicycle.R;
import com.unicycle.R.drawable;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private List<Image> images;
    private Context mContext;

    public ImageAdapter(Context c, List<Image> imageList) {
        mContext = c;
        images = imageList;
    }

    public int getCount() {
        return (images.size()+1);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
    	if (position == (getCount()-1)) {
    		imageView.setImageResource(R.drawable.ic_menu_add);
    		imageView.setLongClickable(false);
    	} else {
	        Uri uri = images.get(position).getUri();
	        imageView.setImageBitmap(Image.decodeFile(new File(uri.getPath())));
	        if (images.get(position).isCover()) {
	        	imageView.setPadding(3,3,3,3);
	        	imageView.setBackgroundColor(Color.YELLOW);
	        }
	        if ( imageView.getDrawable().getIntrinsicWidth() > imageView.getDrawable().getIntrinsicHeight()) {
	        	imageView.setLayoutParams(new Gallery.LayoutParams(170, 120));
	        } else {
	        	imageView.setLayoutParams(new Gallery.LayoutParams(90, 120));
	        }
	        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	}
        return imageView;
    }
    

}