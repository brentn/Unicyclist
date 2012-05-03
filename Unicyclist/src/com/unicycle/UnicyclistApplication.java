//TODO: Start with location.  fix crash on GPS start
//TODO: Handle map ID properly
//TODO: confirm backup/restore DB
//TODO: make GPS work less


package com.unicycle;

import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.unicycle.MyLocation.LocationResult;

public class UnicyclistApplication extends Application {

	private static android.location.Location _myLocation = new android.location.Location("");
	private static com.unicycle.Location _currentLocation;
	private static Trail _currentTrail;
	private static Feature _currentFeature;
	private static List<Tag> _currentTagSet;
	
	@Override
	public void onCreate() {
		super.onCreate();
		  //get location from last run
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		_myLocation.setLatitude(settings.getInt("latitude", 90)/1e6);
		_myLocation.setLongitude(settings.getInt("longitude",0)/1e6);
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}
	
	public android.location.Location getMyLocation() {
		return UnicyclistApplication._myLocation;
	}
	
	public void setMyLocation(android.location.Location myLocation) {
		UnicyclistApplication._myLocation = myLocation;
	}
	
	public com.unicycle.Location getCurrentLocation() {
		return UnicyclistApplication._currentLocation;
	}
	
	public void setCurrentLocation(com.unicycle.Location location) {
		UnicyclistApplication._currentLocation = location;
	}
	
	public Trail getCurrentTrail() {
		return UnicyclistApplication._currentTrail;
	}
	
	public void setCurrentTrail(Trail trail) {
		UnicyclistApplication._currentTrail = trail;
	}
	
	public Feature getCurrentFeature() {
		return UnicyclistApplication._currentFeature;
	}
	
	public void setCurrentFeature(Feature feature) {
		UnicyclistApplication._currentFeature = feature;
	}
	
	public LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(final android.location.Location location){
	    	_myLocation = location;
	    }
	};

}
