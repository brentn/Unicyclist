package com.unicycle;


public class Tag extends Object{
	
	final public static int LOCATION_TAG=1;
	final public static int TRAIL_TAG=2;
	final public static int FEATURE_TAG=3;
	
	private int _id = -1;
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
	
	@Override
	public boolean equals(Object that) {
		if (this == that) { return true;}
		if (! (that instanceof Tag)) { return false; }
		return (this.hashCode() == that.hashCode());
	}
	
	@Override
	public  int hashCode() {
		return this._name.hashCode();
	}
	
}	
