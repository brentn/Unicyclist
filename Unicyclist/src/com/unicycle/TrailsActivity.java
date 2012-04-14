package com.unicycle;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class TrailsActivity extends MapActivity {
	
	final int GET_NEW_TRAIL = 1;
	
	private TrailsListAdapter trailsListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trails);
        
        //Set up resources
        Location location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        
        //Read Trails from DB
        Trails db = new Trails(this);
        List<Trail> trailsList = db.getAllTrailsForLocation(location);
        
        //Find View Components
        TextView locationName = (TextView) findViewById(R.id.locationName);
        MapView mapView = (MapView) findViewById(R.id.mapView);
        ListView trailsView = (ListView) findViewById(R.id.trails_list);
        ToggleButton satButton = (ToggleButton) findViewById(R.id.satButton);
        
        //Set up adapters
        trailsListAdapter = new TrailsListAdapter(this.getBaseContext(), R.layout.trails_list_item, trailsList);
        trailsView.setAdapter(trailsListAdapter);
        
        locationName.setTypeface(roboto);
        MapController mapController = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        mapController.setCenter(point);
        mapView.setSatellite(true);
        satButton.setChecked(true);
        mapController.setZoom(16);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trails, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newTrail:	
            	startActivityForResult(new Intent(TrailsActivity.this, NewTrailActivity.class), GET_NEW_TRAIL);
            	break;   
        }
        return true;
    }
    


}
