package com.unicycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class TrailsActivity extends MapActivity {
	
	final int GET_NEW_TRAIL = 1;
	
	private ProgressDialog pd=null;
	private TrailsListAdapter trailsListAdapter;
	private Location location;
	private List<Trail> trailsList;
	private MapView mapView;
	private ToggleButton satButton;
	private TrailsOverlay trailsOverlay;
	
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
        TextView locationName = (TextView) findViewById(R.id.locationName);
        mapView = (MapView) findViewById(R.id.mapView);
        ListView trailsView = (ListView) findViewById(R.id.trails_list);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        
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
        mapView.setSatellite(false);
        satButton.setChecked(false);
        mapController.setZoom(16);
        
        //add stuff to the map
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable marker = this.getResources().getDrawable(R.drawable.ic_trail_pin);
        marker.setBounds(0, -marker.getIntrinsicHeight(), marker.getIntrinsicWidth(), 0);
        trailsOverlay = new TrailsOverlay(marker, this);
        for(Iterator<Trail> i = trailsList.iterator(); i.hasNext(); ) {
        	  Trail trail = i.next();
        	  point = new GeoPoint((int) (trail.getLatitude()*1e6),(int) (trail.getLongitude()*1e6));
        	  OverlayItem overlayitem = new OverlayItem(point, trail.getName(), trail.getDescription());
              trailsOverlay.addOverlay(overlayitem,trail.getId());
        	}
        mapOverlays.add(trailsOverlay);

        
        trailsView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Trail trail = (Trail) ((ListView) parent).getItemAtPosition(position);
				launchTrail(trail);
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
    	pd = ProgressDialog.show(TrailsActivity.this, "Opening...", "Please wait...", true, false);
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
            	pd = ProgressDialog.show(TrailsActivity.this, "Opening...", "Please wait...", true, false);
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
            		String imagePath = aData.getStringExtra("uri");
            		Trail trail = new Trail(location.getId(),name,latitude,longitude,description,directions,0,rating,difficulty);
            		//Add to database
            		Trails db = new Trails(this);
            		trail.setId(db.addTrail(trail));
            		db.close();    		
            		//add image, if selected
            		if (imagePath != null && imagePath.length() > 0) {
            			Uri selectedImageUri = Uri.parse(imagePath);
            			ExifInterface exif = null;
    		    		float[] latlong = new float[2];
    		    		try {
    						exif = new ExifInterface(selectedImageUri.getPath());
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    		    		if (exif.getLatLong(latlong)) {
    		    			trail.addImage(TrailsActivity.this, new Image(TrailsActivity.this,selectedImageUri,(double) latlong[0],(double) latlong[1]));
    		    		} else {
    		    			trail.addImage(TrailsActivity.this, new Image(TrailsActivity.this,selectedImageUri,trail.getLatitude(),trail.getLongitude()));
    		    		}
            		}
             		//Add to location list in memory
            		trailsList.add(trail);
            		trailsListAdapter.notifyDataSetChanged();


            	}
                break;
        }
        super.onActivityResult(aRequestCode, aResultCode, aData);
    }
    
    public class TrailsOverlay extends ItemizedOverlay {

    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    	private List<Integer> trailIds = new ArrayList<Integer>();
    	Context mContext;
    	
    	public TrailsOverlay(Drawable defaultMarker, Context context) {
    	  super(defaultMarker);
    	  mContext = context;
    	}
    	
    	public void addOverlay(OverlayItem overlay,int id) {
    	    mOverlays.add(overlay);
    	    trailIds.add(id);
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
    		final int id = index;
    	  OverlayItem item = mOverlays.get(index);
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    	  dialog.setTitle(item.getTitle());
    	  dialog.setMessage(item.getSnippet());
    	  dialog.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Trails trails = new Trails(TrailsActivity.this);
        		Trail trail = trails.getTrail(trailIds.get(id));
        		launchTrail(trail);				
			}

    	  });
    	  dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
			}

    	  });
    	  dialog.show();
    	  return true;
    	}

    }
    
    protected void onResume() {
    	super.onResume();
		if (pd!=null) {
			pd.dismiss();
		}
    }

    private void launchTrail(Trail trail) {
		((UnicyclistApplication) getApplication()).setCurrentTrail(trail);
    	pd = ProgressDialog.show(TrailsActivity.this, "Opening Trail...", "Please wait...", true, false);
		Intent intent = new Intent(TrailsActivity.this, TrailActivity.class);
		TrailsActivity.this.startActivity(intent);
    }

}
