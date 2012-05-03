package com.unicycle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class Location extends Object {	
	
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
	private String _md5sum = "";
	private List<Image> _images;
	private List<Tag> _tags;
	private List<Comment> _comments;
	private List<Feature> _features;
	
	//constructors
	public Location() {
		this._id=-1;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
		this._comments = new ArrayList<Comment>();
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
		this._comments = new ArrayList<Comment>();
	}
	
	public Location(String name, double latitude, double longitude, String description, String directions, int rating, String md5sum) {
		this._id=-1;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._rating = rating;
		this._md5sum = md5sum;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
		this._comments = new ArrayList<Comment>();
	}
	
	public Location(int id, String name, double latitude, double longitude, String description, String directions, int rating, String md5sum) {
		this._id = id;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._rating = rating;
		this._md5sum = md5sum;
		this._favourite = false;
		this._deleted = false;
		this._tags = new ArrayList<Tag>();
		this._images = new ArrayList<Image>();
		this._comments = new ArrayList<Comment>();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true;}
		if (! (o instanceof Location)) { return false; }
		Location that = (Location) o;
		return this._name == that._name &&
				(this._latitude - that._latitude < .0001) &&
				(this._longitude - that._longitude < .0001);
	}
	
	@Override
	public  int hashCode() {
		int result = 43;
		result = 31 * this._name.hashCode();
		result = 31*result + (int) Double.doubleToLongBits(this._latitude);
		result = 31*result + (int) Double.doubleToLongBits(this._longitude);
		return result;
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
	
	public String getMd5sum() {
		return this._md5sum;
	}
	
	public void setMd5sum(String md5sum) {
		this._md5sum = md5sum;
	}
	
	public List<Tag> getTags() {
		return _tags;
	}
	
//	public String getTagString() {
//		String result = "";
//		Iterator<Tag> i = _tags.iterator();
//		while (i.hasNext()) {
//			result = result + i.next().getName() + "      ";
//		}
//		return result.trim();
//	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
	
	public void addTag(Context context,String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tags.addTagFor(Location.this, tag);
		_tags.add(tag);
		tags.close();
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeTagFor(Location.this, tag);
			_tags.remove(tag);
		}
		tags.close();
	}
	
	public List<Image> getImages() {
		return this._images;
	}
	
	public void setImages(List<Image> images) {
		this._images = images;
	}
	
	public void addImage(Context context, Image image) {
		Images images = new Images(context);
		if (image != null) {
			images.addImageFor(Location.this,image);
			_images.add(image);
		}
	}
	
	public void removeImage(Context context, int imageId) {
		Images images = new Images(context);
		Image image = images.getImage(imageId);
		if (image != null) {
			images.removeImage(image);
			for (int x=0; x<this._images.size();x++) {
				if (this._images.get(x).getId() == imageId) {
					this._images.remove(x);
					break;
				}
			}
		}
	}
	
	public List<Comment> getComments() {
		return this._comments;
	}
	
	public void setComments(List<Comment> comments) {
		this._comments = comments;
	}
	
	public void addComment(Context context, Comment comment) {
		Comments comments = new Comments(context);
		comments.addCommentFor(Location.this, comment);
		_comments.add(0, comment);
		comments.close();
	}
	
	public void removeComment(Context context, int commentId) {
		Comments comments = new Comments(context);
		Comment comment = comments.getComment(commentId);
		if (comment != null) {
			comments.removeCommentFor(Location.this, comment);
			_comments.remove(comment);
		}
	}
	
	public List<Feature> getFeatures() {
		return this._features;
	}
	
	public void setFeatures (List<Feature> features) {
		this._features = features;
	}
	
	public void addFeature(Context context, Feature feature) {
		Features features = new Features(context);
		features.addFeatureFor(Location.this, feature);
		_features.add(feature);
		features.close();
	}
	
	public void removeFeature(Context context, int featureId) {
		Features features = new Features(context);
		Feature feature = features.getFeature(featureId);
		if (feature != null) {
			features.removeFeature(feature);
			_features.remove(feature);
		}
	}
	
	public int sortByDistance(android.location.Location myLocation, com.unicycle.Location otherLocation) {
		if ((myLocation==null) || (otherLocation==null)) {
			return 0; //abort to avoid nullPointerExeption
		}
		android.location.Location dest1 = new android.location.Location("");
		android.location.Location dest2 = new android.location.Location("");
		
		dest1.setLatitude(this.getLatitude());
		dest1.setLongitude(this.getLongitude());
		dest2.setLatitude(otherLocation.getLatitude());
		dest2.setLongitude(otherLocation.getLongitude());
		
		double dist1 = myLocation.distanceTo(dest1);
		double dist2 = myLocation.distanceTo(dest2);
		
		if (dist1 < dist2) {
			return -1;
		} else if (dist1 > dist2) {
			return 1;
		}
		return 0;
	}
}
