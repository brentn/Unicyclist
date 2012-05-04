package com.unicycle.images;

import java.io.File;

import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class GetPhoto extends Activity {

	private Uri imageUri;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CharSequence[] choices = new CharSequence[] {this.getString(R.string.camera),this.getString(R.string.gallery)};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(this.getString(R.string.get_photo));
        dialog.setOnCancelListener( new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
        });
        dialog.setItems(choices, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
            	Intent intent;
            	switch (item) {
            	case 0:
            		String filename = new Images(GetPhoto.this).getNextFileName(Image.IMAGE_STORE);
            	    intent = new Intent("android.media.action.IMAGE_CAPTURE");
            	    File photo = new File(Environment.getExternalStorageDirectory(), filename);
            	    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            	    imageUri = Uri.fromFile(photo);
            	    startActivityForResult(intent, UnicyclistActivity.CAMERA_REQUEST);
            	    break;
            	case 1:
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, UnicyclistActivity.SELECT_PICTURE);
            		break;
            	}
            }
        }).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 Uri selectedImage=Uri.EMPTY;
		 if (resultCode == RESULT_OK) {
			 switch (requestCode) {
			 case UnicyclistActivity.CAMERA_REQUEST:
		            selectedImage = imageUri;
				 break;
			 case UnicyclistActivity.SELECT_PICTURE:
		            selectedImage = data.getData();
				 break;
			 }
		    	Intent intent = new Intent();
		    	intent.setData(selectedImage);
		    	setResult(RESULT_OK, intent);
		 }
    	finish();
	}
		    
}

