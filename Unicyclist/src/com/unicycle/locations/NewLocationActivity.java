package com.unicycle.locations;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.R.id;
import com.unicycle.R.layout;
import com.unicycle.images.GetPhoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class NewLocationActivity extends Activity {

	private EditText name;
	private ImageView photo;
	private EditText description;
	private EditText directions;
	private Button addButton;
	private Button cancelButton;
	private double latitude;
	private double longitude;
	private Uri selectedImageUri = Uri.EMPTY;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
        
        //Find view variables
        photo = (ImageView) findViewById(R.id.image);
        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        directions = (EditText) findViewById(R.id.directions);
        
        photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                startActivityForResult(new Intent(NewLocationActivity.this,GetPhoto.class),UnicyclistActivity.GET_PHOTO);
			}
        });
        addButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		if (name.getText().toString().trim().length() == 0) {
        			new AlertDialog.Builder(NewLocationActivity.this)
        			.setTitle("Oops!")
        			.setMessage("You must give this location a Name.")
        			.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,	int which) {
        				}
        			}).show();
        		} else {
	        		Intent _result = new Intent();
	        		_result.putExtra("name",name.getText().toString());
	        		_result.putExtra("latitude", latitude);
	        		_result.putExtra("longitude",longitude);
	        		_result.putExtra("description",description.getText().toString());
	        		_result.putExtra("directions",directions.getText().toString());
	        		_result.putExtra("rating",5);
	        		_result.putExtra("uri",selectedImageUri.toString());
	        		setResult(Activity.RESULT_OK,_result);
	        		NewLocationActivity.this.finish();
        		}
        	}
        });
        cancelButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		NewLocationActivity.this.finish();
        	}
        });
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(NewLocationActivity.this);
        Intent intent = new Intent(NewLocationActivity.this, LocationPickerActivity.class);
        intent.putExtra("latitude", (settings.getInt("latitude", 40000000)/1e6));
        intent.putExtra("longitude", (settings.getInt("longitude",-122000000)/1e6));
        startActivityForResult(intent,UnicyclistActivity.SELECT_LOCATION);
    }
    
    @Override
    protected void onActivityResult(
        int aRequestCode, int aResultCode, Intent aData) {
        switch (aRequestCode) {
    	case UnicyclistActivity.GET_PHOTO:
	    	selectedImageUri = aData.getData();
            getContentResolver().notifyChange(selectedImageUri, null);
            ContentResolver cr = getContentResolver();
            try {
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
               photo.setImageBitmap(bitmap);
            } catch (Exception e) {
               Log.e("Camera", e.toString());
            }
    		break;
        case UnicyclistActivity.SELECT_LOCATION:
        	if ((aData != null) && (aResultCode == Activity.RESULT_OK)) {
        		latitude = aData.getDoubleExtra("latitude", 0);
        		longitude = aData.getDoubleExtra("longitude", 0);
        	} else {
        		NewLocationActivity.this.finish();
        	}
            break;
        }
        super.onActivityResult(aRequestCode, aResultCode, aData);
    }
	    
}
