package com.unicycle;

import android.app.Application;

public class UnicyclistApplication extends Application {
	
	public static Location _currentLocation = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public Location getCurrentLocation() {
		return this._currentLocation;
	}
	
	public void setCurrentLocation(Location location) {
		this._currentLocation = location;
	}

}
