package com.unicycle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class TrailsActivity extends MapActivity {
	
	private List<Trail> trailsList  = new ArrayList<Trail>();
	private TrailsListAdapter trailsListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trails);
        
        //Set up resources
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        
        //Read Trails from DB
        
        //Find View Components
        TextView locationName = (TextView) findViewById(R.id.locationName);
        ListView trailsView = (ListView) findViewById(R.id.trails_list);
        
        //Set up adapters
        trailsListAdapter = new TrailsListAdapter(this.getBaseContext(), R.layout.trails_list_item, trailsList);
        trailsView.setAdapter(trailsListAdapter);
        
        locationName.setTypeface(roboto);
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
