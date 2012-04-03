package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationsActivity extends MapActivity {
	
	private ListView locationsView;
	private ListView favouritesView;
	private LocationsListAdapter locationsListAdapter;
	private LocationsListAdapter favouritesListAdapter;
    private ListView.OnItemClickListener clickListener;
    
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;
	
	private List<Location> locationList;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        setContentView(R.layout.locations_list);
        
        DatabaseHandler db = new DatabaseHandler(this);
        db.initialize();
        
//this.addSampleData();
        
        locationList = db.getAllLocations();
        db.close();
        List<Location> myList = new ArrayList<Location>();
        for(Iterator<Location> i = locationList.iterator(); i.hasNext(); ) {
      	  Location location = i.next();
      	  if (location.isFavourite()) {
      		  myList.add(location);
      	  }
      	}

        locationsView = (ListView) findViewById(R.id.allLocationsList);
        locationsListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, locationList);
        locationsView.setAdapter(locationsListAdapter);
        registerForContextMenu(locationsView);
        favouritesView = (ListView) findViewById(R.id.myLocationsList);
        favouritesListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, myList);
        favouritesView.setAdapter(favouritesListAdapter);
        registerForContextMenu(favouritesView);
        Gallery menu = (Gallery) findViewById(R.id.gallery);
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.locations),getString(R.string.favourites),getString(R.string.mapview)}));
        
        page = (ViewFlipper)findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        
        // Change View
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

        // Item Click Detection
        clickListener = new ListView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Intent locationIntent = new Intent(LocationsActivity.this, LocationActivity.class);
        		locationIntent.putExtra("id",id);
        		LocationsActivity.this.startActivity(locationIntent);
        	}
        };
        
        ((ListView) findViewById(R.id.allLocationsList)).setOnItemClickListener(clickListener);
        ((ListView) findViewById(R.id.myLocationsList)).setOnItemClickListener(clickListener);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        MapController mapController = mapView.getController();
        mapController.setZoom(11);
        WhereAmI myLocation = new WhereAmI(this.getBaseContext());
        mapController.setCenter(new GeoPoint((int) (myLocation.getLatitude()*1e6),(int) (myLocation.getLongitude()*1e6)));
        
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