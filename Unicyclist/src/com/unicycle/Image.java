package com.unicycle;

public class Image {
	
	public static final int LOCATION_IMAGE = 1;
	public static final int TRAIL_IMAGE = 2;
	public static final int CHALLENGE_IMAGE = 3;

	private int _id;
	private int _type;
	private String _url;;
	private String _localPath;
	private Double _latitude;
	private Double _longitude;
	private String _description;
	
	public Image(String fileLocation) {
		if (fileLocation.toLowerCase().substring(0,4) == "http" ) {
			_url = fileLocation;
			_localPath = "";
		} else {
			_url = "";
			_localPath = fileLocation;
		}
		_localPath = "";
		_latitude=0d;
		_longitude=0d;
		_description = "";
	}
	
	public Image (String  url, String path) {
		_url = url;
		_localPath = path;
		_latitude = 0d;
		_longitude = 0d;
		_description = "";
	}
	
	public Image (String url, String path, double lat, double lon, String desc) {
		_url = url;
		_localPath = path;
		_latitude = lat;
		_longitude = lon;
		_description = desc;
	}
	
	public Image (int id,String url, String path, double lat, double lon, String desc) {
		_id = id;
		_url = url;
		_localPath = path;
		_latitude = lat;
		_longitude = lon;
		_description = desc;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
	}
	
	public int getType() {
		return _type;
	}
	
	public void setType(int type) {
		_type = type;
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