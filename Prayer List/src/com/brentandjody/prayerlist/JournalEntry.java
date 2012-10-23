package com.brentandjody.prayerlist;

import java.util.Date;

public class JournalEntry {

	//Private Variables
	private int _id;
	private int _requestId;
	private Date _date;
	private String _entry;
	
	//Constructors
	public JournalEntry(int requestId, Date date, String entry) {
		this._id = -1;
		this._requestId = requestId;
		this._date = date;
		this._entry = entry;
	}
	
	public JournalEntry(int id, int requestId, Date date, String entry) {
		this._id = id;
		this._requestId = requestId;
		this._date = date;
		this._entry = entry;
	}
	
	//Getters and Setters
	public void setId(int id) {
		this._id = id;
	}
	public int getId() {
		return this._id;
	}
	public void setRequestId(int requestId) {
		this._requestId = requestId;
	}
	public int getRequestId() {
		return this._requestId;
	}
	public void setDate(Date date) {
		this._date = date;
	}
	public Date getDate() {
		return this._date;
	}
	public void setEntry(String entry) {
		this._entry = entry;
	}
	public String getEntry() {
		return this._entry;
	}
}
