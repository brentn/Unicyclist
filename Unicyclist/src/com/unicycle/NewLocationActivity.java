package com.unicycle;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class NewLocationActivity extends MapActivity {

	private MapView locationPicker;
	private GeoPoint p;
	private LocationsOverlay mapOverlay;
	private MyLocationListener locationListener;
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private LocationManager locationManager;
	private Drawable drawable;
	private ToggleButton satButton;
	private ToggleButton gpsButton;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
        
        locationPicker = (MapView) findViewById(R.id.locationPicker);
        satButton = (ToggleButton) findViewById(R.id.satButton);
        gpsButton = (ToggleButton) findViewById(R.id.gpsButton);
        drawable = this.getResources().getDrawable(R.drawable.ic_map_pin);

        satButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		locationPicker.setSatellite(satButton.isChecked());
        	}
        });
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
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
        
        mapOverlays = locationPicker.getOverlays();
        mapOverlay = new LocationsOverlay(drawable, this);
        mapOverlays.clear();
        mapOverlays.add(mapOverlay);        
        
  
        mapController = locationPicker.getController();
        mapController.setZoom(11);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        GeoPoint p = new GeoPoint(
				 (int)(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude()*1000000),
				 (int)(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()*1000000));
		mapController.animateTo(p);
		
        locationPicker.setSatellite(false);

    }
	    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
 	}

    private class MyLocationListener implements LocationListener{

  	  public void onLocationChanged(android.location.Location argLocation) {
	  	   // TODO Auto-generated method stub
	  	   GeoPoint myGeoPoint = new GeoPoint(
	  	    (int)(argLocation.getLatitude()*1000000),
	  	    (int)(argLocation.getLongitude()*1000000));
	  	   
	  	   mapController.animateTo(myGeoPoint);;
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
}


