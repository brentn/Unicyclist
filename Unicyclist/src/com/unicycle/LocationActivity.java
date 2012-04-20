package com.unicycle;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LocationActivity extends Activity {
	
		
	private Location location;
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;
	private TextView description;
	private TextView directions;
	private String selectedImagePath;
	private TextView tags;
	private TextView addTagsText;
	private ImageButton addTagsButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        //get view objects
        TextView name = (TextView) findViewById(R.id.name);
        ViewGroup images = (ViewGroup) findViewById(R.id.imagesGoHere);
        ImageButton addImage = (ImageButton) findViewById(R.id.addImageButton);
        Gallery descriptionMenu = (Gallery) findViewById(R.id.descriptionMenu);
        description = (TextView) findViewById(R.id.description);
        directions = (TextView) findViewById(R.id.directions);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        Button featuresButton = (Button) findViewById(R.id.featuresButton);
        ViewGroup comments = (ViewGroup) findViewById(R.id.commentsGoHere);
        tags = (TextView) findViewById(R.id.tags);
        addTagsText = (TextView) findViewById(R.id.addTagsText);
        addTagsButton = (ImageButton) findViewById(R.id.addTags);
        ImageButton editTagsButton = (ImageButton) findViewById(R.id.editTags);

        //add dynamic view objects
        images.addView(new Images(this).getLocationImagesView(location));
        comments.addView(new Comments(this).getLocationCommentsView(location));

        //set up adapters
        descriptionMenu.setAdapter(new DescriptionMenuAdapter(this, new String[] {"Description","Directions"}));

        //set up listeners
        addImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, Images.SELECT_PICTURE);
    		}
        });
        
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
      
        description.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);

				alert.setTitle("Description");
				// Set an EditText view to get user input 
				final EditText input = new EditText(LocationActivity.this);
				input.setText(description.getText());
				input.setLines(6);
				input.setGravity(Gravity.TOP);
				alert.setView(input);
				alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					description.setText(input.getText().toString());
					location.setDescription(input.getText().toString());
		        	Locations db = new Locations(getBaseContext());
		        	db.updateLocation(location);
		        	db.close();
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});

				alert.show();
				return false;
			}
        });
        directions.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);

				alert.setTitle("Directions");
				// Set an EditText view to get user input 
				final EditText input = new EditText(LocationActivity.this);
				input.setText(directions.getText());
				input.setLines(6);
				input.setGravity(Gravity.TOP);
				alert.setView(input);
				alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					directions.setText(input.getText().toString());
					location.setDirections(input.getText().toString());
		        	Locations db = new Locations(getBaseContext());
		        	db.updateLocation(location);
		        	db.close();
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});

				alert.show();
				return false;
			}
        });
        trailsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		startActivity(new Intent(LocationActivity.this, TrailsActivity.class));
        	}
        });
        OnClickListener editTags = new OnClickListener() {
        	public void onClick(View view) {
        		((UnicyclistApplication) getApplication()).copyTagsFromCurrentLocation();
        		startActivityForResult(new Intent(LocationActivity.this, TagsActivity.class),Location.SELECT_TAGS);
        	}
        };
        addTagsText.setOnClickListener(editTags);
        addTagsButton.setOnClickListener(editTags);
        editTagsButton.setOnClickListener(editTags);
        
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

	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) {
		    if (requestCode == Images.SELECT_PICTURE) {
		    	Uri selectedImageUri = data.getData();
		    	if (selectedImageUri != null) {
		            location.addImage(LocationActivity.this, new Image(selectedImageUri));
		    	}
		    }
		    if (requestCode == Location.SELECT_TAGS) {
	        	showTags();
	        	Locations db = new Locations(LocationActivity.this);
	        	db.updateLocation(location);
	        	db.close();
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
	 
	 private void showTags() {
		 String tagString = location.getTagString();
		 if (tagString == "") {
			 addTagsText.setVisibility(View.VISIBLE);
			 addTagsButton.setVisibility(View.GONE);
		 } else {
			 addTagsText.setVisibility(View.GONE);
			 addTagsButton.setVisibility(View.VISIBLE);
		 }
		 tags.setText(tagString);
	 }

}
