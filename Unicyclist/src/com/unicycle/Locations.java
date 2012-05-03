package com.unicycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Locations extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "locations";
	
	//table names
	private static final String TABLE_LOCATIONS = "locations";
	private static final String TABLE_FAVOURITES = "favourites";
	private static final String TABLE_DELETED = "deleted";
	
	//column names
	private static final String KEY_LOCATION_ID = "id";
	private static final String KEY_LOCATION_NAME = "name";
	private static final String KEY_LOCATION_LAT = "latitude";
	private static final String KEY_LOCATION_LONG = "longitude";
	private static final String KEY_LOCATION_DESCRIPTION = "description";
	private static final String KEY_LOCATION_DIRECTIONS = "directions";
	private static final String KEY_LOCATION_RATING = "rating";
	private static final String KEY_LOCATION_MD5SUM = "md5sum";
	private static final String KEY_FAVOURITES_ID = "id";
	private static final String KEY_FAVOURITES_LOCATIONID = "locationId";
	private static final String KEY_DELETED_ID = "id";
	private static final String KEY_DELETED_LOCATIONID = "locationId";
	
	private static List<Integer> favourites = new ArrayList<Integer>();
	private static List<Integer> deleted = new ArrayList<Integer>();
	
	private Context mContext;

    private void loadFavouriteList(SQLiteDatabase db) {
    	String query = "SELECT " + KEY_FAVOURITES_LOCATIONID + " FROM " + TABLE_FAVOURITES;
    	favourites.clear();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
    			favourites.add(Integer.parseInt(cursor.getString(0)));
    		} while (cursor.moveToNext());
    	}
    }
    
    private void loadDeletedList(SQLiteDatabase db) {
    	String query = "SELECT " + KEY_DELETED_LOCATIONID + " FROM " + TABLE_DELETED;
    	deleted.clear();
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor.moveToFirst()) {
    		do {
    			deleted.add(Integer.parseInt(cursor.getString(0)));
    		} while (cursor.moveToNext());
    	}
    }
    
    public Locations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        SQLiteDatabase db = this.getReadableDatabase();
        this.loadFavouriteList(db);
        this.loadDeletedList(db);
        db.close();
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
				+ KEY_LOCATION_ID + " INTEGER PRIMARY KEY," + KEY_LOCATION_NAME + " TEXT,"
				+ KEY_LOCATION_LAT + " INTEGER," + KEY_LOCATION_LONG + " INTEGER,"
				+ KEY_LOCATION_DESCRIPTION + " TEXT," + KEY_LOCATION_DIRECTIONS + " TEXT," 
				+ KEY_LOCATION_RATING + " INTEGER," + KEY_LOCATION_MD5SUM + " TEXT)";
		String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "(" + KEY_FAVOURITES_ID + " INTEGER,"+KEY_FAVOURITES_LOCATIONID + " INTEGER)";
		String CREATE_DELETED_TABLE = "CREATE TABLE " + TABLE_DELETED + "(" + KEY_DELETED_ID + " INTEGER,"+KEY_DELETED_LOCATIONID + " INTEGER)";
		db.execSQL(CREATE_LOCATIONS_TABLE);
		db.execSQL(CREATE_FAVOURITES_TABLE);
		db.execSQL(CREATE_DELETED_TABLE);
	}
	
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	switch (oldVersion) {
    	case 3:
    		db.execSQL("ALTER TABLE " + TABLE_LOCATIONS + " ADD COLUMN " + KEY_LOCATION_MD5SUM + " TEXT");
    		//no break here
    	}
    }
    
	public String databaseName() {
		return DATABASE_NAME;
	}
    
    private void addFavourite(int id) {
    	if (! favourites.contains(id)) {
    		favourites.add(id);
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	String query = "SELECT " + KEY_FAVOURITES_LOCATIONID + " FROM " + TABLE_FAVOURITES 
	    			+ " WHERE " + KEY_FAVOURITES_LOCATIONID + "="+Integer.toString(id);
	    	Cursor cursor = db.rawQuery(query,null);
	    	if (! cursor.moveToFirst()) {
	    		ContentValues values = new ContentValues();
	    		values.put(KEY_FAVOURITES_LOCATIONID, id);
	    		db.insert(TABLE_FAVOURITES, null, values);
	    	}
	    	cursor.close();
	    	db.close();
    	}
    }
    
    private void clearFavourite(int id) {
    	if (favourites.contains(id)) {
    		favourites.remove(favourites.indexOf(id));
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	String query = "DELETE FROM " + TABLE_FAVOURITES 
	    			+ " WHERE " + KEY_FAVOURITES_LOCATIONID + "="+Integer.toString(id);
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
    
    //Public Methods
        
    public int addLocation(Location location) {
    	SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, location.getName()); 
        values.put(KEY_LOCATION_LAT, (int) Math.round(location.getLatitude() * 1e6));
        values.put(KEY_LOCATION_LONG, (int) Math.round(location.getLongitude() * 1e6));
        values.put(KEY_LOCATION_DESCRIPTION, location.getDescription());
        values.put(KEY_LOCATION_DIRECTIONS, location.getDirections());
        values.put(KEY_LOCATION_RATING, location.getRating());
        values.put(KEY_LOCATION_MD5SUM, location.getMd5sum());
        int id = (int) db.insert(TABLE_LOCATIONS, null, values);
        if (location.isFavourite()) {
        	this.addFavourite(id);
        }
        db.close();
        
        return id;
    }
    
    public Location getLocation(int id) {
    	Location result = null;
        Tags tags = new Tags(mContext);
        Comments comments = new Comments(mContext);
        Images images = new Images(mContext);
        Features features = new Features(mContext);
    	if ( ! this.isDeleted(id)) {
	    	String query = "SELECT " + KEY_LOCATION_ID + "," + KEY_LOCATION_NAME + ","
			    			+ KEY_LOCATION_LAT + "," + KEY_LOCATION_LONG + ","
			    			+ KEY_LOCATION_DESCRIPTION + "," + KEY_LOCATION_DIRECTIONS + "," + KEY_LOCATION_RATING + ","
			    			+ KEY_LOCATION_MD5SUM
	    			+ " FROM " + TABLE_LOCATIONS
	    			+ " WHERE " + KEY_LOCATION_ID + " = " + Integer.toString(id) + " LIMIT 1";
	    	SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(query, null);
	        if (cursor.moveToFirst()) {
		        Location location = new Location(Integer.parseInt(cursor.getString(0)),
		                cursor.getString(1), Integer.parseInt(cursor.getString(2))/1e6, Integer.parseInt(cursor.getString(3))/1e6,
		                cursor.getString(4), cursor.getString(5), 
		                cursor.getInt(6), cursor.getString(7));
		        if (this.isFavourite(location.getId())) {
		        	location.setFavourite();
		        }
		        location.setImages(images.getImagesFor(location));
		        location.setTags(tags.getTagsFor(location));
		        location.setComments(comments.getCommentsFor(location));
		        location.setFeatures(features.getFeaturesFor(location));
		        result = location;
	        } 
	        db.close();
    	}
    	return result;    
    }
    
    public List<Location> getAllLocations() {
	    List<Location> locationList = new ArrayList<Location>();
	    String selectQuery = "SELECT " + KEY_LOCATION_ID + "   FROM " + TABLE_LOCATIONS;
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	int id = Integer.parseInt(cursor.getString(0));
	        	if (! this.isDeleted(id)) {
		            Location location = this.getLocation(id);
		            if (this.isFavourite(location.getId())) {
		            	location.setFavourite();
		            }
		            locationList.add(location);
	        	}
	        } while (cursor.moveToNext());
	    }
	    db.close();
	    return locationList;
    }

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
        values.put(KEY_LOCATION_MD5SUM, location.getMd5sum());
        int result = db.update(TABLE_LOCATIONS, values, KEY_LOCATION_ID + " = ?", new String[] { String.valueOf(id) });
        
        if (location.isFavourite()) {
        	this.addFavourite(id);
        } else {
        	this.clearFavourite(id);
        }
        if (location.isDeleted()) {
        	this.deleteLocation(id);
        }
        Tags tags = new Tags(mContext);
        tags.removeTagsFor(location);
        tags.addTags(location, location.getTags());
//TODO: add comments and images and features here too??        
        db.close();
        return result;
    }
    
    
    public void deleteLocation(int id) {
    	if (! isDeleted(id)) {
    		//delete associated data
//    		Location location = this.getLocation(id);
//    		Tags tags = new Tags(mContext);
//    		tags.removeTagsFor(location);
//    		Comments comments = new Comments(mContext);
//    		comments.removeCommentsFor(location);
//    		Images images = new Images(mContext);
//    		images.removeImagesFor(location);
//    		Features features = new Features(mContext);
//    		features.removeFeaturesFor(location);
    		//mark location record as deleted
        	SQLiteDatabase db = this.getWritableDatabase();
        	ContentValues values = new ContentValues();
        	values.put(KEY_DELETED_LOCATIONID, id);
        	db.insert(TABLE_DELETED, null, values);
        	db.close();    		
    	}
    }
    
}
