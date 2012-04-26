package com.unicycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LocationActivity extends Activity {
	
		
	private Location location;
	private ViewFlipper page; 
	private Animation fadeIn;
	private Animation fadeOut;
	private ViewGroup images;
	private TextView description;
	private TextView directions;
	private ViewGroup tags;
	private ViewGroup comments;
	private ProgressDialog pd = null;

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
        images = (ViewGroup) findViewById(R.id.imagesGoHere);
        Gallery descriptionMenu = (Gallery) findViewById(R.id.descriptionMenu);
        description = (TextView) findViewById(R.id.description);
        directions = (TextView) findViewById(R.id.directions);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        Button featuresButton = (Button) findViewById(R.id.featuresButton);
        comments = (ViewGroup) findViewById(R.id.commentsGoHere);
        tags = (ViewGroup) findViewById(R.id.tagsGoHere);

        //add dynamic view objects
        images.addView(new Images(this).getLocationImagesView(LocationActivity.this,location));
        comments.addView(new Comments(this).getCommentsView(location));
        tags.addView(new Tags(this).getTagsView(LocationActivity.this,location));

        //set up adapters
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
            	comments.newComment(location);
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
		            images.removeAllViews();
		            images.addView(new Images(this).getLocationImagesView(LocationActivity.this,location));
		    	}
		    }
		    if (requestCode == UnicyclistActivity.SELECT_TAGS) {
	        	Locations db = new Locations(LocationActivity.this);
	        	db.updateLocation(location);
	        	db.close();
	        	tags.removeAllViews();
	        	tags.addView(new Tags(this).getTagsView(LocationActivity.this,location));
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
	 
    public void showTagProgress() {
        pd = ProgressDialog.show(LocationActivity.this, "Searching for tag", "Please wait...", true, false);
    }

    @Override
    protected void onResume() {
    	super.onResume();
		if (pd!=null) {
			pd.dismiss();
		}
    	Location location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
    	if (location != null) {
    		comments.removeAllViews();
    		comments.addView(new Comments(LocationActivity.this).getCommentsView(location));
    		tags.removeAllViews();
    		tags.addView(new Tags(this).getTagsView(LocationActivity.this,location)); 
    	}
    }
	 
	   
}
