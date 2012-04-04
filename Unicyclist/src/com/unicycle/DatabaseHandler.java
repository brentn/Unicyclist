package com.unicycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "locations";
	
	//table names
	private static final String TABLE_TAGS = "tags";
	private static final String TABLE_LOCATIONS = "locations";
	private static final String TABLE_FAVOURITES = "favourites";
	private static final String TABLE_DELETED = "deleted";
	private static final String TABLE_LOCATION_TAGS = "location_tags";
	
	//column names
	private static final String KEY_TAG_ID = "id";
	private static final String KEY_TAG_NAME = "name";
	private static final String KEY_LOCATION_ID = "id";
	private static final String KEY_LOCATION_NAME = "name";
	private static final String KEY_LOCATION_LAT = "latitude";
	private static final String KEY_LOCATION_LONG = "longitude";
	private static final String KEY_LOCATION_DESCRIPTION = "description";
	private static final String KEY_LOCATION_DIRECTIONS = "directions";
	private static final String KEY_LOCATION_RATING = "rating";
	private static final String KEY_FAVOURITES_ID = "id";
	private static final String KEY_FAVOURITES_LOCATIONID = "locationId";
	private static final String KEY_DELETED_ID = "id";
	private static final String KEY_DELETED_LOCATIONID = "locationId";
	private static final String KEY_LOCATION_TAG_LOCATIONID = "locationId";
	private static final String KEY_LOCATION_TAG_TAGID = "tagId";
	
	private static List<Integer> favourites = new ArrayList<Integer>();
	private static List<Integer> deleted = new ArrayList<Integer>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_TAGS + "("
				+ KEY_TAG_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT)";
		String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
				+ KEY_LOCATION_ID + " INTEGER PRIMARY KEY," + KEY_LOCATION_NAME + " TEXT,"
				+ KEY_LOCATION_LAT + " INTEGER," + KEY_LOCATION_LONG + " INTEGER,"
				+ KEY_LOCATION_DESCRIPTION + " TEXT," + KEY_LOCATION_DIRECTIONS + " TEXT," 
				+ KEY_LOCATION_RATING + " INTEGER" + ")";
		String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "(" + KEY_FAVOURITES_ID + " INTEGER,"+KEY_FAVOURITES_LOCATIONID + " INTEGER)";
		String CREATE_DELETED_TABLE = "CREATE TABLE " + TABLE_DELETED + "(" + KEY_DELETED_ID + " INTEGER,"+KEY_DELETED_LOCATIONID + " INTEGER)";
		String CREATE_LOCATION_TAGS_TABLE = "CREATE TABLE " + TABLE_LOCATION_TAGS + "("
				+ KEY_LOCATION_TAG_LOCATIONID + " INTEGER," + KEY_LOCATION_TAG_TAGID + " INTEGER)";
		db.execSQL(CREATE_TAGS_TABLE);
		db.execSQL(CREATE_LOCATIONS_TABLE);
		db.execSQL(CREATE_FAVOURITES_TABLE);
		db.execSQL(CREATE_DELETED_TABLE);
		db.execSQL(CREATE_LOCATION_TAGS_TABLE);
	}
	
	public void initialize() {
		SQLiteDatabase db = this.getReadableDatabase();
		this.loadFavouriteList(db);
		this.loadDeletedList(db);		
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_TAGS);
 
        // Create tables again
        onCreate(db);
    }
    
    public void backup() {
    	final String DB_FILEPATH = "/data/data/{package_name}/databases/database.db";
    	final String DB_BACKUP = "{package_name}.db";
    	
    	close();
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File currentDB = new File(data, DB_FILEPATH);
                File backupDB = new File(sd, DB_BACKUP);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }
    
    public boolean restore() {
    	final String DB_FILEPATH = "/data/data/{package_name}/databases/database.db";
    	final String DB_BACKUP = "{package_name}.db";

    	    // Close the SQLiteOpenHelper so it will commit the created empty
    	    // database to internal storage.
    	    close();
    	    try {
	    	    File newDb = new File(DB_BACKUP);
	    	    File oldDb = new File(DB_FILEPATH);
	    	    if (newDb.exists()) {
	    	        FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
	    	        // Access the copied database so SQLiteHelper will cache it and mark
	    	        // it as created.
	    	        getWritableDatabase().close();
	    	        return true;
	    	    }
	    	    return false;
    	    } catch (Exception e) {
    	    	return false;
    	    }
    }
    
    
    private void loadFavouriteList(SQLiteDatabase db) {
    	String query = "SELECT " + KEY_FAVOURITES_LOCATIONID + " FROM " + TABLE_FAVOURITES;
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
    			favourites.add(Integer.parseInt(cursor.getString(0)));
    		} while (cursor.moveToNext());
    	}
    }
    
    private void loadDeletedList(SQLiteDatabase db) {
    	String query = "SELECT " + KEY_DELETED_LOCATIONID + " FROM " + TABLE_DELETED;
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
    			deleted.add(Integer.parseInt(cursor.getString(0)));
    		} while (cursor.moveToNext());
    	}
    }
    
    private void addFavourite(int id) {
    	if (! favourites.contains(id)) {
    		favourites.add(id);
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	String query = "SELECT " + KEY_FAVOURITES_LOCATIONID + " FROM " + TABLE_FAVOURITES + " WHERE " + KEY_FAVOURITES_LOCATIONID + "="+id;
	    	Cursor cursor = db.rawQuery(query,null);
	    	if (! cursor.moveToFirst()) {
	    		ContentValues values = new ContentValues();
	    		values.put(KEY_FAVOURITES_LOCATIONID, id);
	    		db.insert(TABLE_FAVOURITES, null, values);
	    	}
	    	db.close();
    	}
    }
    
    private void clearFavourite(int id) {
    	if (favourites.contains(id)) {
    		favourites.remove(id);
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	String query = "DELETE FROM " + TABLE_FAVOURITES + " WHERE " + KEY_FAVOURITES_LOCATIONID + "="+id;
	    	db.execSQL(query);
	    	db.close();
    	}
    }
    
    private boolean isFavourite(int id) {
    	return favourites.contains(id);
    }
    
    private boolean isDeleted(int id) {
    	return deleted.contains(id);
    }
    
    private int addNewTag(Tag tag) { //returns id if exists, otherwise adds a new entry and returns its id
    	Tag existingTag = this.findTagByName(tag.getName());
    	if (existingTag == null) { 
	    	SQLiteDatabase db = this.getWritableDatabase(); 	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_TAG_NAME, tag.getName());  
	        
	        int id = (int) db.insert(TABLE_TAGS, null, values);
	        db.close();
	        return id;
    	} else {
    		return existingTag.getId();
    	}
    }
   
    private void addTags(Integer locationId, List<Tag> tags) {
    	Iterator<Tag> i = tags.iterator();
    	while (i.hasNext()) {
    		this.addTag(locationId, (Tag) i.next());
    		i.remove();
    	}
    }
    
    private void addTag(Integer locationId, Tag tag) {
    	int tagId = addNewTag(tag);
    	// check for duplicates
    	String query = "SELECT * FROM " + TABLE_LOCATION_TAGS + " WHERE " + KEY_LOCATION_TAG_TAGID + " = " + tagId + " AND " + KEY_LOCATION_TAG_LOCATIONID + " = " + locationId;
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor cursor = db.rawQuery(query, null);
    	if (! cursor.moveToFirst()) {
    		ContentValues values = new ContentValues();
    		values.put(KEY_LOCATION_TAG_LOCATIONID, locationId);
    		values.put(KEY_LOCATION_TAG_TAGID, tagId);
    	
    		db.insert(TABLE_LOCATION_TAGS,  null, values);
    	}
    	db.close();
    }
    
    //Public Methods
        
    public void addLocation(Location location) {
    	SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, location.getName()); 
        values.put(KEY_LOCATION_LAT, (int) Math.round(location.getLatitude() * 1e6));
        values.put(KEY_LOCATION_LONG, (int) Math.round(location.getLongitude() * 1e6));
        values.put(KEY_LOCATION_DESCRIPTION, location.getDescription());
        values.put(KEY_LOCATION_DIRECTIONS, location.getDirections());
        values.put(KEY_LOCATION_RATING, location.getRating());
        int id = (int) db.insert(TABLE_LOCATIONS, null, values);
        db.close();
        if (location.isFavourite()) {
        	this.addFavourite(id);
        }
        this.addTags(id, location.getTags());
    }
    
    public Tag findTagByName(String name) {
    	name = name.toLowerCase().trim();
    	String selectQuery = "SELECT * FROM " + TABLE_TAGS + " WHERE " + KEY_TAG_NAME + " = '" + name+"'";
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	if (cursor.moveToFirst()) {
    		Tag tag = new Tag();
    		tag.setId(Integer.parseInt(cursor.getString(0)));
    		tag.setName(cursor.getString(1));
    		db.close();
    		return tag;
    	} else {
    		db.close();
    		return null;
    	}
    }
    
    public List<Tag> getTagsForLocation(Location location) {
    	List<Tag> tagList = new ArrayList<Tag>();
    	String selectQuery = "SELECT t." + KEY_TAG_ID + ", t." + KEY_TAG_NAME 
    			+ " FROM " + TABLE_LOCATION_TAGS + " lt"
   			+ " LEFT OUTER JOIN " + TABLE_TAGS + " t ON t." + KEY_TAG_ID + " = lt." + KEY_LOCATION_TAG_TAGID + " WHERE lt." + KEY_LOCATION_TAG_LOCATIONID + " = " + location.getId();
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
    	if (cursor.moveToFirst()) {
    		do {
    			Tag tag = new Tag();
    			tag.setId(Integer.parseInt(cursor.getString(0)));
    			tag.setName(cursor.getString(1));
    			tagList.add(tag);
    		} while (cursor.moveToNext());
    	}
    	db.close();
    	return tagList;
    }

    public Location getLocation(int id) {
    	if ( ! this.isDeleted(id)) {
	    	String query = "SELECT " + KEY_LOCATION_ID + "," + KEY_LOCATION_NAME + ","
			    			+ KEY_LOCATION_LAT + "," + KEY_LOCATION_LONG + ","
			    			+ KEY_LOCATION_DESCRIPTION + "," + KEY_LOCATION_DIRECTIONS + "," + KEY_LOCATION_RATING
	    			+ " FROM " + TABLE_LOCATIONS
	    			+ " WHERE " + KEY_LOCATION_ID + " = " + id;
	    	SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(query, null);
	        if (cursor.moveToFirst()) {
		        Location location = new Location(Integer.parseInt(cursor.getString(0)),
		                cursor.getString(1), Integer.parseInt(cursor.getString(2))/1e6, Integer.parseInt(cursor.getString(3))/1e6,
		                cursor.getString(4), cursor.getString(5), 
		                Integer.parseInt(cursor.getString(6)));
		        location.setTags(this.getTagsForLocation(location));
		        db.close();
		        if (this.isFavourite(location.getId())) {
		        	location.setFavourite();
		        }
		        return location;
	        } 
	        db.close();
    	}
    	return null;    
    }
    
    public List<Location> getAllLocations() {
	    List<Location> locationList = new ArrayList<Location>();
	    String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    if (cursor.moveToFirst()) {
	        do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	if (! this.isDeleted(id)) {
		            Location location = new Location();
		            location.setId(id);
		            location.setName(cursor.getString(1));
		            location.setCoordinates(Integer.parseInt(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
		            location.setDescription(cursor.getString(4));
		            location.setDirections(cursor.getString(5));
		            location.setRating(Integer.parseInt(cursor.getString(6)));
		            if (this.isFavourite(location.getId())) {
		            	location.setFavourite();
		            }
		            location.setTags(this.getTagsForLocation(location));
		            locationList.add(location);
	        	}
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return locationList;
    }
    
//    public List<Location> getMyLocations() {
//    	List<Location> myLocationList = new ArrayList<Location>();
//    	String query = "SELECT * FROM " + TABLE_FAVOURITES;
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	Cursor cursor = db.rawQuery(query,null);
//    	if (cursor.moveToFirst()) {
//    		do {
//	            Location location = new Location();
//    			location = this.getLocation(Integer.parseInt(cursor.getString(0)));
//	            myLocationList.add(location);
//    		} while (cursor.moveToNext());
//    	}
//    	return myLocationList;
//    }
    
    public int updateLocation(Location location) {
    	int id = location.getId();
    	SQLiteDatabase db = this.getWritableDatabase(); 
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, location.getName()); 
        values.put(KEY_LOCATION_LAT, (int) Math.round(location.getLatitude() * 1e6));
        values.put(KEY_LOCATION_LONG, (int) Math.round(location.getLongitude() * 1e6));
        values.put(KEY_LOCATION_DESCRIPTION, location.getDescription());
        values.put(KEY_LOCATION_DIRECTIONS, location.getDirections());
        values.put(KEY_LOCATION_RATING, location.getRating());
        int result = db.update(TABLE_LOCATIONS, values, KEY_LOCATION_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
        
        if (isFavourite(id)) {
        	this.addFavourite(id);
        } else {
        	this.clearFavourite(id);
        }
        if (isDeleted(id)) {
        	this.deleteLocation(id);
        }
        return result;
    }
    
    public void deleteTag(int id) {
    	//only delete if not referenced in location_tag table
    	String query = "SELECT * FROM " + TABLE_LOCATION_TAGS + " WHERE " + KEY_LOCATION_TAG_TAGID + " = " + id;
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	if (db.rawQuery(query, null).isNull(0)) {
    		db.delete(TABLE_TAGS, KEY_TAG_ID + " =?", new String[] {String.valueOf(id)});
    	}
    	db.close();
    }
    
    public void deleteLocation(int id) {
    	if (! isDeleted(id)) {
        	SQLiteDatabase db = this.getWritableDatabase();
        	ContentValues values = new ContentValues();
        	values.put(KEY_DELETED_ID, id);
        	db.insert(TABLE_DELETED, null, values);
        	db.close();    		
    	}
    }
    
}
