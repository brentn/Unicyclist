package com.unicycle.skills;

import java.util.Date;

public class Ability {
	
	private int _id = -1;
	private int _personId;
	private Date _date;
	private int _proficiency;
	
	public Ability(int personId, Date date, int proficiency) {
		this._personId = personId;
		this._date = date;
		this._proficiency = proficiency;
	}
	
	public Ability(int id, int personId, Date date, int proficiency) {
		this._id = id;		
		this._personId = personId;
		this._date = date;
		this._proficiency = proficiency;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public int getPersonId() {
		return this._personId;
	}
	
	public void setPersonId(int personId) {
		this._personId = personId;
	}
	
	public Date getDate() {
		return this._date;
	}
	
	public int getProficiency() {
		return this._proficiency;
	}

}
