package com.unicycle;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class TrailsActivity extends MapActivity {
	
	final int GET_NEW_TRAIL = 1;
	
	private TrailsListAdapter trailsListAdapter;
	private Location location;
	private List<Trail> trailsList;
	private TextView trailName;
	private ViewFlipper flipper;
	private MapView mapView;
	private ToggleButton satButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trails);
        
        //Set up resources
        location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        
        //Read Trails from DB
        Trails db = new Trails(this);
        trailsList = db.getAllTrailsForLocation(location);
        
        //Find View Components
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        TextView locationName = (TextView) findViewById(R.id.locationName);
        mapView = (MapView) findViewById(R.id.mapView);
        ListView trailsView = (ListView) findViewById(R.id.trails_list);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        trailName = (TextView) findViewById(R.id.trailName);
        
        View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.trails_list_add, null, false);
        trailsView.addFooterView(footerView);

        //Set up adapters
        trailsListAdapter = new TrailsListAdapter(this.getBaseContext(), R.layout.trails_list_item, trailsList);
        trailsView.setAdapter(trailsListAdapter);
        
        locationName.setTypeface(roboto);
        locationName.setText(location.getName());
        MapController mapController = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        mapController.setCenter(point);
        mapView.setSatellite(true);
        satButton.setChecked(true);
        mapController.setZoom(16);
        
        trailsView.setOnItemLongClickListener( new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
//				((TextView) findViewById(R.id.trailName)).setText(trailsList.get(position).getName());
//				((TextView) findViewById(R.id.description)).setText(trailsList.get(position).getDescription());				
//				flipper.setDisplayedChild(1);
				startActivity(new Intent(TrailsActivity.this, TrailActivity.class));
				return false;
			}
        	
        });
        
        satButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
        		mapView.setSatellite(satButton.isChecked());

			}
        });
        
    }

    public void onClick(View footerView) {
    	startActivityForResult(new Intent(TrailsActivity.this, NewTrailActivity.class), GET_NEW_TRAIL);
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
    
    @Override
    protected void onActivityResult(
        int aRequestCode, int aResultCode, Intent aData) {
        switch (aRequestCode) {
            case GET_NEW_TRAIL:
            	if ((aData != null) && (aResultCode == Activity.RESULT_OK)) {
            		//Retrieve Data
            		String name = aData.getStringExtra("name");
            		double latitude = aData.getDoubleExtra("latitude", 0);
            		double longitude = aData.getDoubleExtra("longitude", 0);
            		int difficulty = aData.getIntExtra("difficulty",0);
            		String description = aData.getStringExtra("description");
            		String directions = aData.getStringExtra("directions");
            		int rating = aData.getIntExtra("rating",5);
            		Trail trail = new Trail(location.getId(),name,latitude,longitude,description,directions,0,rating,difficulty);
            		//Add to database
            		Trails db = new Trails(this);
            		trail.setId(db.addTrail(trail));
            		db.close();    		
            		//Add to location list in memory
            		trailsList.add(trail);
            		trailsListAdapter.notifyDataSetChanged();


            	}
                break;
        }
        super.onActivityResult(aRequestCode, aResultCode, aData);
    }


}
