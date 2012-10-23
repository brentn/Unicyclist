package com.unicycle.rides;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unicycle.Person;
import com.unicycle.tags.Tags;

public class Rides extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "rides";
	
	//table names
	private static final String TABLE_RIDES = "rides";
	private static final String TABLE_PARTICIPANTS = "participants";
	
	//column names
	private static final String KEY_RIDE_ID = "id";
	private static final String KEY_RIDE_DATE = "date";
	private static final String KEY_RIDE_LOCATION = "location";
	private static final String KEY_RIDE_TRAIL = "trail";
	private static final String KEY_RIDE_LENGTH = "length";
	private static final String KEY_RIDE_COMPLETED = "completed";
	private static final String KEY_RIDE_CONDITION = "condition";
	private static final String KEY_RIDE_QUALITY = "quality";
	private static final String KEY_RIDE_COMMENTS = "comments";
	private static final String KEY_PARTICIPANT_ID = "id";
	private static final String KEY_PARTICIPANT_RIDEID = "rideId";
	private static final String KEY_PARTICIPANT_PARTICIPANTID = "participantId";
	
	private Context mContext;

    public Rides(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_RIDE_TABLE = "CREATE TABLE " + TABLE_RIDES + " (" + KEY_RIDE_ID + " INTEGER PRIMARY KEY, "
				+ KEY_RIDE_DATE + " INTEGER, " + KEY_RIDE_LOCATION + " INTEGER, " + KEY_RIDE_TRAIL + " INTEGER, "
				+ KEY_RIDE_LENGTH + " INTEGER, " + KEY_RIDE_COMPLETED + " INTEGER, " + KEY_RIDE_CONDITION + " INTEGER, "
				+ KEY_RIDE_QUALITY + " INTEGER, " + KEY_RIDE_COMMENTS + " TEXT)";
		String CREATE_PARTICIPANT_TABLE = "CREATE TABLE " + TABLE_PARTICIPANTS + " (" + KEY_PARTICIPANT_ID + " INTEGER PRIMARY KEY, "
				+ KEY_PARTICIPANT_RIDEID + " INTEGER, " + KEY_PARTICIPANT_PARTICIPANTID + " INTEGER)";
		db.execSQL(CREATE_RIDE_TABLE);
		db.execSQL(CREATE_PARTICIPANT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public int find(Ride ride) {
		int result = -1;
		String query = "SELECT " + KEY_RIDE_ID + " FROM " + TABLE_RIDES 
				+ " WHERE " + KEY_RIDE_DATE + " = " + Long.toString(ride.getDate().getTime())
				+ " AND " + KEY_RIDE_LOCATION + " = " + Integer.toString(ride.getLocationId())
				+ " AND " + KEY_RIDE_TRAIL + " = " + Integer.toString(ride.getTrailId())
				+ " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = cursor.getInt(0);
		}
		db.close();
		return result;
	}
	
	public int addRide(Ride ride) {
		int result = find(ride);
		if (result == -1) {
	    	SQLiteDatabase db = this.getWritableDatabase(); 	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_RIDE_DATE, ride.getDate().getTime());
	        values.put(KEY_RIDE_LOCATION, ride.getLocationId());
	        values.put(KEY_RIDE_TRAIL, ride.getTrailId());
	        values.put(KEY_RIDE_LENGTH, (ride.getLength()*1e6));
	        values.put(KEY_RIDE_COMPLETED, (ride.completed()?1:0));
	        values.put(KEY_RIDE_CONDITION, ride.getTrailCondition());
	        values.put(KEY_RIDE_QUALITY, ride.getRideQuality());
	        values.put(KEY_RIDE_COMMENTS, ride.getComments());
	        result = (int) db.insert(TABLE_RIDES, null, values);
	        if (ride.getParticipants().size() > 0) {
	        	addParticipants(ride.getId(),ride.getParticipants());
	        }
	        db.close();
		}
		return result;
	}
	
	public int addParticipant(int rideId,Person participant) {
		int result = -1;
		//Ensure participant does not already have a record for this ride
		String query = "SELECT " + KEY_PARTICIPANT_ID + " FROM " + TABLE_PARTICIPANTS
				+ " WHERE " + KEY_PARTICIPANT_PARTICIPANTID + " = " + Integer.toString(participant.getId())
				+ " AND " + KEY_PARTICIPANT_RIDEID + " = " + Integer.toString(rideId);
		SQLiteDatabase rdb = this.getReadableDatabase();
		Cursor cursor = rdb.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = cursor.getInt(0);
		} else {
			SQLiteDatabase wdb = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_PARTICIPANT_RIDEID, rideId);
			values.put(KEY_PARTICIPANT_PARTICIPANTID, participant.getId());
			result = (int) wdb.insert(TABLE_PARTICIPANTS, null, values);
			wdb.close();
		}
		rdb.close();
		return result;
	}
	
	public void addParticipants(int rideId, List<Person> participants) {
		Iterator<Person> iterator = participants.iterator();
		while (iterator.hasNext()) {
			addParticipant(rideId, iterator.next());
		}
	}
	
	public Ride getRide(int rideId) {
		Ride result = null;
		String query = "SELECT " + KEY_RIDE_ID + ", " + KEY_RIDE_DATE + ", " + KEY_RIDE_LOCATION + ", " + KEY_RIDE_TRAIL + ", " + KEY_RIDE_LENGTH + ", "
				+ KEY_RIDE_COMPLETED + ", " + KEY_RIDE_CONDITION + ", " + KEY_RIDE_QUALITY + ", " + KEY_RIDE_COMMENTS
				+ " FROM " + TABLE_RIDES
				+ " WHERE " + KEY_RIDE_ID + " = " + Integer.toString(rideId) + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = new Ride(cursor.getInt(0),new Date(Long.parseLong(cursor.getString(1))),cursor.getInt(2),cursor.getInt(3),
					(cursor.getInt(4)/1e6), (cursor.getInt(5)==1), cursor.getInt(6), cursor.getInt(7), cursor.getString(8));
			result.setTags(new Tags(mContext).getTagsFor(result));
		}
		db.close();
		return result;
	}
	
	public List<Ride> getAllRidesFor(Object o) {
		List<Ride> result = new ArrayList<Ride>();
		String query = "SELECT " + KEY_RIDE_ID + " FROM " + TABLE_RIDES + " AS r ";
		String joinClause = "";
		String whereClause = "";
		if (o instanceof Date) {
			whereClause = " WHERE " + KEY_RIDE_DATE + " = " + Long.toString(((Date) o).getTime());
		} else if (o instanceof Person) {
			joinClause = " JOIN " + TABLE_PARTICIPANTS + " AS p ON r." + KEY_RIDE_ID + " = p." + KEY_PARTICIPANT_RIDEID;
			whereClause = " WHERE p." + KEY_PARTICIPANT_PARTICIPANTID + " = " + Integer.toString(((Person) o).getId()); 
		} else if (o instanceof Integer) {
			whereClause = " WHERE " + KEY_RIDE_LOCATION + " = " + Integer.toString((Integer) o); 			
		} else if (o instanceof Long) {
			whereClause = " WHERE " + KEY_RIDE_TRAIL + " = " + Long.toString((Long) o); 						
		}
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query+joinClause+whereClause,null);
		if (cursor.moveToFirst()) {
			do {
				result.add(getRide(cursor.getInt(0)));
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}
	
	public int updateRide(Ride ride) {
		int result = find(ride);
		if (result >= 0) {
	    	SQLiteDatabase db = this.getWritableDatabase(); 	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_RIDE_DATE, ride.getDate().getTime());
	        values.put(KEY_RIDE_LOCATION, ride.getLocationId());
	        values.put(KEY_RIDE_TRAIL, ride.getTrailId());
	        values.put(KEY_RIDE_LENGTH, (ride.getLength()*1e6));
	        values.put(KEY_RIDE_COMPLETED, (ride.completed()?1:0));
	        values.put(KEY_RIDE_CONDITION, ride.getTrailCondition());
	        values.put(KEY_RIDE_QUALITY, ride.getRideQuality());
	        values.put(KEY_RIDE_COMMENTS, ride.getComments());
	        result = db.update(TABLE_RIDES, values, KEY_RIDE_ID + " = ?", new String[] { String.valueOf(result) });
	        if (ride.getParticipants().size() > 0) {
	        	addParticipants(ride.getId(),ride.getParticipants());
	        }
	        db.close();
		}
		return result;
	}
	
	public void deleteRide(Ride ride) {
		//delete ride participants
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PARTICIPANTS, KEY_PARTICIPANT_RIDEID + " = ?", new String[] { String.valueOf(ride.getId())});
		//delete ride
		db.delete(TABLE_RIDES, KEY_RIDE_ID + " = ?", new String[] { String.valueOf(ride.getId())});
		db.close();
	}

}
