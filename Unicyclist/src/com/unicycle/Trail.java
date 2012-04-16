package com.unicycle;

import java.util.ArrayList;
import java.util.List;

public class Trail {
	
	static final int DIFFICULTY_EASIEST = 1;
	static final int DIFFICULTY_MODERATE = 2;
	static final int DIFFICULTY_HARD = 3;
	static final int DIFFICULTY_HARDEST = 4;
	
	private int _id;
	private int _locationId;
	private String _name = "";
	private double _latitude;
	private double _longitude;
	private String _description = "";
	private String _directions = "";
	private float _length = 0;
	private int _rating = 5;
	private int _difficulty = 0;
	private List<Challenge> _features = new ArrayList<Challenge>();
	private List<GPSTrack> _tracks = new ArrayList<GPSTrack>();
	private List<Image> _images = new ArrayList<Image>();
	private List<Tag> _tags = new ArrayList<Tag>();
	
	public Trail(Location location) {
		this._id = -1;
		this._locationId = location.getId();
		this._latitude = location.getLatitude();
		this._longitude = location.getLongitude();
	}
	
	public Trail(int id, int locationId, String name, double latitude, double longitude, 
			String description, String directions, float length, int rating, int difficulty) {
		this._id = id;
		this._locationId = locationId;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._length = length;
		this._rating = rating;
		this._difficulty = difficulty;
	}

	public Trail(int id, int locationId, String name, double latitude, double longitude, 
			String description, String directions, float length, int rating, int difficulty,
			List<Challenge> features, List<GPSTrack> tracks, List<Image> images, List<Tag> tags) {
		this._id = id;
		this._locationId = locationId;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._length = length;
		this._rating = rating;
		this._difficulty = difficulty;
		this._features = features;
		this._tracks = tracks;
		this._images = images;
		this._tags = tags;
	}

	public Trail(int locationId, String name, double latitude, double longitude, 
			String description, String directions, float length, int rating, int difficulty) {
		this._id = -1;
		this._locationId = locationId;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._length = length;
		this._rating = rating;
		this._difficulty = difficulty;
	}

	public Trail(int locationId, String name, double latitude, double longitude, 
			String description, String directions, float length, int rating, int difficulty,
			List<Challenge> features, List<GPSTrack> tracks, List<Image> images, List<Tag> tags) {
		this._id = -1;
		this._locationId = locationId;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._length = length;
		this._rating = rating;
		this._difficulty = difficulty;
		this._features = features;
		this._tracks = tracks;
		this._images = images;
		this._tags = tags;
	}

	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getLocationId() {
		return this._locationId;
	}
	
	public void setLocationId(int id) {
		this._locationId = id;
	}
	
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	public double getLatitude() {
		return this._latitude;
	}
	
	public double getLongitude() {
		return this._longitude;
	}
	
	public void setCoordinates(double latitude, double longitude) {
		this._latitude = latitude;
		this._longitude = longitude;
	}
	
	public void setCoordinates(android.location.Location location) {
		this._latitude = location.getLatitude();
		this._longitude = location.getLongitude();
	}
	
	public String getDescription() {
		return this._description;
	}
	
	public void setDescription(String description) {
		this._description = description;
	}
	
	public String getDirections() {
		return this._directions;
	}
	
	public void setDirections(String directions) {
		this._directions = directions;
	}
	
	public float getLength() {
		return this._length;
	}
	
	public void setLength(float length) {
		this._length = length;
	}
	
	public int getRating() {
		return this._rating;
	}
	
	public void setRating(int rating) {
		this._rating = rating;
	}
	
	public int getDifficulty() {
		return this._difficulty;
	}
	
	public void setDifficulty(int difficulty) {
		this._difficulty = difficulty;
	}
	
	public List<Image> getImages() {
		return this._images;
	}
	
	public void setImages(List<Image> images) {
		this._images = images;
	}
	
	public List<Challenge> getFeatures() {
		return this._features;
	}
	
	public void setFeatures(List<Challenge> features) {
		this._features = features;
	}
	
	public List<GPSTrack> getTracks() {
		return this._tracks;
	}
	
	public void setTracks(List<GPSTrack> tracks) {
		this._tracks = tracks;
	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
}
