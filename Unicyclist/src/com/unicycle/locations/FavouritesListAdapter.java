package com.unicycle.locations;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import com.unicycle.MyLocation;
import com.unicycle.R;
import com.unicycle.MyLocation.LocationResult;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.tags.Tags;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FavouritesListAdapter extends android.widget.ArrayAdapter<Location> {
	
	private List<Location> locations;
	private android.location.Location _myLocation = new android.location.Location("");
	private android.location.Location _destLocation = new android.location.Location("");
	private boolean miles;
	private MyLocation myLocation = new MyLocation();
	private FavouritesListAdapter lla = this;
	
	public FavouritesListAdapter(Context context, int textViewResourceId, List<Location> locations) {
        super(context, textViewResourceId, locations);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		myLocation.getLocation(this.getContext(), locationResult);
		miles = settings.getBoolean("miles", false);
        this.locations = locations;
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
            		double meters = _myLocation.distanceTo(_destLocation);
            		if (meters < 200000) {
            			distance.setText(d.format((miles)?meters/(1609.344):(meters/1000)));
            		}
            	}
            }
            if (tags != null) {
            	Tags t = new Tags(getContext());
            	tags.setText(t.getTagStringFor(location));
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
	
	public LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(final android.location.Location location){
	    	_myLocation = location;
	    	lla.notifyDataSetChanged();
	    }
	};

}


