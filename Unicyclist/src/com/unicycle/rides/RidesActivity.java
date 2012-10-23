package com.unicycle.rides;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.unicycle.R;

public class RidesActivity extends Activity {
	private ViewFlipper page;
	private Animation fadeIn;
	private Animation fadeOut;
	private List<Ride> rideList = new ArrayList<Ride>();
	private RidesListAdapter ridesListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rides);
        
        //Set up title
    	Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");
    	TextView title = (TextView) findViewById(R.id.title);
    	title.setTypeface(tf);

        //Set up list
        ListView rideListView = (ListView) findViewById(R.id.rideList);
        View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.new_ride, null, false);
        rideListView.addFooterView(footerView);
        ridesListAdapter = new RidesListAdapter(RidesActivity.this, R.layout.rides_list_item, rideList);
        rideListView.setAdapter(ridesListAdapter);


        
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
    
    private class RidesListAdapter extends ArrayAdapter<Ride> {
    	
    	private List<Ride> _rides;
    	
    	public RidesListAdapter(Context context, int textViewResourceId,List<Ride> rides) {
    		super(context, textViewResourceId, rides);
    		_rides = rides;
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.rides_list_item, null);
            }
            v.setBackgroundColor(Color.TRANSPARENT);
            return v;
    	}

    }
}
