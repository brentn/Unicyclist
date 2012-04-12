package com.unicycle;

import android.os.Bundle;
import android.widget.Gallery;

import com.google.android.maps.MapActivity;

public class TrailsActivity extends MapActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trails_list);
        
        //Read Trails from DB
        
        //Find View Components
        Gallery menu = (Gallery) findViewById(R.id.gallery);
        
        
        //Set up adapters
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.trails),getString(R.string.mapview)}));
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
