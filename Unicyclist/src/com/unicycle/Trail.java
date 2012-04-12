package com.unicycle;

import java.util.List;

public class Trail {
	private int _id;
	private int _locationId;
	private String _name;
	private android.location.Location _coordinates;
	private String _description;
	private int _rating;
	private int _difficulty;
	private List<Challenge> _challenges;
	private List<Image> _images;
	private List<Tag> _tags;

	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getLocation() {
		return this._locationId;
	}
	
	public void setLocation(int id) {
		this._locationId = id;
	}
	
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	public double getLatitude() {
		return this._coordinates.getLatitude();
	}
	
	public double getLongitude() {
		return this._coordinates.getLongitude();
	}
	
	public android.location.Location getCoordinates() {
		return this._coordinates;
	}
	
	public void setCoordinates(double latitude, double longitude) {
		this._coordinates.setLatitude(latitude);
		this._coordinates.setLongitude(longitude);
	}
	
	public void setCoordinates(android.location.Location location) {
		this._coordinates = location;
	}
	
	public String getDescription() {
		return this._description;
	}
	
	public void setDescription(String description) {
		this._description = description;
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
	
	public List<Challenge> getChallenges() {
		return this._challenges;
	}
	
	public void setChallenges(List<Challenge> challenges) {
		this._challenges = challenges;
	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
}
