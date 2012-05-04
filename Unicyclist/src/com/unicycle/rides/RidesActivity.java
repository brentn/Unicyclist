package com.unicycle.rides;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.unicycle.R;
import com.unicycle.UnicyclistApplication;
import com.unicycle.locations.LocationsActivity;
import com.unicycle.locations.LocationsListAdapter;

public class RidesActivity extends Activity {
	private ViewFlipper page;
	private Animation fadeIn;
	private Animation fadeOut;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides);
        
        ListView rideList = (ListView) findViewById(R.id.rideList);
        View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.new_ride, null, false);
        rideList.addFooterView(footerView);


        
//        //Set up page containers
//        page = (ViewFlipper) findViewById(R.id.flipper);
//        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
//        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
//        
//        //Set up main menu
//        Gallery menu = (Gallery) findViewById(R.id.menu);
//        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.skills),getString(R.string.goals),getString(R.string.accomplishments)}));
//        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
//	        @Override
//	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	        	
//	        	page.setInAnimation(fadeIn);
//	        	page.setOutAnimation(fadeOut);
//	        	page.setDisplayedChild(position);
//	        }
//	        @Override
//	        public void onNothingSelected(AdapterView<?> arg0) {
//	            // Do nothing
//	        }
//        });   
    }
}
