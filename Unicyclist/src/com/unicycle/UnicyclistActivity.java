package com.unicycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class UnicyclistActivity extends Activity {
	
	public static final int CREATE_LOCATION = 1;
	public static final int SELECT_TAGS = 2;
	public static final int SELECT_LOCATION = 3;
	public static final int LOCATION_TYPE=100;
	public static final int TRAIL_TYPE=101;
	public static final int FEATURE_TYPE=102;
	public static final int CAMERA_REQUEST = 1888; 
	public static final int SELECT_PICTURE = 1889;
	public static final int GET_PHOTO = 1890;


	private Context mContext = this;
	private ProgressDialog pd = null;
	SlidingDrawer locationsButton;
	SlidingDrawer ridesButton;
	SlidingDrawer skillsButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        locationsButton = (SlidingDrawer) findViewById(R.id.locationsSlider);
        ridesButton = (SlidingDrawer) findViewById(R.id.ridesSlider);
        skillsButton = (SlidingDrawer) findViewById(R.id.skillsSlider);
        ImageView feature = (ImageView) findViewById(R.id.feature);

        locationsButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
        });
        locationsButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
        	
			@Override
			public void onDrawerOpened() {
		        pd = ProgressDialog.show(mContext, "Opening..", "Please wait...", true, false);
				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
				UnicyclistActivity.this.startActivity(intent);
			}
        	
        });
        ridesButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
        });
        ridesButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
//				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
//				UnicyclistActivity.this.startActivity(intent);
			}
        	
        });
        skillsButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
        });
        skillsButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
//				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
//				UnicyclistActivity.this.startActivity(intent);
		}
        	
        });
	}
	
	protected void onResume() {
		super.onResume();
		if (pd!=null) {
			pd.dismiss();
		}
		if (locationsButton.isOpened()) {
			locationsButton.animateClose();
		}
		if (ridesButton.isOpened()) {
			ridesButton.animateClose();
		}
		if (skillsButton.isOpened()) {
			skillsButton.animateClose();
		}        		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:		startActivity(new Intent(this, Preferences.class));
            	break;
        }
        return true;
    }
    


}
