package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationsActivity extends MapActivity {
	
	private ViewFlipper page;
	private Animation fadeIn;
	private Animation fadeOut;
	
	private LocationsListAdapter locationsListAdapter;
	private LocationsListAdapter favouritesListAdapter;
	private MapController mapController;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_list);
        
        // Read Locations from Databse
        DatabaseHandler db = new DatabaseHandler(this);
        db.initialize();
//this.addSampleData();
        List<Location> locationList = db.getAllLocations();
        db.close();
        List<Location> myList = new ArrayList<Location>();
        for(Iterator<Location> i = locationList.iterator(); i.hasNext(); ) {
      	  Location location = i.next();
      	  if (location.isFavourite()) {
      		  myList.add(location);
      	  }
      	}
        
        //Find View Components
        Gallery menu = (Gallery) findViewById(R.id.gallery);
        page = (ViewFlipper)findViewById(R.id.flipper);
        ListView locationsView = (ListView) findViewById(R.id.allLocationsList);
        ListView favouritesView = (ListView) findViewById(R.id.myLocationsList);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        ToggleButton gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        ToggleButton satButton = (ToggleButton) findViewById(R.id.satButton);


        //Find other resources
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        //Set up adapters
        locationsListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, locationList);
        locationsView.setAdapter(locationsListAdapter);
        favouritesListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, myList);
        favouritesView.setAdapter(favouritesListAdapter);
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.locations),getString(R.string.favourites),getString(R.string.mapview)}));
        
        //Set up menus
        registerForContextMenu(locationsView);
        registerForContextMenu(favouritesView);
        
        
        //Set up listeners
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
	        	page.setInAnimation(fadeIn);
	        	page.setOutAnimation(fadeOut);
	        	page.setDisplayedChild(position);
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	            // Do nothing
	        }
        });
        ListView.OnItemClickListener clickListener = new ListView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Intent locationIntent = new Intent(LocationsActivity.this, LocationActivity.class);
        		locationIntent.putExtra("id",id);
        		LocationsActivity.this.startActivity(locationIntent);
        	}

        };
        locationsView.setOnItemClickListener(clickListener);
        favouritesView.setOnItemClickListener(clickListener);
        mapController = mapView.getController();
        mapController.setZoom(11);
        satButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		//mapView.setSatellite(satButton.isChecked());
        	}
        });
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				if (gpsButton.isChecked()) {
//					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//				} else {
//					locationManager.removeUpdates(locationListener);
//				}
			}
        	
        });

        
        //Initialize the map
        //mapView.setBuiltInZoomControls(true);
        gpsButton.setChecked(true);
        mapView.setSatellite(false);
		GeoPoint myGeoPoint = new GeoPoint(
				 (int)(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()*1000000),
				 (int)(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()*1000000));
		mapController.animateTo(myGeoPoint);
        
        //add stuff to the map
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_map_pin);
        LocationsOverlay locationsOverlay = new LocationsOverlay(drawable, this);
        for(Iterator<Location> i = locationList.iterator(); i.hasNext(); ) {
        	  Location location = i.next();
        	  GeoPoint point = new GeoPoint((int) (location.getLatitude()*1e6),(int) (location.getLongitude()*1e6));
        	  OverlayItem overlayitem = new OverlayItem(point, location.getName(), location.getDescription());
              locationsOverlay.addOverlay(overlayitem);
        	}
        mapOverlays.add(locationsOverlay);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		 super.onCreateContextMenu(menu, v, menuInfo);  
		     menu.setHeaderTitle("Locations");  
		     menu.add(0, v.getId(), 0, getString(R.string.add_to_my_locations));  
		     menu.add(0, v.getId(), 0, getString(R.string.delete_location));  
    }  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newLocation:                    
	        	Intent intent = new Intent(LocationsActivity.this,NewLocationActivity.class);
	        	LocationsActivity.this.startActivity(intent);
	        	locationsListAdapter.notifyDataSetChanged();
	        	favouritesListAdapter.notifyDataSetChanged();
                break;   
            case R.id.settings:		startActivity(new Intent(this, Preferences.class));
            	break;
        }
        return true;
    }
    
    private class MyLocationListener implements LocationListener{

    	  public void onLocationChanged(android.location.Location argLocation) {
    	   // TODO Auto-generated method stub
    	   GeoPoint myGeoPoint = new GeoPoint(
    	    (int)(argLocation.getLatitude()*1000000),
    	    (int)(argLocation.getLongitude()*1000000));
    	   mapController.animateTo(myGeoPoint);
    	  }

    	  public void onProviderDisabled(String provider) {
    	   // TODO Auto-generated method stub
    	  }

    	  public void onProviderEnabled(String provider) {
    	   // TODO Auto-generated method stub
    	  }

    	  public void onStatusChanged(String provider,
    	    int status, Bundle extras) {
    	   // TODO Auto-generated method stub
    	  }
	 }
    
//    @Override  
//    public boolean onContextItemSelected(MenuItem item) {  
//            if(item.getTitle()==getString(R.string.add_to_my_locations)) {
//            	int id = item.getItemId();
//            	 DatabaseHandler db = new DatabaseHandler(this);
//            	 db.addFavourite(id);
//            	 db.close();
//            	 locationList.get(id).setFavourite();
//            	 myLocationList.add(locationList.get(id));
//            	 
//        	}  
////        else if(item.getTitle()==getString(R.string.delete_location)){function2(item.getItemId());}  
////        else {return false;}  
//    return true;  
//    }  
    
         
    private void addSampleData() {
    	DatabaseHandler db = new DatabaseHandler(this);
    	Location location = new Location();
    	Location location2 = new Location();
    	
    	
    	location.setName("Downes Bowl");
    	location.setCoordinates( 49.068208, -122.327056);
    	location.setDescription("Right in town. A good workout.  Lots of hills.");
    	location.setDirections("Park at the Ag Rec center across the street.  Then follow the paved pathway under the road to get to the entrance.");
    	location.setRating(3);
    	location.addTag("MUni");
    	db.addLocation(location);  
    	
    	location2.setName("Ledgeview");
    	location2.setCoordinates( 49.070808, -122.222654);
    	location2.setDescription("Right in town. Ride up in 45 mins, down in about 30.  Can be muddy.");
    	location2.setDirections("Drive just past Ledgeview Golf course, and park on the shoulder of the road.  There's a big muddy area on the right, and a yellow gate where the trails begin.");
    	location2.setRating(7);
    	location2.addTag("Shared Trail");
    	location2.addTag("MUni");
    	location2.setFavourite();
    	db.addLocation(location2);
    }
}