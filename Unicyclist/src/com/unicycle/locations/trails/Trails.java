package com.unicycle.locations.trails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.unicycle.comments.Comments;
import com.unicycle.images.Images;
import com.unicycle.locations.Location;
import com.unicycle.locations.features.Features;
import com.unicycle.tags.Tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Trails extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "trails";
	
	//table names
	private static final String TABLE_TRAILS = "trails";
	
	//column names
	private static final String KEY_TRAIL_ID = "id";
	private static final String KEY_TRAIL_LOCATIONID = "locationId";
	private static final String KEY_TRAIL_NAME = "name";
	private static final String KEY_TRAIL_LAT = "latitude";
	private static final String KEY_TRAIL_LON = "longitude";
	private static final String KEY_TRAIL_DESCRIPTION = "description";
	private static final String KEY_TRAIL_DIRECTIONS = "directions";
	private static final String KEY_TRAIL_LENGTH = "length";
	private static final String KEY_TRAIL_RATING = "rating";
	private static final String KEY_TRAIL_DIFFICULTY = "difficulty";

	private Context mContext;
	
    public Trails(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TRAILS_TABLE = "CREATE TABLE " + TABLE_TRAILS + "("
				+ KEY_TRAIL_ID + " INTEGER PRIMARY KEY, " + KEY_TRAIL_LOCATIONID + " INTEGER, "
				+ KEY_TRAIL_NAME + " TEXT, " + KEY_TRAIL_LAT + " INTEGER, " + KEY_TRAIL_LON + " INTEGER, "
				+ KEY_TRAIL_DESCRIPTION + " TEXT, " + KEY_TRAIL_DIRECTIONS + " TEXT, " 
				+ KEY_TRAIL_LENGTH + " INTEGER, " + KEY_TRAIL_RATING + " INTEGER, " 
				+ KEY_TRAIL_DIFFICULTY + " INTEGER)";
		db.execSQL(CREATE_TRAILS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAILS);
		onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public int addTrail(Trail trail) {
    	SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_TRAIL_LOCATIONID, trail.getLocationId());
        values.put(KEY_TRAIL_NAME, trail.getName()); 
        values.put(KEY_TRAIL_LAT, (int) Math.round(trail.getLatitude() * 1e6));
        values.put(KEY_TRAIL_LON, (int) Math.round(trail.getLongitude() * 1e6));
        values.put(KEY_TRAIL_DESCRIPTION, trail.getDescription());
        values.put(KEY_TRAIL_DIRECTIONS, trail.getDirections());
        values.put(KEY_TRAIL_LENGTH, trail.getLength());
        values.put(KEY_TRAIL_RATING, trail.getRating());
        values.put(KEY_TRAIL_DIFFICULTY, trail.getDifficulty());
        int id = (int) db.insert(TABLE_TRAILS, null, values);
        db.close();
        return id;
    }
	
	public int updateTrail(Trail trail) {
    	int id = trail.getId();
    	SQLiteDatabase db = this.getWritableDatabase(); 
        ContentValues values = new ContentValues();
        values.put(KEY_TRAIL_LOCATIONID, trail.getLocationId());
        values.put(KEY_TRAIL_NAME, trail.getName()); 
        values.put(KEY_TRAIL_LAT, (int) Math.round(trail.getLatitude() * 1e6));
        values.put(KEY_TRAIL_LON, (int) Math.round(trail.getLongitude() * 1e6));
        values.put(KEY_TRAIL_DESCRIPTION, trail.getDescription());
        values.put(KEY_TRAIL_DIRECTIONS, trail.getDirections());
        values.put(KEY_TRAIL_LENGTH, trail.getLength());
        values.put(KEY_TRAIL_RATING, trail.getRating());
        values.put(KEY_TRAIL_DIFFICULTY, trail.getDifficulty());
        int result = db.update(TABLE_TRAILS, values, KEY_TRAIL_ID + " = ?", new String[] { String.valueOf(id) });
        Tags tags = new Tags(mContext);
        tags.removeTagsFor(trail);
        tags.addTags(trail, trail.getTags());
//TODO: add comments and images and tracks and features here too??        
        db.close();
        return result;
	}
	
	public Trail getTrail(int id) {
		Trail result = null;
    	String query = "SELECT " + KEY_TRAIL_ID + "," + KEY_TRAIL_LOCATIONID + ","
    			+ KEY_TRAIL_NAME + "," + KEY_TRAIL_LAT + "," + KEY_TRAIL_LON + ","
    			+ KEY_TRAIL_DESCRIPTION + "," + KEY_TRAIL_DIRECTIONS + "," + KEY_TRAIL_LENGTH + ","
    			+ KEY_TRAIL_RATING + "," + KEY_TRAIL_DIFFICULTY
    			+ " FROM " + TABLE_TRAILS
    			+ " WHERE " + KEY_TRAIL_ID + " = " + Integer.toString(id) + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
		    Trail trail = new Trail(Integer.parseInt(cursor.getString(0)),
		    		Integer.parseInt(cursor.getString(1)),
		            cursor.getString(2), Integer.parseInt(cursor.getString(3))/1e6, Integer.parseInt(cursor.getString(4))/1e6,
		            cursor.getString(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)), 
		            Integer.parseInt(cursor.getString(8)), Integer.parseInt(cursor.getString(9)));
		    Images images = new Images(mContext);
	        trail.setImages(images.getImagesFor(trail));
	        Tags tags = new Tags(mContext);
	        trail.setTags(tags.getTagsFor(trail));
	        Comments comments = new Comments(mContext);
	        trail.setComments(comments.getCommentsFor(trail));
	        Features features = new Features(mContext);
	        trail.setFeatures(features.getFeaturesFor(trail));
//TODO: uncomment this after GPSTracks are set up	        
//		    GPSTracks tracks = new GPSTracks(mContext);
//		    trail.setTracks(tracks.getTracksFor(trail));
		    result = trail;
		} 
		db.close();
		return result;    
	}

	public List<Trail> getAllTrailsForLocation(Location location) {
		List<Trail> trailList = new ArrayList<Trail>();
		String query = "SELECT " + KEY_TRAIL_ID 
				+ " FROM " + TABLE_TRAILS
				+ " WHERE " + KEY_TRAIL_LOCATIONID + " = " + Integer.toString(location.getId());
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null); 
		if (cursor.moveToFirst()) {
			do {
				Trail trail = getTrail(Integer.parseInt(cursor.getString(0)));
				trailList.add(trail);
			} while (cursor.moveToNext());
		}
		db.close();
		return trailList;
	}

}
