package com.unicycle.skills;

import com.unicycle.GalleryMenuAdapter;
import com.unicycle.R;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.R.string;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ViewFlipper;

public class SkillsActivity extends Activity {
	
	private ViewFlipper page;
	private Animation fadeIn;
	private Animation fadeOut;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skills);
        
        //Set up page containers
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        
        //Set up main menu
        Gallery menu = (Gallery) findViewById(R.id.menu);
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.skills),getString(R.string.goals),getString(R.string.accomplishments)}));
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	        	
	        	page.setInAnimation(fadeIn);
	        	page.setOutAnimation(fadeOut);
	        	page.setDisplayedChild(position);
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	            // Do nothing
	        }
        });

        
    }

}
