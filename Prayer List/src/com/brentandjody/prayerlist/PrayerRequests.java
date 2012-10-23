package com.brentandjody.prayerlist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class PrayerRequests {
	
	private ContentResolver prayerRequests;
	
	public PrayerRequests(Context c) {
		prayerRequests = c.getContentResolver();
	}
	
	public int add(PrayerRequest request) {
		if (request == null) return -1;
		int result = -1;
		ContentValues values = new ContentValues();
		values.put(Database.KEY_REQUEST_DESCRIPTION, request.getDescription());
		values.put(Database.KEY_REQUEST_CHECKED, request.getChecked()?1:0);
		values.put(Database.KEY_REQUEST_ANSWERED, request.getAnswered());
		values.put(Database.KEY_REQUEST_REQUESTDATE, request.getRequestDate().getTime());
		String idString = prayerRequests.insert(PrayerRequestProvider.CONTENT_URI, values).getLastPathSegment();
		result = Integer.parseInt(idString);
		return result;
	}
	
	public PrayerRequest get(int id) {
		if (id<0) return null;
		PrayerRequest request = null;
		String[] projection = {Database.KEY_REQUEST_DESCRIPTION,
							Database.KEY_REQUEST_CHECKED,
							Database.KEY_REQUEST_ANSWERED,
							Database.KEY_REQUEST_REQUESTDATE};
		Cursor cursor = prayerRequests.query(Uri.withAppendedPath(PrayerRequestProvider.CONTENT_URI,
							String.valueOf(id)), projection, null, null, null);
	    if (cursor.moveToFirst()) {
	    	//TODO: Not returning lists or journal yet
	    	request = new PrayerRequest(id,cursor.getString(0),(cursor.getInt(1)==1),cursor.getInt(2),
	    			new java.sql.Date(Long.parseLong(cursor.getString(3))),null,null);
	    }
	    cursor.close();
	    return request;
	}
	
	public void update(PrayerRequest request) {
		if ((request == null) || (request.getId() == -1)) return;
		ContentValues values = new ContentValues();
		values.put(Database.KEY_REQUEST_DESCRIPTION, request.getDescription());
		values.put(Database.KEY_REQUEST_CHECKED, request.getChecked()?1:0);
		values.put(Database.KEY_REQUEST_ANSWERED, request.getAnswered());
		values.put(Database.KEY_REQUEST_REQUESTDATE, request.getRequestDate().getTime());
		//TODO: doesn't update lists or journal yet
		prayerRequests.update(Uri.withAppendedPath(PrayerRequestProvider.CONTENT_URI,String.valueOf(request.getId())), values,"",null);
	}
	
	public boolean delete(int id) {
		if (id == -1) return false;
		String where = Database.KEY_SUBLIST_ID + "=?";
		String[] whereArgs = new String[] {Integer.toString(id)};
		int rows = prayerRequests.delete(PrayerRequestProvider.CONTENT_URI, where, whereArgs);
		return (rows>0);
	}
	
	public boolean delete(PrayerRequest request) {
		if ((request == null) || (request.getId() == -1)) return false;
		return delete(request.getId());
	}

}
