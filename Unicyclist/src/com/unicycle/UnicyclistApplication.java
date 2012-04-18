package com.unicycle;

import java.util.List;

import android.app.Application;
import android.location.Location;

import com.unicycle.MyLocation.LocationResult;

public class UnicyclistApplication extends Application {

	private static android.location.Location _myLocation = new android.location.Location("");
	private static com.unicycle.Location _currentLocation;
	private static List<Tag> _currentTagSet;
	
	{_myLocation.setLatitude(90);
	_myLocation.setLongitude(0);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(getApplicationContext(), locationResult);
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
	
	public List<Tag> getCurrentTagSet() {
		return UnicyclistApplication._currentTagSet;
	}
	
	public void setCurrentTagSet(List<Tag> tagSet) {
		UnicyclistApplication._currentTagSet = tagSet;
	}
	
	public void copyTagsFromCurrentLocation() {
		
		UnicyclistApplication._currentTagSet = UnicyclistApplication._currentLocation.getTags();
	}
	
	public LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(final android.location.Location location){
	    	_myLocation = location;
	    }
	};

}
