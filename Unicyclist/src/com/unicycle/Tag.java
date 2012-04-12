package com.unicycle;


public class Tag {
	
	private int _id;
	private String _name;
	private int _usage;
	
	public Tag() {
		this.setUsage(0);
	}
	
	public Tag(String name) {
		this.setName(name);
		this.setUsage(0);
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
	
	public int getUsage() {
		return this._usage;
	}
	
	public void setUsage(int usage) {
		this._usage = usage;
	}
	
}	
