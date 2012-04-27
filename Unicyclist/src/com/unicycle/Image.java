package com.unicycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Image {
	
	private static final String IMAGE_STORE = "/com.unicycle/images/";
	
	private int _id;
	private Uri _uri;
	private int _imageHash;
	private Double _latitude;
	private Double _longitude;
	private String _description;
	
	Context mContext;
	
	public Image(Context context,Uri uri) {
		//ensure data directory exists
		mContext = context;
		String dirname = Environment.getExternalStorageDirectory() + IMAGE_STORE;;
		File sddir = new File(dirname);
		sddir.mkdirs();
		_id = -1;
		_imageHash = 0;
		_uri = getLocalImage(uri); //this will replace _imageHash, if a file is created
		_latitude=0d;
		_longitude=0d;
		_description = "";
	}
	
	public Image(Context context,Uri uri,double lat,double lon) {
		mContext = context;
		String dirname = Environment.getExternalStorageDirectory() + IMAGE_STORE;;
		File sddir = new File(dirname);
		sddir.mkdirs();
		_id = -1;
		_imageHash = 0;
		_uri = getLocalImage(uri); //this will replace _imageHash, if a file is created
		_latitude=lat;
		_longitude=lon;
		_description = "";		
	}
	
	public Image (Context context,int id, int hash, Uri uri, double lat, double lon, String desc) {
		mContext = context;
		//ensure data directory exists
		String dirname = Environment.getExternalStorageDirectory() + IMAGE_STORE;;
		File sddir = new File(dirname);
		sddir.mkdirs();
		_id = id;
		_imageHash = 0;
		_uri = getLocalImage(uri);  //this will replace _imageHash, if a file is created
		_latitude = lat;
		_longitude = lon;
		_description = desc;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true;}
		if (! (o instanceof Image)) { return false; }
	    Image  that = (Image) o;
		return (this._uri == that._uri);
	}
	
	@Override
	public  int hashCode() {
		int result = 43;
		result = 31*result + (int) Double.doubleToLongBits(this._latitude);
		result = 31*result + (int) Double.doubleToLongBits(this._longitude);
		result = 31*result + this._imageHash;
		return result;
	}
	
	private Uri getLocalImage(Uri uri) {
		Uri result = uri;
		String correctPath = Environment.getExternalStorageDirectory()+IMAGE_STORE;
		Images images = new Images(mContext);
		File dest = new File(correctPath);
		Log.d("com.unicycle","Path is: "+uri.getPath());
		if (uri.getPath().contains(correctPath)) {
			return result; //the image is already where it should be
		}
		try {
			InputStream is = mContext.getContentResolver().openInputStream(uri);;
			FileInputStream source = (FileInputStream) is;
			try {
				String filename = images.getNextFileName(correctPath);
				File destFile = new File(dest,filename);
				FileOutputStream destination = new FileOutputStream(destFile);
				try {
					FileUtils.copyFile(source, destination);
					Log.i("com.unicycle","file copied successully");
					result = Uri.fromFile(destFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.e("com.unicycle","The output directory was missing: "+dest.getName());
			}
		} catch (FileNotFoundException e) {
			Log.e("com.unicycle","The input file was missing: "+uri.getPath());
		}
		return result;
	}
	
	  //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=70;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
	
	public int getImageHash() {
		return this._imageHash;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
	}
	
	public Uri getUri() {
		return _uri;
	}
	
	public void setUri(Uri uri) {
		this._uri = uri;
	}
	
	public double getLatitude() {
		return _latitude;
	}
	
	public double getLongitude() {
		return _longitude;
	}
	
	public void setCoordinates(double lat, double lon) {
		_latitude = lat;
		_longitude = lon;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String desc) {
		_description = desc;
	}
	
}
