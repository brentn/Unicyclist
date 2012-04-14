package com.unicycle;

import java.util.List;

import android.app.Application;

public class UnicyclistApplication extends Application {
	
	public static Location _currentLocation;
	public static List<Tag> _currentTagSet;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public Location getCurrentLocation() {
		return UnicyclistApplication._currentLocation;
	}
	
	public void setCurrentLocation(Location location) {
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
	
}
