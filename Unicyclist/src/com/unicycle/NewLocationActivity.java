package com.unicycle;

import java.util.Collections;
import java.util.Comparator;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class NewLocationActivity extends Activity {

	private EditText name;
	private EditText description;
	private EditText directions;
	private Button addButton;
	private Button cancelButton;
	private double latitude;
	private double longitude;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);
        
        //Find view variables
        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        directions = (EditText) findViewById(R.id.directions);
        
        addButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View arg0) {
        		Intent _result = new Intent();
        		_result.putExtra("name",name.getText().toString());
        		_result.putExtra("latitude", latitude);
        		_result.putExtra("longitude",longitude);
        		_result.putExtra("description",description.getText().toString());
        		_result.putExtra("directions",directions.getText().toString());
        		_result.putExtra("rating",5);
        		setResult(Activity.RESULT_OK,_result);
        		NewLocationActivity.this.finish();
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
        intent.putExtra("latitude", (settings.getInt("latitude", 90)/1e6));
        intent.putExtra("longitude", (settings.getInt("longitude",0)/1e6));
        startActivityForResult(intent,UnicyclistActivity.SELECT_LOCATION);
    }
    
    @Override
    protected void onActivityResult(
        int aRequestCode, int aResultCode, Intent aData) {
        switch (aRequestCode) {
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
