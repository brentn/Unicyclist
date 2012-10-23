package com.unicycle.rides;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.unicycle.Person;
import com.unicycle.skills.Skill;
import com.unicycle.tags.Tag;
import com.unicycle.tags.Tags;

public class Ride {
	
	private int _id = -1;
	private Date _date;
	private int _locationId;
	private int _trailId = -1;
	private List<Person> _participants = new ArrayList<Person>();
	private double _length = -1;
	private boolean _completed = false;
	private String _comments = "";
	private int _trailCondition = -1;
	private int _rideQuality = -1;
	private List<Tag> _tags = new ArrayList<Tag>();

	public Ride(Date date, int locationId) {
		this._date = date;
		this._locationId = locationId;
	}
	
	public Ride(Date date, int locationId, List<Person> people) {
		this._date = date;
		this._locationId = locationId;
		this._participants = people;
	}
	
	public Ride(Date date, int locationId, int trailId) {
		this._date = date;
		this._locationId = locationId;
		this._trailId = trailId;
	}
	
	public Ride(Date date, int locationId, int trailId, List<Person> people) {
		this._date = date;
		this._locationId = locationId;
		this._trailId = trailId;
		this._participants = people;
	}
	
	public Ride(int id, Date date, int locationId, int trailId, double length, boolean completed, int trailCondition, int rideQuality, String comments) {
		this._id = id;
		this._date = date;
		this._locationId = locationId;
		this._trailId = trailId;
		this._length = length;
		this._completed = completed;
		this._trailCondition = trailCondition;
		this._rideQuality = rideQuality;
		this._comments = comments;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public Date getDate() {
		return this._date;
	}
	
	public void setDate(Date date) {
		this._date = date;
	}
	
	public int getLocationId() {
		return this._locationId;
	}
	
	public void setLocationId(int locationId) {
		this._locationId = locationId;
	}
	
	public int getTrailId() {
		return this._trailId;
	}
	
	public void setTrailId(int trailId) {
		this._trailId = trailId;
	}
	
	public double getLength() {
		return this._length;
	}
	
	public void setLength(double length) {
		this._length = length;
	}
	
	public boolean completed() {
		return this._completed;
	}
	
	public void setCompleted(boolean completed) {
		this._completed = completed;
	}
	
	public String getComments() {
		return this._comments;
	}
	
	public void setComments(String comments) {
		this._comments = comments;
	}
	
	public int getTrailCondition() {
		return this._trailCondition;
	}
	
	public void setTrailCondition(int trailCondition) {
		this._trailCondition = trailCondition;
	}
	
	public int getRideQuality() {
		return this._rideQuality;
	}
	
	public void setRideQuality(int rideQuality) {
		this._rideQuality = rideQuality;
	}
	
	public List<Person> getParticipants() {
		return this._participants;
	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
	
	public void addTag(Context context,String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tags.addTagFor(Ride.this, tag);
		_tags.add(tag);
		tags.close();
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeTagFor(Ride.this, tag);
			_tags.remove(tag);
		}
		tags.close();
	}
	
}
