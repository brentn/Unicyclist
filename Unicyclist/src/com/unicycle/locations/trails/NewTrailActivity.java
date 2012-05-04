package com.unicycle.locations.trails;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.UnicyclistApplication;
import com.unicycle.R.drawable;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.images.GetPhoto;
import com.unicycle.locations.LocationPickerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

public class NewTrailActivity extends Activity {
	
	private EditText name;
	private ImageView cameraButton;
	private double latitude;
	private double longitude;
	private EditText description;
	private EditText directions;
	private int difficulty=1;
	private Uri imageUri=Uri.EMPTY;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trail);
        
        cameraButton = (ImageView) findViewById(R.id.photoButton);
        name = (EditText) findViewById(R.id.trailName);
        Gallery difficultySelector = (Gallery) findViewById(R.id.difficultyGallery);
        Button trailHeadButton = (Button) findViewById(R.id.trailCoordinates);
        description = (EditText) findViewById(R.id.description);
        directions = (EditText) findViewById(R.id.directions);        
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button addButton = (Button) findViewById(R.id.addButton);
        
        latitude = ((UnicyclistApplication) getApplication()).getCurrentLocation().getLatitude();
        longitude = ((UnicyclistApplication) getApplication()).getCurrentLocation().getLongitude();
        
        cameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                startActivityForResult(new Intent(NewTrailActivity.this,GetPhoto.class),UnicyclistActivity.GET_PHOTO);
			}
        });
        
        difficultySelector.setAdapter(new ImageAdapter(this));
        difficultySelector.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
	        	difficulty = position+1;
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	            // Do nothing
	        }
        });
        
        trailHeadButton.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View view) {
				Intent intent = new Intent(NewTrailActivity.this,LocationPickerActivity.class);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude",longitude);
				startActivityForResult(intent,UnicyclistActivity.SELECT_LOCATION);
			}
        	
        });
      
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
        		Intent _result = new Intent();
        		setResult(Activity.RESULT_CANCELED,_result);
        		NewTrailActivity.this.finish();	
			}
        });
        
        addButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg) {
        		if (name.getText().toString().trim().length() == 0) {
        			new AlertDialog.Builder(NewTrailActivity.this)
        			.setTitle("Oops!")
        			.setMessage("You must give this trail a Name.")
        			.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,	int which) {
        				}
        			}).show();
        		} else {
	        		Intent _result = new Intent();
	        		_result.putExtra("name",name.getText().toString());
	        		_result.putExtra("latitude", latitude);
	        		_result.putExtra("longitude",longitude);
	        		_result.putExtra("difficulty", difficulty);
	        		_result.putExtra("description",description.getText().toString());
	        		_result.putExtra("directions",directions.getText().toString());
	        		_result.putExtra("rating",5);
	        		_result.putExtra("uri",imageUri.toString());
	        		setResult(Activity.RESULT_OK,_result);
	        		NewTrailActivity.this.finish();
        		}
        	}
        });
        
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == Activity.RESULT_OK) {
        	switch (requestCode) {
        	case UnicyclistActivity.GET_PHOTO:
    	    	imageUri = data.getData();
                getContentResolver().notifyChange(imageUri, null);
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
                    cameraButton.setImageBitmap(bitmap);
                } catch (Exception e) {
                   Log.e("Camera", e.toString());
                }
        		break;
        	case UnicyclistActivity.SELECT_LOCATION:
        		latitude = data.getDoubleExtra("latitude",0);
        		longitude = data.getDoubleExtra("longitude", 0);
        		break;
        	}
        }  
    } 
    
    private  class ImageAdapter extends BaseAdapter {
        
        public ImageAdapter(Context c) {
            mContext = c;
        }
 
        public int getCount() {
            return mImageIds.length;
        }
 
        public Object getItem(int position) {
            return position;
        }
 
        public long getItemId(int position) {
            return (position+1);
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
 
            ImageView i = new ImageView(mContext);
            
            i.setImageResource(mImageIds[position]);
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            return i;
        }
 
        private Context mContext;
 
        private Integer[] mImageIds = {
                R.drawable.easiest,
                R.drawable.more_difficult,
                R.drawable.most_difficult,
                R.drawable.double_diamond,
        };
    }
}


