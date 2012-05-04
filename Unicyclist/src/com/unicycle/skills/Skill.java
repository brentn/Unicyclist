package com.unicycle.skills;

import java.util.ArrayList;
import java.util.List;

import com.unicycle.tags.Tag;
import com.unicycle.tags.Tags;

import android.content.Context;

public class Skill {
	
	private int _id = -1;
	private String _name;
	private String _description = "";
	private int _difficulty;
	private List<Tag> _tags = new ArrayList<Tag>();
	
	public Skill(String name, int difficulty) {
		this._name = name;
		this._difficulty = difficulty;
	}
	
	public Skill(int id, String name, String description, int difficulty) {
		this._id = id;
		this._name = name;
		this._description = description;
		this._difficulty = difficulty;
	}
	
	public int getId() {
		return this._id;
	}
	
	public void setId(int id) {
		this._id = id;
	}
	
	public String getName() {
		return this._name;
	}
	
	public void setName(String name) {
		this._name = name;
	}
	
	public String getDescription() {
		return this._description;
	}
	
	public void setDescription(String description) {
		this._description = description;
	}
	
	public int getDifficulty() {
		return this._difficulty;
	}
	
	public void setDifficulty(int difficulty) {
		this._difficulty = difficulty;
	}
	
	public List<Tag> getTags() {
		return this._tags;
	}
	
	public void setTags(List<Tag> tags) {
		this._tags = tags;
	}
	
	public void addTag(Context context,String name) {
		Tags tags = new Tags(context);
		Tag tag = new Tag(name);
		tags.addTagFor(Skill.this, tag);
		_tags.add(tag);
		tags.close();
	}
	
	public void removeTag(Context context, String name) {
		Tags tags = new Tags(context);
		Tag tag = tags.findTagByName(name);
		if (tag != null) {
			tags.removeTagFor(Skill.this, tag);
			_tags.remove(tag);
		}
		tags.close();
	}


}
