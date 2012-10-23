package com.brentandjody.prayerlist;

import java.util.Date;

public class Answer {
	
	//private variables
	int _id;
	int _requestId;
	Date _answerDate;
	int _rating;
	String _details;
	
	//constructors
	public Answer(int requestId) {
		this._id = -1;
		this._requestId = requestId;
		this._answerDate = new Date();
		this._rating = -1;
		this._details = "";
	}
	
	public Answer(int id, int requestId, Date answerDate, int rating, String details) {
		this._id = id;
		this._requestId = requestId;
		this._answerDate = answerDate;
		this._rating = rating;
		this._details = details;
	}
	
	@Override 
	public String toString() {
		return this._details;
	}
	
	//getters and setters
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getRequestId() {
		return this._requestId;
	}
	
	public void setRequestId(int requestId) {
		this._requestId = requestId;
	}

	public Date getAnswerDate() {
		return this._answerDate;
	}
	
	public void setAnswerDate(Date answerDate) {
		this._answerDate = answerDate;
	}
	
	public int getRating() {
		return this._rating;
	}
	
	public void setRating(int rating) {
		this._rating = rating;
	}
	
	public String getDetails() {
		return this._details;
	}
	
	public void setDetails(String details) {
		this._details = details;
	}
}
