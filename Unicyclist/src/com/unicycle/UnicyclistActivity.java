package com.unicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class UnicyclistActivity extends Activity {
	
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

        locationsButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
				UnicyclistActivity.this.startActivity(intent);
			}
        	
        });
        ridesButton.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
//				Intent intent = new Intent(UnicyclistActivity.this, LocationsActivity.class);
//				UnicyclistActivity.this.startActivity(intent);
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

}
