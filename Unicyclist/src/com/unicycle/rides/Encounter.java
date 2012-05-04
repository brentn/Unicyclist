package com.unicycle.rides;

public class Encounter {
	
	private int _id;
	private int _featureId;
	private int _personId;
	private int _numAttempts;
	private int _numLanded;
	
	public Encounter(int featureId, int personId, int numAttempts, int numLanded) {
		this._featureId = featureId;
		this._personId = personId;
		this._numAttempts = numAttempts;
		this._numLanded = numLanded;
	}
	
	public Encounter(int id, int featureId, int personId, int numAttempts, int numLanded) {
		this._id = id;
		this._featureId = featureId;
		this._personId = personId;
		this._numAttempts = numAttempts;
		this._numLanded = numLanded;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getFeatureId() {
		return this._featureId;
	}
	
	public void setFeatureId(int featureId) {
		this._featureId = featureId;
	}
	
	public int getPersonId() {
		return this._personId;
	}
	
	public void setPersonId(int personId) {
		this._personId = personId;
	}
	
	public double getSuccessRate() {
		if (this._numAttempts == 0) {
			return 0;
		} 
		return (this._numLanded / this._numAttempts);
	}

}
