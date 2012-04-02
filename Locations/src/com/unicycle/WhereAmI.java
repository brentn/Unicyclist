package com.unicycle;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

public class WhereAmI {
	Context mContext;
	double _latitude;
	double _longitude;

    public WhereAmI (Context mContext) {
    	this.mContext =  mContext;
		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);  
    	List<String> providers = lm.getProviders(true);
    	
    	/* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
    	android.location.Location l = null;
    	
    	for (int i=providers.size()-1; i>=0; i--) {
    		Log.w("Brent",providers.get(i));
    		l = lm.getLastKnownLocation(providers.get(i));
    		if (l != null) break;
    	}
    	this._latitude = providers.size();
		if (l != null) {
			this._latitude = l.getLatitude(); 
			this._longitude = l.getLongitude();
		}
	}
	
	public double getLatitude() {
		return _latitude;
	}
	
	public double getLongitude() {
		return _longitude;
	}
	
}
