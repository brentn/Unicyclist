package com.unicycle.locations.features;

import java.util.List;

import com.unicycle.comments.Comment;
import com.unicycle.comments.Comments;
import com.unicycle.images.Image;
import com.unicycle.images.Images;
import com.unicycle.tags.Tag;
import com.unicycle.tags.Tags;

import android.content.Context;

public class Feature {
	
	private int _id;
	private int _locationId = -1;
	private int _trailId = -1;
	private String _name;
	private double _latitude;
	private double _longitude;
	private String _description = "";
	private String _directions = "";
	private int _difficulty;
	private List<Image> _images;
	private List<Tag> _tags;
	private List<Comment> _comments;
	
	public Feature(int locationId, String name) {
		this._locationId = locationId;
		this._name = name;
	}
	
	public Feature(int locationId, int trailId, String name) {
		this._locationId = locationId;
		this._trailId = trailId;
		this._name = name;
	}
	
	public Feature(int id, int locationId, int trailId, String name, double latitude, double longitude, String description,
			String directions, int difficulty) {
		this._id = id;
		this._locationId = locationId;
		this._trailId = trailId;
		this._name = name;
		this._latitude = latitude;
		this._longitude = longitude;
		this._description = description;
		this._directions = directions;
		this._difficulty = difficulty;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true;}
		if (! (o instanceof Feature)) { return false; }
		Feature that = (Feature) o;
		return 
				(this._latitude - that._latitude < .0001) &&
				(this._longitude - that._longitude < .0001);
	}
	
	@Override
	public  int hashCode() {
		int result = 13;
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
	
	public int getTrailId() {
		return this._trailId;
	}
	
	public void setTrailId(int id) {
		this._trailId = id;
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
			images.addImageFor(Feature.this,image);
			_images.add(image);
		}
	}
	
	public void removeImage(Context context, int imageId) {
		Images images = new Images(context);
		Image image = images.getImage(imageId);
		if (image != null) {
			images.removeImage(image);
			_images.remove(image);
		}
	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}

	public void addTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tags.addTagFor(Feature.this, tag);
		_tags.add(tag);
		tags.close();
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeTagFor(Feature.this, tag);
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
		comments.addCommentFor(Feature.this, comment);
		_comments.add(0, comment);
		comments.close();
	}
	
	public void removeComment(Context context, int commentId) {
		Comments comments = new Comments(context);
		Comment comment = comments.getComment(commentId);
		if (comment != null) {
			comments.removeCommentFor(Feature.this, comment);
			_comments.remove(comment);
		}
	}
	

}
