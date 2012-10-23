package com.brentandjody.prayerlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrayerRequest {
	
	//Public Constants
	public static final int UNANSWERED = 0;
	public static final int ANSWERED = 1;
	public static final int NO_ANSWER = 2;
	
	//Private Variables
	private int _id;
	private String _description;
	private boolean _checked;
	private int _answered;
	private Date _requestDate;
	private List<SubList> _subLists;
	private List<JournalEntry> _journal;
	
	//Constructors
	public PrayerRequest(String description) {
		this._id = -1;
		this._description = description;
		this._checked = false;
		this._answered = UNANSWERED;
		this._requestDate = new Date();
		this._subLists = new ArrayList<SubList>();
		this._journal = new ArrayList<JournalEntry>();
	}
	
	public PrayerRequest (int id,String description,boolean checked,int answered,Date requestDate, List<SubList> subLists, List<JournalEntry> journal) {
		this._id = id;
		this._description = description;
		this._checked = checked;
		this._answered = answered;
		this._requestDate = requestDate;
		this._subLists = subLists;
		this._journal = journal;
	}
	
	@Override 
	public String toString() {
		return this._description;
	}
	
	//Getters & Setters
	public void setId(int id) {
		this._id = id;
	}
	public int getId() {
		return _id;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	public String getDescription() {
		return this._description;
	}
	public void setChecked(boolean checked) {
		this._checked = checked;
	}
	public boolean getChecked() {
		return this._checked;
	}
	public void setAnswered(int answered) {
		this._answered = answered;
	}
	public int getAnswered() {
		return _answered;
	}
	public void setRequestDate(Date requestDate) {
		this._requestDate = requestDate;
	}
	public Date getRequestDate() {
		return this._requestDate;
	}
	public void setSubLists(List<SubList> subLists) {
		this._subLists = subLists;
	}
	public List<SubList> getSubLists() {
		return this._subLists;
	}
	public void setJournal(List<JournalEntry> journal) {
		this._journal = journal;
	}
	public List<JournalEntry> getJournal() {
		return this._journal;
	}

}
