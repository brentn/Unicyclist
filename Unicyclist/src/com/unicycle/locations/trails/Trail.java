package com.unicycle.locations.trails;

import java.util.ArrayList;
import java.util.List;

import com.unicycle.comments.Comment;
import com.unicycle.comments.Comments;
import com.unicycle.images.Image;
import com.unicycle.images.Images;
import com.unicycle.locations.Location;
import com.unicycle.locations.features.Feature;
import com.unicycle.locations.features.Features;
import com.unicycle.tags.Tag;
import com.unicycle.tags.Tags;

import android.content.Context;

public class Trail extends Object {
	
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
	private List<Feature> _features = new ArrayList<Feature>();
	private List<GPSTrack> _tracks = new ArrayList<GPSTrack>();
	private List<Image> _images = new ArrayList<Image>();
	private List<Tag> _tags = new ArrayList<Tag>();
	private List<Comment> _comments = new ArrayList<Comment>();
	
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
			List<Feature> features, List<GPSTrack> tracks, List<Image> images, List<Tag> tags,
			List<Comment> comments) {
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
		this._comments = comments;
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
			List<Feature> features, List<GPSTrack> tracks, List<Image> images, List<Tag> tags,
			List<Comment> comments) {
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
		this._comments = comments;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true;}
		if (! (o instanceof Trail)) { return false; }
		Trail that = (Trail) o;
		return this._name == that._name &&
				(this._latitude - that._latitude < .0001) &&
				(this._longitude - that._longitude < .0001);
	}
	
	@Override
	public  int hashCode() {
		int result = 43;
		result = this._name.hashCode();
		result = 31*result + (int) Double.doubleToLongBits(this._latitude);
		result = 31*result + (int) Double.doubleToLongBits(this._longitude);
		return result;
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
	
	public void addImage(Context context, Image image) {
		Images images = new Images(context);
		if (image != null) {
			images.addImageFor(Trail.this,image);
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
	
	public List<Feature> getFeatures() {
		return this._features;
	}
	
	public void setFeatures(List<Feature> features) {
		this._features = features;
	}

	public void addFeature(Context context, Feature feature) {
		Features features = new Features(context);
		features.addFeatureFor(Trail.this, feature);
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
	
	public List<GPSTrack> getTracks() {
		return this._tracks;
	}
	
	public void setTracks(List<GPSTrack> tracks) {
		this._tracks = tracks;
	}
	
//	public void addTrack(Context context, Track track) {
//		Tracks tracks = new Tracks(context);
//		tracks.addTrackFor(Trail.this, track);
//		_tracks.add(track);
//		tracks.close();
//	}
	
//	public void removeTrack(Context context, int trackId) {
//		Tracks tracks = new Tracks(context);
//		Track track = tracks.getTrack(trackId);
//		if (track != null) {
//			tracks.removeTrackFor(Trail.this, track);
//			_tracks.remove(track);
//		}
//	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}

	public void addTag(Context context,String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tags.addTagFor(Trail.this, tag);
		_tags.add(tag);
		tags.close();
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeTagFor(Trail.this, tag);
			_tags.remove(tag);
		}
		tags.close();
	}
	
	public void setComments(List<Comment> comments) {
		this._comments = comments;
	}
	
	public List<Comment> getComments() {
		return this._comments;
	}
	
	public void addComment(Context context, Comment comment) {
		Comments comments = new Comments(context);
		comments.addCommentFor(Trail.this, comment);
		_comments.add(0, comment);
		comments.close();
	}
	
	public void removeComment(Context context, int commentId) {
		Comments comments = new Comments(context);
		Comment comment = comments.getComment(commentId);
		if (comment != null) {
			comments.removeCommentFor(Trail.this, comment);
			_comments.remove(comment);
		}
	}
	
}
