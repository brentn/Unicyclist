package com.unicycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Image {
	
	private static final String IMAGE_STORE = "com.unicycle/images/";
	
	private int _id;
	private Uri _uri;
	private int _imageHash;
	private Double _latitude;
	private Double _longitude;
	private String _description;
	
	public Image(Uri uri) {
		//ensure data directory exists
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
	
	public Image (int id, int hash, Uri uri, double lat, double lon, String desc) {
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
		String correctPath = Environment.getExternalStorageDirectory()+IMAGE_STORE;
		String filename = "image0";
		File dest = new File(correctPath);
		File[] files = dest.listFiles();
		if (files != null) {
			filename = "image"+files[files.length].getName().substring(5);
		}
		Log.d("com.unicycle","Path is: "+uri.getPath());
		if (uri.getPath().contains(correctPath)) {
			return uri; //the image is already where it should be
		}
		try {
			FileInputStream source = new FileInputStream(new File(uri.getPath()));
			try {
				File destFile = new File(dest,filename);
				FileOutputStream destination = new FileOutputStream(destFile);
				try {
					FileUtils.copyFile(source, destination);
					return uri.fromFile(destFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				Log.e("com.unicycle","The output directory was missing: "+dest.getName());
			}
		} catch (FileNotFoundException e) {
			Log.e("com.unicycle","The input file was missing: "+uri.getPath());
		}
		return uri;
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
