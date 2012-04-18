package com.unicycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class LocationActivity extends Activity {
	
	private static final int SELECT_PICTURE = 1;
		
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

        TextView name = (TextView) findViewById(R.id.name);
        Gallery imageGallery = (Gallery) findViewById(R.id.images);
        description = (TextView) findViewById(R.id.description);
        directions = (TextView) findViewById(R.id.directions);
        tags = (TextView) findViewById(R.id.tags);
        addTagsText = (TextView) findViewById(R.id.addTagsText);
        addTagsButton = (ImageButton) findViewById(R.id.addTags);
        ImageButton editTagsButton = (ImageButton) findViewById(R.id.editTags);
        ImageView addImageButton = (ImageView) findViewById(R.id.addImageButton);
        Gallery descriptionMenu = (Gallery) findViewById(R.id.descriptionMenu);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        RelativeLayout commentsSection = (RelativeLayout) findViewById(R.id.commentsSection);

        //Images images = new Images(this);
        //imageGallery.setAdapter(new ImageAdapter(this, images.getImagesForLocation(location.getId()) ));       
        ListView commentsView = new Comments(this).getLocationCommentsView(location);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        relativeParams.addRule(RelativeLayout.BELOW,findViewById(R.id.commentsTitle).getId());
        relativeParams.addRule(RelativeLayout.ABOVE,findViewById(R.id.tagarea).getId());
        commentsSection.addView(commentsView,relativeParams);
        
        commentsSection.setOnLongClickListener( new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				Toast.makeText(LocationActivity.this,"CLICK",Toast.LENGTH_SHORT).show();
				return false;
			}
        });

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
        description.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);

				alert.setTitle("Description");
				// Set an EditText view to get user input 
				final EditText input = new EditText(LocationActivity.this);
				input.setText(description.getText());
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
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode == RESULT_OK) {
		    if (requestCode == SELECT_PICTURE) {
	            Uri selectedImageUri = data.getData();
	            selectedImagePath = getPath(selectedImageUri);
	            Image image = new Image(Image.LOCATION_IMAGE,selectedImagePath);
	            location.addImage(LocationActivity.this, image);
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
