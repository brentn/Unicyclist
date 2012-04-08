package com.unicycle;


public class Tag {
	private int _id;
	private String _name;
	private int _timesUsed;
	
	public Tag() {
	}
	
	public Tag(String name) {
		this._name = name;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setName(String name) {
		name=name.toLowerCase();
		name=name.replaceAll("\\s+", " "); //remove extra whitespace
		this._name = name;
	}
	
	public String getName() {
		return this._name;
	}
}	
