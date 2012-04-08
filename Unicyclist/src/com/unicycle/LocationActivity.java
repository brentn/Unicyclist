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
	
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;
	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;

	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        Intent intent = getIntent();
        int id = (int) intent.getLongExtra("id",0);
        
        DatabaseHandler db = new DatabaseHandler(this);
        Location location = new Location();
        
        location = db.getLocation(id);
        
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        TextView name = (TextView) findViewById(R.id.name);
        final TextView description = (TextView) findViewById(R.id.description);
        final TextView directions = (TextView) findViewById(R.id.directions);
        TextView tags = (TextView) findViewById(R.id.tags);
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
        	tags.setText(location.getTagString());
        }
       
	 }
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if (resultCode == RESULT_OK) {
		        if (requestCode == SELECT_PICTURE) {
		            Uri selectedImageUri = data.getData();
		            selectedImagePath = getPath(selectedImageUri);
		            Toast.makeText(LocationActivity.this,selectedImagePath, Toast.LENGTH_SHORT).show();
		        }
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

}
