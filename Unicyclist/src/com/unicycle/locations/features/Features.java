package com.unicycle.locations.features;

import java.util.ArrayList;
import java.util.List;

import com.unicycle.comments.Comments;
import com.unicycle.images.Images;
import com.unicycle.locations.Location;
import com.unicycle.locations.trails.Trail;
import com.unicycle.tags.Tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Features extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "features";
	
	//table names
	private static final String TABLE_FEATURES = "features";
	
	//column names
	private static final String KEY_FEATURE_ID = "id";
	private static final String KEY_FEATURE_LOCATIONID = "locationId";
	private static final String KEY_FEATURE_TRAILID = "trailId";
	private static final String KEY_FEATURE_NAME = "name";
	private static final String KEY_FEATURE_LAT = "latitude";
	private static final String KEY_FEATURE_LON = "longitude";
	private static final String KEY_FEATURE_DESCRIPTION = "description";
	private static final String KEY_FEATURE_DIRECTIONS = "directions";
	private static final String KEY_FEATURE_DIFFICULTY = "difficulty";
	
	private Context mContext;
	
    public Features(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FEATURES_TABLE = "CREATE TABLE " + TABLE_FEATURES + " ("
				+ KEY_FEATURE_ID + " INTEGER PRIMARY KEY, " + KEY_FEATURE_LOCATIONID + " INTEGER, " + KEY_FEATURE_TRAILID + " INTEGER, "
				+ KEY_FEATURE_NAME + " TEXT, " + KEY_FEATURE_LAT + " INTEGER, " + KEY_FEATURE_LON + " INTEGER, "
				+ KEY_FEATURE_DESCRIPTION + " TEXT, " + KEY_FEATURE_DIRECTIONS + " TEXT " + KEY_FEATURE_DIFFICULTY + " INTEGER)";
		db.execSQL(CREATE_FEATURES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURES);
		onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public int find(Feature feature) {
		int result = -1;
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT " + KEY_FEATURE_ID + " FROM " + TABLE_FEATURES 
				+ " WHERE " + KEY_FEATURE_NAME + " = " + feature.getName()
				+ " AND " + KEY_FEATURE_LAT + " = " + feature.getLatitude()
				+ " AND " + KEY_FEATURE_LON + " = " + feature.getLongitude() + " LIMIT 1";
		Cursor cursor = db.rawQuery(query,null);
		if (cursor.moveToFirst()) {
			result = cursor.getInt(0);
		}
		db.close();
		return result;
	}
	
	public boolean exists(Feature feature) {
		return (find(feature) != -1);
	}
	
	public int addFeature(Feature feature) {
    	SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_FEATURE_LOCATIONID, feature.getLocationId());
        values.put(KEY_FEATURE_TRAILID, feature.getTrailId());
        values.put(KEY_FEATURE_NAME, feature.getName()); 
        values.put(KEY_FEATURE_LAT, (int) Math.round(feature.getLatitude() * 1e6));
        values.put(KEY_FEATURE_LON, (int) Math.round(feature.getLongitude() * 1e6));
        values.put(KEY_FEATURE_DESCRIPTION, feature.getDescription());
        values.put(KEY_FEATURE_DIRECTIONS, feature.getDirections());
        values.put(KEY_FEATURE_DIFFICULTY, feature.getDifficulty());
        int id = (int) db.insert(TABLE_FEATURES, null, values);
        db.close();
        return id;	
    }

	public int updateFeature(Feature feature) {
    	int id = feature.getId();
    	SQLiteDatabase db = this.getWritableDatabase(); 
        ContentValues values = new ContentValues();
        values.put(KEY_FEATURE_LOCATIONID, feature.getLocationId());
        values.put(KEY_FEATURE_TRAILID, feature.getTrailId());
        values.put(KEY_FEATURE_NAME, feature.getName()); 
        values.put(KEY_FEATURE_LAT, (int) Math.round(feature.getLatitude() * 1e6));
        values.put(KEY_FEATURE_LON, (int) Math.round(feature.getLongitude() * 1e6));
        values.put(KEY_FEATURE_DESCRIPTION, feature.getDescription());
        values.put(KEY_FEATURE_DIRECTIONS, feature.getDirections());
        values.put(KEY_FEATURE_DIFFICULTY, feature.getDifficulty());
        int result = db.update(TABLE_FEATURES, values, KEY_FEATURE_ID + " = ?", new String[] { String.valueOf(id) });
        Tags tags = new Tags(mContext);
        tags.removeTagsFor(feature);
        tags.addTags(feature, feature.getTags());
//TODO: add comments and images and here too??        
        db.close();
        return result;
	}
	
	public int addFeatureFor(Object o,Feature feature) {
		int result = -1;
		int objectId = -1;
		if (o instanceof Location) {
			objectId = ((Location) o).getId();
			feature.setLocationId(objectId);
		} else if (o instanceof Trail) {
			objectId = ((Trail) o).getId();
			feature.setTrailId(objectId);
			feature.setLocationId(((Trail) o).getLocationId());
		}
		if (objectId == -1) {return -1;} //return if no ID has been set
		feature.setId(find(feature));
		if (feature.getId() == -1) {
			result = addFeature(feature);
			
		} else {
			result = updateFeature(feature);
		}
		return result;
	}
	
	public Feature getFeature(int id) {
		Feature result = null;
    	String query = "SELECT " + KEY_FEATURE_ID + "," + KEY_FEATURE_LOCATIONID + "," + KEY_FEATURE_TRAILID + ","
    			+ KEY_FEATURE_NAME + "," + KEY_FEATURE_LAT + "," + KEY_FEATURE_LON + ","
    			+ KEY_FEATURE_DESCRIPTION + "," + KEY_FEATURE_DIRECTIONS + "," + KEY_FEATURE_DIFFICULTY
    			+ " FROM " + TABLE_FEATURES
    			+ " WHERE " + KEY_FEATURE_ID + " = " + Integer.toString(id) + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			Feature feature = new Feature(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),
					cursor.getString(3),(cursor.getInt(4)/1e6),(cursor.getInt(5)/1e6),
					cursor.getString(6),cursor.getString(7),cursor.getInt(8));
		    Images images = new Images(mContext);
	        feature.setImages(images.getImagesFor(feature));
	        Tags tags = new Tags(mContext);
	        feature.setTags(tags.getTagsFor(feature));
	        Comments comments = new Comments(mContext);
	        feature.setComments(comments.getCommentsFor(feature));
		    result = feature;
		} 
		db.close();
		return result;
	}
	
	public List<Feature> getFeaturesFor(Object o) {
		List<Feature> result = new ArrayList<Feature>();
		String whereClause = "";
		if (o instanceof Location) {
			whereClause = " WHERE " + KEY_FEATURE_LOCATIONID + " = " + ((Location) o).getId();
		} else if (o instanceof Trail) {
			whereClause = " WHERE " + KEY_FEATURE_TRAILID + " = " + ((Trail) o).getId();
		}
		String query = "SELECT " + KEY_FEATURE_ID + " FROM " + TABLE_FEATURES + whereClause;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if (cursor.moveToFirst()) {
			do {
				Feature feature = getFeature(cursor.getInt(0));
				result.add(feature);
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}
	
	public void removeFeature(Feature feature) {
	   	int id = find(feature);
    	if ( id >= 0 ) {
    		SQLiteDatabase db = this.getWritableDatabase();
    		db.delete(TABLE_FEATURES, KEY_FEATURE_ID + " = ?", new String[] {String.valueOf(id) });
    		db.close();
    	}
	}

}
