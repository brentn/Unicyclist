package com.unicycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private TextView tagString;
	private TextView addTagsText;
	private ImageButton addTagsButton;
	private ImageAdapter imageAdapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        //get view objects
        TextView name = (TextView) findViewById(R.id.name);
        Gallery images = (Gallery) findViewById(R.id.images);
        Gallery descriptionMenu = (Gallery) findViewById(R.id.descriptionMenu);
        description = (TextView) findViewById(R.id.description);
        directions = (TextView) findViewById(R.id.directions);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        Button featuresButton = (Button) findViewById(R.id.featuresButton);
        ViewGroup comments = (ViewGroup) findViewById(R.id.commentsGoHere);
        tagString = (TextView) findViewById(R.id.tags);
        ViewGroup tags = (ViewGroup) findViewById(R.id.tagsGoHere);
        addTagsText = (TextView) findViewById(R.id.addTagsText);
        addTagsButton = (ImageButton) findViewById(R.id.addTags);
        ImageButton editTagsButton = (ImageButton) findViewById(R.id.editTags);

        //add dynamic view objects
        comments.addView(new Comments(this).getLocationCommentsView(location));
        tags.addView(new Tags(this).getLocationTagsView(location));

        //set up adapters
        imageAdapter = new ImageAdapter(this,location.getImages());
        images.setAdapter(imageAdapter);
        descriptionMenu.setAdapter(new DescriptionMenuAdapter(this, new String[] {"Description","Directions"}));

        //set up listeners
//        addImage.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//				startActivityForResult(intent, Images.SELECT_PICTURE);
//    		}
//        });
        
    	//Menus
     
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
        	name.setTypeface(roboto);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addImage:	
            	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, Images.SELECT_PICTURE);
            	break; 
            case R.id.addComment:
            	Comments comments = new Comments(LocationActivity.this);
            	comments.newLocationComment(location);
            	break;
        }
        return true;
    }
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) {
		    if (requestCode == Images.SELECT_PICTURE) {
		    	Uri selectedImageUri = data.getData();
		    	if (selectedImageUri != null) {
		            location.addImage(LocationActivity.this, new Image(LocationActivity.this,selectedImageUri));
		            imageAdapter.notifyDataSetChanged();
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
		 String tagText = location.getTagString();
		 if (tagText == "") {
			 addTagsText.setVisibility(View.VISIBLE);
			 addTagsButton.setVisibility(View.GONE);
		 } else {
			 addTagsText.setVisibility(View.GONE);
			 addTagsButton.setVisibility(View.VISIBLE);
		 }
		 tagString.setText(tagText);
	 }

}
