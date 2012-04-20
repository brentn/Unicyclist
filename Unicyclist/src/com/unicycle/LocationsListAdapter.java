package com.unicycle;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationsListAdapter extends android.widget.ArrayAdapter<Location> {
	
	private List<Location> locations;
	private android.location.Location _myLocation = new android.location.Location("");
	private android.location.Location _destLocation = new android.location.Location("");
	private boolean miles;
	
	public LocationsListAdapter(android.location.Location myLocation, Context context, int textViewResourceId, List<Location> locations) {
        super(context, textViewResourceId, locations);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		miles = settings.getBoolean("miles", false);
        this.locations = locations;
        _myLocation = myLocation;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecimalFormat d = new DecimalFormat("###.0");
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.locations_list_item, null);
        }
      
        Location location = locations.get(position);
        if (location != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView description = (TextView) v.findViewById(R.id.description);
            TextView distance = (TextView) v.findViewById(R.id.distance);
            TextView tags = (TextView) v.findViewById(R.id.tags);

            if (name != null) {
        		name.setText(location.getName());
        		if (location.isFavourite()) {
        			name.setTextColor(Color.YELLOW);
        		} else {
        			name.setTextColor(Color.WHITE);
        		}
            }

            if(description != null) {
            	description.setText(location.getDescription() );
            }
            if (distance != null) {
            	_destLocation.setLatitude(location.getLatitude());
            	_destLocation.setLongitude(location.getLongitude());
            	if (_myLocation != null) {
	            	if (miles) {
	            		distance.setText(d.format(_myLocation.distanceTo(_destLocation)/1609.344)+"\nmi");
	            	} else {
	            		distance.setText(d.format(_myLocation.distanceTo(_destLocation)/1000)+"\nkm");
	            	}
            	}
            }
            if (tags != null) {
        		tags.setText(location.getTagString());
            }
        }
        return v;
    }
	
	@Override
	public long getItemId(int position) {
		return (long) locations.get(position).getId();
	}
	
	public Location findById(int id) {
		Location result = null;
		for(Iterator<Location> i = locations.iterator(); i.hasNext(); ) {
			Location location = i.next();
			if ( location.getId() == id ) {
				result = location;
				break;
			}
		}
		return result;
	}
	



}

