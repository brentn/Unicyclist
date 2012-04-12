package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

public class Location {
	
	//private variables
	private int _id;
	private String _name;
	private double _latitude;
	private double _longitude;
	private String _description;
	private String _directions;
	private int _rating;
	private boolean _favourite;
	private boolean _deleted;
	private List<Image> _images;
	private List<Tag> _tags;
	
	//constructors
	public Location() {
		this._id=-1;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
	}
	
	public Location(int id, String name, double latitude, double longitude) {
		this._id = id;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
	}
	
	public Location(String name, double latitude, double longitude, String description, String directions, int rating) {
		this._id=-1;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._rating = rating;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
	}
	
	public Location(int id, String name, double latitude, double longitude, String description, String directions, int rating) {
		this._id = id;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._rating = rating;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getId() {
		return this._id;
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
	
	public void setCoordinatesE6(int latitudeE6, int longitudeE6) {
		this._latitude = (latitudeE6 / 1e6);
		this._longitude = (longitudeE6 / 1e6);
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
	
	public int getRating() {
		return this._rating;
	}
	
	public void setRating(int rating) {
		this._rating = rating;
	}
	
	public void setFavourite() {
		this._favourite = true;
	}
	
	public void clearFavourite() {
		this._favourite = false;
	}
	
	public void toggleFavourite() {
		this._favourite =  (! this._favourite);
	}
	
	public boolean isFavourite() {
		return this._favourite;
	}
	
	public void delete() {
		this._deleted = true;
	}
	
	public boolean isDeleted() {
		return this._deleted;
	}
	
	public List<Tag> getTags() {
		return _tags;
	}
	
	public String getTagString() {
		String result = "";
		Iterator<Tag> i = _tags.iterator();
		while (i.hasNext()) {
			result = result + i.next().getName() + "      ";
		}
		return result.trim();
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
	
	public void addTag(Context context,String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tag.setId(tags.addTag(tag));
		tags.addLocationTag(this, tag);
		_tags.add(tag);
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeLocationTag(this, tag);
			_tags.remove(tag);
		}
	}
}
