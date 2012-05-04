package com.unicycle.comments;

import java.util.Date;

public class Comment {
	
	private int _id = 0;
	private String _comment = "";
	private String _user = "";
	private Date _date;
	
	public Comment() {
	}
	
	public  Comment(String comment) {
		this._date = new Date();
		this._comment = comment;
	}
	
	public Comment(String comment, String user) {
		this._date = new Date();
		this._comment = comment;
		this._user = user;
	}
	
	public Comment(int id, Date date, String comment, String user) {
		this._date = new Date();
		this._comment = comment;
		this._user = user;
		this._date = date;
		this._id = id;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public String getComment() {
		return this._comment;
	}
	
	public void setComment(String comment) {
		this._comment = comment;
	}
	
	public String getUser() {
		return this._user;
	}
	
	public void setUser(String user) {
		this._user = user;
	}
	
	public Date getDate() {
		return this._date;
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) { return true;}
		if (! (that instanceof Comment)) { return false; }
		return (this.hashCode() == that.hashCode());
	}
	
	@Override
	public  int hashCode() {
		int result = this._date.hashCode();
		result = result * 32 + _comment.hashCode();
		return result;
	}

}
