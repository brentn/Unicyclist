package com.unicycle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity {
	
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
//        Toast.makeText(LocationActivity.this,location.getName(), Toast.LENGTH_SHORT).show();
        
        TextView name = (TextView) findViewById(R.id.name);
        TextView description = (TextView) findViewById(R.id.description);
        TextView directions = (TextView) findViewById(R.id.directions);
        TextView tags = (TextView) findViewById(R.id.tags);
        ImageButton addImageButton = (ImageButton) findViewById(R.id.addImageButton);
        
        addImageButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
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
