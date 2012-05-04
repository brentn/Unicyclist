package com.unicycle.locations.trails;

import java.io.IOException;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.UnicyclistApplication;
import com.unicycle.R.drawable;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.R.menu;
import com.unicycle.comments.Comments;
import com.unicycle.images.Image;
import com.unicycle.images.ImageAdapter;
import com.unicycle.images.Images;
import com.unicycle.locations.Description;
import com.unicycle.locations.LocationPickerActivity;
import com.unicycle.tags.Tags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class TrailActivity extends Activity {

	private Trail trail;
	private ProgressDialog pd = null;
	private ViewGroup images;
	private ViewGroup description;
	private ViewGroup tags;
	private ViewGroup comments;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trail);
        
        trail = ((UnicyclistApplication) getApplication()).getCurrentTrail();
        Typeface roboto = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Thin.ttf");
        
        //find view components
        TextView trailName = (TextView) findViewById(R.id.trailName);
        ImageView difficulty = (ImageView) findViewById(R.id.difficulty);
        images = (ViewGroup) findViewById(R.id.imagesGoHere);
        description = (ViewGroup) findViewById(R.id.descriptionGoesHere);
        tags = (ViewGroup) findViewById(R.id.tagsGoHere);
        Button featuresButton = (Button) findViewById(R.id.features);
        Button trailheadButton = (Button) findViewById(R.id.trailheadButton);
        comments = (ViewGroup) findViewById(R.id.commentsGoHere);
        
        //fill in view
        if (trailName != null) {
        	trailName.setText(trail.getName());
        	trailName.setTypeface(roboto);
        }
        if (difficulty != null) {
	    	switch (trail.getDifficulty()) {
	        case Trail.DIFFICULTY_EASIEST: difficulty.setImageResource(R.drawable.easiest);
	        	break;
	        case Trail.DIFFICULTY_MODERATE: difficulty.setImageResource(R.drawable.more_difficult);
	        	break;
	        case Trail.DIFFICULTY_HARD: difficulty.setImageResource(R.drawable.most_difficult);
	        	break;
	        case Trail.DIFFICULTY_HARDEST: difficulty.setImageResource(R.drawable.double_diamond);
	        	break;
	        }
        }
        if (images != null) {
        	images.addView(new Images(this).getImagesView(TrailActivity.this,trail));
        }
        if (description != null) {
        	description.addView(new Description(this).getView(trail));
        }
        if (tags != null) {
        	tags.addView(new Tags(this).getTagsView(TrailActivity.this,trail));
        }
        if (comments != null) {
        	comments.addView(new Comments(this).getCommentsView(trail));
        }
        
        trailheadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(TrailActivity.this, LocationPickerActivity.class);
				intent.putExtra("latitude", trail.getLatitude());
				intent.putExtra("longitude",trail.getLongitude());
				intent.putExtra("zoom", 16);
				TrailActivity.this.startActivityForResult(intent,UnicyclistActivity.SELECT_LOCATION);
			}
        });
        
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
        switch (item.getItemId()) {
            case R.id.delete:
            	image = trail.getImages().get((int) info.id);
            	trail.removeImage(TrailActivity.this,image.getId());
            	images.removeAllViews();
            	images.addView(new Images(this).getImagesView(TrailActivity.this,trail));
                return true;
            case R.id.set_cover:
            	image = trail.getImages().get((int) info.id);
            	for (Image i: trail.getImages()) {
            		i.setCover(false);
            	}
            	image.setCover(true);
            	((ImageAdapter)((Gallery) images.getChildAt(0)).getAdapter()).notifyDataSetChanged();
            	Images i = new Images(TrailActivity.this);
            	i.updateImage(image);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) {
			 switch (requestCode) {
			 case UnicyclistActivity.GET_PHOTO:
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
		    			trail.addImage(TrailActivity.this, new Image(TrailActivity.this,selectedImageUri,(double) latlong[0],(double) latlong[1]));
		    		} else {
		    			trail.addImage(TrailActivity.this, new Image(TrailActivity.this,selectedImageUri,trail.getLatitude(),trail.getLongitude()));
		    		}
		            images.removeAllViews();
		            images.addView(new Images(this).getImagesView(TrailActivity.this,trail));
		    	}
		    	break;
			 case UnicyclistActivity.SELECT_TAGS:
		    	tags.removeAllViews();
	        	tags.addView(new Tags(this).getTagsView(TrailActivity.this,trail));
	        	break;
			 case UnicyclistActivity.SELECT_LOCATION:
        		trail.setCoordinates(data.getDoubleExtra("latitude",0), data.getDoubleExtra("longitude", 0));
        		break;
		    }
		 }
	 }

	
    public void showTagProgress() {
        pd = ProgressDialog.show(TrailActivity.this, "Searching for tag", "Please wait...", true, false);
    }

    @Override
    protected void onPause() {
    	Trails trails = new Trails(TrailActivity.this);
    	trails.updateTrail(trail);
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
		if (pd!=null) {
			pd.dismiss();
		}
    }
}
