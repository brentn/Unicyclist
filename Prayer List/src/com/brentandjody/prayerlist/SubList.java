package com.brentandjody.prayerlist;

import java.util.Date;

public class SubList {
	
	//public constants
	public static final int SORT_BY_SIZE = 1;
	public static final int SORT_BY_NAME = 2;
	public static final int SORT_BY_LASTUSED = 3; 
	public static final int SORT_BY_MOSTUSED = 4;
	
	//Private variables
	private int _id;
	private String _name;
	private int _size;
	private Date _lastUsed;
	private int _timesUsed;
	
	//Constructors
	public SubList(String name) {
		this._id = -1;
		this._name = name;
		this._size = 0;
		this._lastUsed = new Date();
		this._timesUsed = 0;
	}
	
	public SubList(int id,String name,int size,Date lastUsed,int timesUsed) {
		this._id = id;
		this._name = name;
		this._size = size;
		this._lastUsed = lastUsed;
		this._timesUsed = timesUsed;
	}
	
	@Override
	public String toString() {
		return this._name;
	}
	
	public boolean equals(SubList t) {
		return (t.getId() == this._id);
	}
	
	//Getters and Setters
	public void setId(int id) {
		this._id = id;
	}
	public int getId() {
		return this._id;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getName() {
		return this._name;
	}
	public int getSize() {
		return this._size;
	}
	public void setSize(int size) {
		this._size = size;
	}
	public void setLastUsed(Date date) {
		this._lastUsed = date;
	}
	public Date getLastUsed() {
		return this._lastUsed;
	}
	public int getTimesUsed() {
		return this._timesUsed;
	}
	public void setTimesUsed(int timesUsed) {
		this._timesUsed = timesUsed;
	}
	public void use() {
		this._lastUsed = new Date();
		this._timesUsed++;
		this._size++;
	}
	public void disuse() {
		this._timesUsed--;
		this._size--;
	}
}
