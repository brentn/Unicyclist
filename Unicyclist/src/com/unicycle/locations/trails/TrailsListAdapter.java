package com.unicycle.locations.trails;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import com.unicycle.R;
import com.unicycle.R.drawable;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.R.string;
import com.unicycle.images.Image;
import com.unicycle.tags.Tags;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TrailsListAdapter extends android.widget.ArrayAdapter<Trail> {
	
	private boolean _miles;
	private List<Trail> _trails;

	public TrailsListAdapter(Context context, int textViewResourceId, List<Trail> trails) {
		super(context, textViewResourceId, trails);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		_miles = settings.getBoolean("miles", false);
        this._trails =  trails;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecimalFormat d = new DecimalFormat("###.0");
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.trails_list_item, null);
        }
        
        Trail trail = _trails.get(position);
        
        if (trail != null) {
        	TextView name = (TextView) v.findViewById(R.id.name);
        	TextView description = (TextView) v.findViewById(R.id.description);
        	TextView features = (TextView) v.findViewById(R.id.features);
        	TextView directions = (TextView) v.findViewById(R.id.directions);
        	ImageView difficulty = (ImageView) v.findViewById(R.id.difficulty);
        	TextView tags = (TextView) v.findViewById(R.id.tags);
        	ImageView image = (ImageView) v.findViewById(R.id.featureImage);
        	
        	if (image != null) {
        		if (! trail.getImages().isEmpty()) {
               		Uri uri = trail.getImages().get(0).getUri();
            		image.setImageBitmap(Image.decodeFile(new File(uri.getPath())));
         		} else {
         			image.setImageResource(R.drawable.ic_photo);
         		}
        	}
        	if (name != null) {
        		name.setText(trail.getName());
        	}
        	if (features != null) {
        		int f = trail.getFeatures().size();
        		if (f == 0) {
        			features.setText("");
        		} else if (f ==1) {
        			features.setText("1 "+getContext().getString(R.string.features));
        		} else {
        			features.setText(Integer.toString(f)+" "+getContext().getString(R.string.features));
        		}
        	}
        	if (difficulty != null) {
        		switch (trail.getDifficulty()) {
        		case 1:
            		difficulty.setImageResource(R.drawable.easiest);
            		break;
        		case 2:
        			difficulty.setImageResource(R.drawable.more_difficult);
        			break;
        		case 3:
        			difficulty.setImageResource(R.drawable.most_difficult);
        			break;
        		case 4:
        			difficulty.setImageResource(R.drawable.double_diamond);
        			break;
        		}
        	}
        	if (description != null) {
        		description.setText(trail.getDescription());
        	}
        	if (directions != null) {
        		directions.setText(trail.getDirections());
        	}
        	if (tags != null) {
        		Tags t = new Tags(getContext());
        		tags.setText(t.getTagStringFor(trail));
        	}
        }
        
        return v;
	}

}
