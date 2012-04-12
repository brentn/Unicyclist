package com.unicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class UnicyclistActivity extends Activity {
	
	SlidingDrawer locationsButton;
	SlidingDrawer ridesButton;
	SlidingDrawer skillsButton;
//	SoundManager mSoundManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        locationsButton = (SlidingDrawer) findViewById(R.id.locationsSlider);
        ridesButton = (SlidingDrawer) findViewById(R.id.ridesSlider);
        skillsButton = (SlidingDrawer) findViewById(R.id.skillsSlider);
        
//        mSoundManager = new SoundManager();
//        mSoundManager.initSounds(getBaseContext());
//        mSoundManager.addSound(1, R.raw.clock);

        locationsButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
//        		mSoundManager.playSound(1);
				return false;
			}
        });
        locationsButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
				UnicyclistActivity.this.startActivity(intent);
			}
        	
        });
        ridesButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
//        		mSoundManager.playSound(1);
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
//        		mSoundManager.playSound(1);
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
