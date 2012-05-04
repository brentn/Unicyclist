package com.unicycle;

public class Person {
	
	private int _id;
	private String _username;

	public Person(String username) {
		this._username = username;
	}
	
	public Person (int id, String username) {
		this._id = id;
		this._username = username;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public String getUsername() {
		return this._username;
	}
	
	public void setUsername(String username) {
		this._username = username;
	}
}
