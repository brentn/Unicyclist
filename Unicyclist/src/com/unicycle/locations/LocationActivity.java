package com.unicycle.locations;

import java.io.IOException;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.UnicyclistApplication;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.R.menu;
import com.unicycle.comments.Comments;
import com.unicycle.images.GetPhoto;
import com.unicycle.images.Image;
import com.unicycle.images.ImageAdapter;
import com.unicycle.images.Images;
import com.unicycle.locations.trails.TrailsActivity;
import com.unicycle.tags.Tags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;

public class LocationActivity extends Activity {
	
		
	private Location location;
	private ViewGroup images;
	private ViewGroup description;
	private ViewGroup tags;
	private ViewGroup comments;
	private ProgressDialog pd = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        location = ((UnicyclistApplication) getApplication()).getCurrentLocation();
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        

        //get view objects
        TextView name = (TextView) findViewById(R.id.name);
        images = (ViewGroup) findViewById(R.id.imagesGoHere);
        description = (ViewGroup) findViewById(R.id.descriptionGoesHere);
        Button trailsButton = (Button) findViewById(R.id.trailsButton);
        Button featuresButton = (Button) findViewById(R.id.featuresButton);
        comments = (ViewGroup) findViewById(R.id.commentsGoHere);
        tags = (ViewGroup) findViewById(R.id.tagsGoHere);

        //add dynamic view objects
        images.addView(new Images(this).getImagesView(LocationActivity.this,location));
        comments.addView(new Comments(this).getCommentsView(location));
        tags.addView(new Tags(this).getTagsView(LocationActivity.this,location));

        //set up adapters
        trailsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
            	pd = ProgressDialog.show(LocationActivity.this, "Opening Trail...", "Please wait...", true, false);
        		startActivity(new Intent(LocationActivity.this, TrailsActivity.class));
        	}
        });
        
        if ( name != null) {
        	name.setText(location.getName());
        	name.setTypeface(roboto);
        }
        if (description != null) {
        	description.addView(new Description(this).getView(location));
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
            	Intent intent = new Intent(LocationActivity.this,GetPhoto.class);
				startActivityForResult(intent, UnicyclistActivity.GET_PHOTO);
            	break; 
            case R.id.addComment:
            	Comments comments = new Comments(LocationActivity.this);
            	comments.newComment(location);
            	break;
        }
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    	//don't show menu if long-click on the add image icon
    	if (info.position < (((Gallery)v).getCount()-1)) {
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.images, menu);
    	}
}
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Image image;
        if (info.id >= location.getImages().size()) {
        	Log.e("LocationActivity","selection beyond size of array.");
        } else {
	        switch (item.getItemId()) {
	            case R.id.delete:
	            	image = location.getImages().get((int) info.id);
	            	location.removeImage(LocationActivity.this,image.getId());
	            	((ImageAdapter)((Gallery) images.getChildAt(0)).getAdapter()).notifyDataSetChanged();
	                return true;
	            case R.id.set_cover:
	            	Images imageDb = new Images(LocationActivity.this);
	            	image = location.getImages().get((int) info.id);
	            	for (Image i: location.getImages()) {
	            		i.setCover(false);
	            		imageDb.updateImage(i);
	            	}
	            	image.setCover(true);
	            	((ImageAdapter)((Gallery) images.getChildAt(0)).getAdapter()).notifyDataSetChanged();
	            	imageDb.updateImage(image);
	                return true;
	            default:
	                return super.onContextItemSelected(item);
	        }
        }
        return false;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) {
		    if (requestCode == UnicyclistActivity.GET_PHOTO) {
		    	Uri selectedImageUri = data.getData();
		    	if (selectedImageUri != null) {
		    		ExifInterface exif = null;
		    		float[] latlong = new float[2];
		    		try {
						exif = new ExifInterface(selectedImageUri.getPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
		    		if (exif.getLatLong(latlong)) {
		    			location.addImage(LocationActivity.this, new Image(LocationActivity.this,selectedImageUri,(double) latlong[0],(double) latlong[1]));
		    		} else {
		    			location.addImage(LocationActivity.this, new Image(LocationActivity.this,selectedImageUri,location.getLatitude(),location.getLongitude()));
		    		}
	            	((ImageAdapter)((Gallery) images.getChildAt(0)).getAdapter()).notifyDataSetChanged();
		            images.addView(new Images(this).getImagesView(LocationActivity.this,location));
		    	}
		    }
		    if (requestCode == UnicyclistActivity.SELECT_TAGS) {
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
