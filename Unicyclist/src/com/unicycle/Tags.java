package com.unicycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Tags extends SQLiteOpenHelper {

	public static final int SORT_BY_NAME = 1;
	public static final int SORT_BY_USAGE = 2;

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "tags";

	//table names
	private static final String TABLE_TAGS = "tags";
	private static final String TABLE_LOCATION_TAGS = "locationTags";
	private static final String TABLE_TRAIL_TAGS = "trailTags";

	//column names
	private static final String KEY_TAG_ID = "id";
	private static final String KEY_TAG_NAME = "name";
	private static final String KEY_TAG_USAGE = "usage";
	
	private static final String KEY_LOCATION_TAG_ID = "id";
	private static final String KEY_LOCATION_TAG_TAGID = "tagId";
	private static final String KEY_LOCATION_TAG_LOCATIONID = "locationId";
	
	private static final String KEY_TRAIL_TAG_ID = "id";
	private static final String KEY_TRAIL_TAG_TAGID = "tagId";
	private static final String KEY_TRAIL_TAG_TRAILID = "trailId";

	public Tags(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_TAGS + "("
				+ KEY_TAG_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT," + KEY_TAG_USAGE + " INTEGER)";
		String CREATE_LOCATION_TAGS_TABLE = "CREATE TABLE " + TABLE_LOCATION_TAGS + "("
				+ KEY_LOCATION_TAG_ID + " INTEGER PRIMARY KEY, "+ KEY_LOCATION_TAG_LOCATIONID + " INTEGER, " + KEY_LOCATION_TAG_TAGID + " INTEGER)";
		String CREATE_TRAIL_TAGS_TABLE = "CREATE TABLE " + TABLE_TRAIL_TAGS + "("
				+ KEY_TRAIL_TAG_ID + " INTEGER PRIMARY KEY, "+ KEY_TRAIL_TAG_TRAILID + " INTEGER, " + KEY_TRAIL_TAG_TAGID + " INTEGER)";
		db.execSQL(CREATE_TAGS_TABLE);
		db.execSQL(CREATE_LOCATION_TAGS_TABLE);
		db.execSQL(CREATE_TRAIL_TAGS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAIL_TAGS);
        onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public int addTag(Tag tag) {
		//Only if it doesn't exist.  Otherwise return ID of existing tag.
		Tag target = findTagByName(tag.getName());
		if ( target != null ) {
			return target.getId();
		} else {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
	        values.put(KEY_TAG_NAME, tag.getName());  
	        values.put(KEY_TAG_USAGE, tag.getUsage());
	        int id = (int) db.insert(TABLE_TAGS, null, values);
	        db.close();
	        return id;
		}
	}
	
	public int addLocationTag(Location location, Tag tag) {
		//ensure locationId has been set
		if (location.getId() == -1) {
			return -1;
		}
		//If it is not a duplicate
		int id = findLocationTag(location,tag);
		if (id == -1) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_LOCATION_TAG_LOCATIONID,location.getId());
			values.put(KEY_LOCATION_TAG_TAGID, tag.getId());
			id = (int) db.insert(TABLE_LOCATION_TAGS, null, values);
			db.close();
			//record another use of this tag
			tag.setUsage(tag.getUsage()+1);
			this.updateTag(tag);
		}
		return id;
	}
	
    public void addLocationTags(Location location, List<Tag> tags) {
    	Iterator<Tag> i = tags.iterator();
    	while (i.hasNext()) {
    		this.addLocationTag(location, (Tag) i.next());
    		i.remove();
    	}
    }    
	
	public int addTrailTag(Trail trail, Tag tag) {
		//ensure locationId has been set
		if (trail.getId() == -1) {
			return -1;
		}
		//If it is not a duplicate
		int id = findTrailTag(trail,tag);
		if (id == -1) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_TRAIL_TAG_TRAILID,trail.getId());
			values.put(KEY_TRAIL_TAG_TAGID, tag.getId());
			id = (int) db.insert(TABLE_TRAIL_TAGS, null, values);
			db.close();
			//record another use of this tag
			tag.setUsage(tag.getUsage()+1);
			this.updateTag(tag);
		}
		return id;
	}
	
    public void addTrailTags(Trail trail, List<Tag> tags) {
    	Iterator<Tag> i = tags.iterator();
    	while (i.hasNext()) {
    		this.addTrailTag(trail, (Tag) i.next());
    		i.remove();
    	}
    }    
	
	public Tag findTagByName(String name) {
		Tag tag = null;
		String query = "SELECT * FROM " + TABLE_TAGS + " WHERE " + KEY_TAG_NAME + " =  '" + name + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			tag = getTag(Integer.parseInt(cursor.getString(0)));
		}
		db.close();
		return tag;
	}
	
	public int findLocationTag(Location location, Tag tag) {
		int result = -1;
		String query = "SELECT * FROM " + TABLE_LOCATION_TAGS 
				+ " WHERE " + KEY_LOCATION_TAG_LOCATIONID + " = " + Integer.toString(location.getId())
				+ " AND " + KEY_LOCATION_TAG_TAGID + " = " + Integer.toString(tag.getId());
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = Integer.parseInt(cursor.getString(0));
		}
		db.close();
		return result;
	}
	
	public int findTrailTag(Trail trail, Tag tag) {
		int result = -1;
		String query = "SELECT * FROM " + TABLE_TRAIL_TAGS 
				+ " WHERE " + KEY_TRAIL_TAG_TRAILID + " = " + Integer.toString(trail.getId())
				+ " AND " + KEY_TRAIL_TAG_TAGID + " = " + Integer.toString(tag.getId());
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = Integer.parseInt(cursor.getString(0));
		}
		db.close();
		return result;
	}
	
	public Tag getTag(int id) {
		Tag tag = null;
		String query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE 
				+ " FROM " + TABLE_TAGS + " WHERE " + KEY_TAG_ID + " = " + Integer.toString(id);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			tag = new Tag();
			tag.setId(Integer.parseInt(cursor.getString(0)));
			tag.setName(cursor.getString(1));
			tag.setUsage(Integer.parseInt(cursor.getString(2)));
		}
		db.close();
		return tag;
	}
	
    public List<Tag> getAllTags(int sortBy) {
	    List<Tag> tagList = new ArrayList<Tag>();
	    String query;
	    switch (sortBy) {
	    case SORT_BY_NAME: 
	    	query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
	    	+ " FROM " + TABLE_TAGS + " ORDER BY " + KEY_TAG_NAME;
	    	break;
	    case SORT_BY_USAGE:
	    	query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
	    	+ " FROM " + TABLE_TAGS + " ORDER BY " + KEY_TAG_USAGE + " DESC";
	    	break;
    	default:
    		query = "SELECT " + KEY_TAG_ID + ", " + KEY_TAG_NAME + ", " + KEY_TAG_USAGE
    		+ " FROM " + TABLE_TAGS;
    		break;
	    };
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(query, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	Tag tag = new Tag();
	        	tag.setId(id);
	        	tag.setName(cursor.getString(1));
	        	tag.setUsage(Integer.parseInt(cursor.getString(2)));
	        	tagList.add(tag);
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return tagList;
    }
    
    public List<Tag> getTagsForLocation(Location location) {
    	List<Tag> tagList = new ArrayList<Tag>();
    	String query = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME + ", t." + KEY_TAG_USAGE
    			+ " FROM " + TABLE_TAGS + " t"
    			+ " INNER JOIN " + TABLE_LOCATION_TAGS + " lt ON t." + KEY_TAG_ID + " = lt." + KEY_LOCATION_TAG_TAGID 
    			+ " WHERE lt." + KEY_LOCATION_TAG_LOCATIONID + " = " + Integer.toString(location.getId())
    			+ " ORDER BY t." + KEY_TAG_USAGE + " DESC";
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	Tag tag = new Tag();
	        	tag.setId(id);
	        	tag.setName(cursor.getString(1));
	        	tag.setUsage(Integer.parseInt(cursor.getString(2)));
	        	tagList.add(tag);    			
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return tagList;
    }
    
    public List<Tag> getTagsForTrail(Trail trail) {
    	List<Tag> tagList = new ArrayList<Tag>();
    	String query = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME + ", t." + KEY_TAG_USAGE
    			+ " FROM " + TABLE_TAGS + " t"
    			+ " INNER JOIN " + TABLE_TRAIL_TAGS + " tt ON t." + KEY_TAG_ID + " = tt." + KEY_TRAIL_TAG_TAGID 
    			+ " WHERE tt." + KEY_TRAIL_TAG_TRAILID + " = " + Integer.toString(trail.getId())
    			+ " ORDER BY t." + KEY_TAG_USAGE + " DESC";
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	Tag tag = new Tag();
	        	tag.setId(id);
	        	tag.setName(cursor.getString(1));
	        	tag.setUsage(Integer.parseInt(cursor.getString(2)));
	        	tagList.add(tag);    			
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return tagList;
    }
    
    public List<Integer> getLocationIdsForTag(Tag tag) {
    	List<Integer> locationList = new ArrayList<Integer>();
    	String query = "SELECT " + KEY_LOCATION_TAG_LOCATIONID 
    			+ " FROM " + TABLE_LOCATION_TAGS
    			+ " WHERE " + KEY_LOCATION_TAG_TAGID + " = " + Integer.toString(tag.getId());
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
    			locationList.add(Integer.parseInt(cursor.getString(0)));
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return locationList;
    }
    
    public int updateTag(Tag tag) {
    	int id = tag.getId();
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(KEY_TAG_NAME, tag.getName());
    	values.put(KEY_TAG_USAGE, tag.getUsage());
    	int result = db.update(TABLE_TAGS, values, KEY_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    	db.close();
    	return result;
    }
    
    public void removeLocationTag(Location location, Tag tag) {
    	int id = findLocationTag(location, tag);
    	if ( id >= 0 ) {
    		SQLiteDatabase db = this.getWritableDatabase();
    		db.delete(TABLE_LOCATION_TAGS, KEY_LOCATION_TAG_ID + " = ?", new String[] {String.valueOf(id) });
    		db.close();
    		//reduce usage count for tag
    		tag.setUsage(tag.getUsage()-1);
    		updateTag(tag);
    	}
    }
    
    public void removeTagsForLocation(Location location) {
    	List<Tag> tagList = location.getTags();
        for(Iterator<Tag> i = tagList.iterator(); i.hasNext(); ) {
      	  Tag tag = i.next();
      	  removeLocationTag(location,tag);
        }
    }
    	
    
}
