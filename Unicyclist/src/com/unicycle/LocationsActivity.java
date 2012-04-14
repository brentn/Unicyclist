package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationsActivity extends MapActivity {
	
	final int GET_NEW_LOCATION = 1;

    ListView.OnItemClickListener clickListener;
    
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;

	private List<Location> locationList;
	private List<Location> favouritesList;
	private LocationsListAdapter locationsListAdapter;
	private LocationsListAdapter favouritesListAdapter;
	private LocationsOverlay locationsOverlay;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private ToggleButton satButton;
	private ToggleButton gpsButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_list);
        
        //Read data from DB
        Locations db = new Locations(this);
//this.addSampleData();
        locationList = db.getAllLocations();
        db.close();
        favouritesList = new ArrayList<Location>();
        for(Iterator<Location> i = locationList.iterator(); i.hasNext(); ) {
      	  Location location = i.next();
      	  if (location.isFavourite()) {
      		  favouritesList.add(location);
      	  }
      	}
        
        //Find View Components
        Gallery menu = (Gallery) findViewById(R.id.gallery);
        page = (ViewFlipper)findViewById(R.id.flipper);
        ListView locationsView = (ListView) findViewById(R.id.allLocationsList);
        ListView favouritesView = (ListView) findViewById(R.id.myLocationsList);
        mapView = (MapView) findViewById(R.id.mapview);
        gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        
        //Find other resources
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        //Set up Adapters
        locationsListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, locationList);
        locationsView.setAdapter(locationsListAdapter);
        favouritesListAdapter = new LocationsListAdapter(this.getBaseContext(), R.layout.locations_list_item, favouritesList);
        favouritesView.setAdapter(favouritesListAdapter);
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.locations),getString(R.string.favourites),getString(R.string.mapview)}));
        
        //Set Up Menus
        registerForContextMenu(locationsView);
        registerForContextMenu(favouritesView);
        
        // Set Up Listeners
        satButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		mapView.setSatellite(satButton.isChecked());
        	}
        });
        gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gpsButton.isChecked()) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				} else {
					locationManager.removeUpdates(locationListener);
				}
			}
        	
        });
        
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	        	page.setInAnimation(fadeIn);
	        	page.setOutAnimation(fadeOut);
	        	page.setDisplayedChild(position);
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	            // Do nothing
	        }
        });
        
        clickListener = new ListView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Location l = (Location) ((ListView) parent).getItemAtPosition(position);
        		((UnicyclistApplication) getApplication()).setCurrentLocation(l);
        		Intent locationIntent = new Intent(LocationsActivity.this, LocationActivity.class);
        		LocationsActivity.this.startActivity(locationIntent);
        	}
        };
        ((ListView) findViewById(R.id.allLocationsList)).setOnItemClickListener(clickListener);
        ((ListView) findViewById(R.id.myLocationsList)).setOnItemClickListener(clickListener);

        
        //Set up Map
        mapController = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        mapController.setZoom(13);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		gpsButton.setChecked(mapView.isSatellite());
		
        //add stuff to the map
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_blue_pin);
        locationsOverlay = new LocationsOverlay(drawable, this);
        for(Iterator<Location> i = locationList.iterator(); i.hasNext(); ) {
        	  Location location = i.next();
        	  GeoPoint point = new GeoPoint((int) (location.getLatitude()*1e6),(int) (location.getLongitude()*1e6));
        	  OverlayItem overlayitem = new OverlayItem(point, location.getName(), location.getDescription());
              locationsOverlay.addOverlay(overlayitem);
        	}
        mapOverlays.add(locationsOverlay);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Location location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
    	if (location != null) {
    		int position = locationList.indexOf(location);
	    	locationList.remove(location);
	    	locationList.add(position, location);
	    	locationsListAdapter.notifyDataSetChanged();
	    	if (location.isFavourite()) {
	    		position = favouritesList.indexOf(location);
	    		favouritesList.remove(location);
	    		favouritesList.add(position, location);
	        	favouritesListAdapter.notifyDataSetChanged();
	    	}
	    	mapView.invalidate();
    	}
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locations, menu);
        return true;
    }
    
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		 super.onCreateContextMenu(menu, v, menuInfo);  
		     AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		     Location selected = ((Location)((ListView) v).getAdapter().getItem(info.position));
		     menu.setHeaderTitle(selected.getName());
		     if (selected.isFavourite()) {
		    	 menu.add(0, v.getId(), 0, getString(R.string.remove_from_favourites));
		     } else {
		    	 menu.add(0, v.getId(), 0, getString(R.string.add_to_favourites));
		     }
		     menu.add(0, v.getId(), 0, getString(R.string.delete_location));
    }  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newLocation:	
            	startActivityForResult(new Intent(LocationsActivity.this, NewLocationActivity.class), GET_NEW_LOCATION);
            	break;   
        }
        return true;
    }
    
    @Override
    protected void onActivityResult(
        int aRequestCode, int aResultCode, Intent aData) {
        switch (aRequestCode) {
            case GET_NEW_LOCATION:
            	if ((aData != null) && (aResultCode == Activity.RESULT_OK)) {
            		//Retrieve Data
            		String name = aData.getStringExtra("name");
            		double latitude = aData.getDoubleExtra("latitude", 0);
            		double longitude = aData.getDoubleExtra("longitude", 0);
            		String description = aData.getStringExtra("description");
            		String directions = aData.getStringExtra("directions");
            		int rating = aData.getIntExtra("rating",5);
            		Location location = new Location(name,latitude,longitude,description,directions,rating);
            		//Add to database
            		Locations db = new Locations(this);
            		location.setId(db.addLocation(location));
            		db.close();
            		//Add to location list in memory
            		locationList.add(location);
            		locationsListAdapter.notifyDataSetChanged();
            		//Add to map
            		GeoPoint point = new GeoPoint((int) (location.getLatitude()*1e6),(int) (location.getLongitude()*1e6));
              	  	OverlayItem overlayitem = new OverlayItem(point, location.getName(), location.getDescription());
                    locationsOverlay.addOverlay(overlayitem);
                    //Add to favourites ONLY if favourites list is currently displayed
            		if (page.getDisplayedChild() == 1) {
                		location.setFavourite();
	            		favouritesList.add(location);
	            		favouritesListAdapter.notifyDataSetChanged();
            		}
            	}
                break;
        }
        super.onActivityResult(aRequestCode, aResultCode, aData);
    }
    
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
    	Location location;
    	
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	int locationId = (int) info.id;
    	String selection = item.getTitle().toString();
    	if (selection == getString(R.string.add_to_favourites)) {
    		//modify database
    		Locations db = new Locations(this);
    		location = db.getLocation(locationId);
    		location.setFavourite();
    		db.updateLocation(location);
    		db.close();
    		//modify location list
    		location = locationsListAdapter.findById(locationId);
    		if (location != null) {
    			location.setFavourite();
    			locationsListAdapter.notifyDataSetChanged();
    		}
    		//modify favourites list
    		favouritesList.add(location);
    		favouritesListAdapter.notifyDataSetChanged();
    		return true;
    	}
    	else if (selection == getString(R.string.remove_from_favourites)) {
    		//modify database
    		Locations db = new Locations(this);
    		location = db.getLocation(locationId);
    		location.clearFavourite();
    		db.updateLocation(location);
    		db.close();
    		//modify location list
    		location = locationsListAdapter.findById(locationId);
    		if (location != null) {
    			location.clearFavourite();
    			locationsListAdapter.notifyDataSetChanged();
    		}
    		//modify favourites list
    		location = favouritesListAdapter.findById(locationId);
    		if (location != null) {
    			favouritesList.remove(location);
    			favouritesListAdapter.notifyDataSetChanged();
    		}
    		return true;
    	}
    	else if (selection == getString(R.string.delete_location)) {
    		//modify database
    		Locations db = new Locations(this);
    		db.deleteLocation(locationId);
    		db.close();
    		//modify location list
    		location = locationsListAdapter.findById(locationId);
    		if (location != null) {
    			locationList.remove(location);
    			locationsListAdapter.notifyDataSetChanged();
    		}
    		//modify favourites list
    		location = favouritesListAdapter.findById(locationId);
    		if (location != null) {
    			favouritesList.remove(location);
    			favouritesListAdapter.notifyDataSetChanged();
    		}
//TODO    		//remove from map
    		
    		return true;
    	}
    	return false;  
    }  

    private class MyLocationListener implements LocationListener{

  	  public void onLocationChanged(android.location.Location argLocation) {
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
    
    public class LocationsOverlay extends ItemizedOverlay {

    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    	Context mContext;
    	
    	public LocationsOverlay(Drawable defaultMarker, Context context) {
    	  super(boundCenterBottom(defaultMarker));
    	  mContext = context;
    	}
    	
    	public void addOverlay(OverlayItem overlay) {
    	    mOverlays.add(overlay);
    	    populate();
    	}
    	
    	@Override
    	protected OverlayItem createItem(int i) {
    	  return mOverlays.get(i);
    	}
    	
    	@Override
    	public int size() {
    	  return mOverlays.size();
    	}
    	
    	@Override
    	protected boolean onTap(int index) {
    	  OverlayItem item = mOverlays.get(index);
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    	  dialog.setTitle(item.getTitle());
    	  dialog.setMessage(item.getSnippet());
    	  dialog.show();
    	  return true;
    	}

    }

         
    private void addSampleData() {
    	Locations db = new Locations(this);
    	Location location = new Location();
    	Location location2 = new Location();
    	
    	
    	location.setName("Downes Bowl");
    	location.setCoordinates( 49.068208, -122.327056);
    	location.setDescription("Right in town. A good workout.  Lots of hills.");
    	location.setDirections("Park at the Ag Rec center across the street.  Then follow the paved pathway under the road to get to the entrance.");
    	location.setRating(3);
    	location.setId(db.addLocation(location));
    	location.addTag(this,"MUni");
    	
    	location2.setName("Ledgeview");
    	location2.setCoordinates( 49.070808, -122.222654);
    	location2.setDescription("Right in town. Ride up in 45 mins, down in about 30.  Can be muddy.");
    	location2.setDirections("Drive just past Ledgeview Golf course, and park on the shoulder of the road.  There's a big muddy area on the right, and a yellow gate where the trails begin.");
    	location2.setRating(7);
    	location2.setFavourite();
    	location2.setId(db.addLocation(location2));
    	location2.addTag(this,"Shared Trail");
    	location2.addTag(this,"MUni");
    	location2.addTag(this,"MyNameIsBrent");
    	location2.addTag(this,"There is more to this Mountain Unicycling than meets the eye");
    	location2.addTag(this,"Red and blue");
    	location2.addTag(this,"Go away");
    }
}