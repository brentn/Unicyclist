package com.unicycle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class LocationActivity extends Activity {
	
	static final int SELECT_TAGS = 2;
	
	private Location location;
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;
	private TextView tags;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        TextView name = (TextView) findViewById(R.id.name);
        final TextView description = (TextView) findViewById(R.id.description);
        final TextView directions = (TextView) findViewById(R.id.directions);
        tags = (TextView) findViewById(R.id.tags);
        TextView addTags = (TextView) findViewById(R.id.addTags);
        ImageView addImageButton = (ImageView) findViewById(R.id.addImageButton);
        Gallery descriptionMenu = (Gallery) findViewById(R.id.descriptionMenu);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        descriptionMenu.setAdapter(new DescriptionMenuAdapter(this, new String[] {"Description","Directions"}));
        
        descriptionMenu.setOnItemSelectedListener(new OnItemSelectedListener() {
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
      
        addImageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });    
        trailsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		startActivity(new Intent(LocationActivity.this, TrailsActivity.class));
        	}
        });
        addTags.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		Location location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        		((UnicyclistApplication) getApplication()).getCurrentTagsFromCurrentLocation();
        		startActivityForResult(new Intent(LocationActivity.this, TagsActivity.class),SELECT_TAGS);
        	}
        });
        
        if ( name != null) {
        	name.setText(location.getName());
        }
        if ( description != null) {
        	description.setText(location.getDescription());
        }
        if ( directions != null) {
        	directions.setText(location.getDirections());
        }
        if ( tags != null) {
       		showTags();
        }
       
	 }
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if ((requestCode == SELECT_PICTURE) && (resultCode == RESULT_OK)) {
	            Uri selectedImageUri = data.getData();
	            selectedImagePath = getPath(selectedImageUri);
	            Toast.makeText(LocationActivity.this,selectedImagePath, Toast.LENGTH_SHORT).show();
		    }
		    if (requestCode == SELECT_TAGS) {
	        	((UnicyclistApplication) getApplication()).setCurrentLocationTagsFromCurrentTags();
	        	location.setTags(((UnicyclistApplication) getApplication()).getCurrentTagSet());
	        	Locations db = new Locations(getBaseContext());
	        	showTags();
	        	db.updateLocation(location);
		    }
	 }
	 
	 public String getPath(Uri uri) {
		    String[] projection = { MediaStore.Images.Media.DATA };
		    Cursor cursor = managedQuery(uri, projection, null, null, null);
		    int column_index = cursor
		            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
 	 }
	 
	 private void showTags() {
		 tags.setText(location.getTagString());
	 }


}
