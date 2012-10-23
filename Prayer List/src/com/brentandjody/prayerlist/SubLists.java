package com.brentandjody.prayerlist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SubLists {
	
	private ContentResolver subLists;
	
	public SubLists(Context c) {
		subLists = c.getContentResolver();
	}
	
	public int add(SubList subList) {
		if (subList == null) return -1;
		int result = -1;
		ContentValues values = new ContentValues();
		values.put(Database.KEY_SUBLIST_NAME, subList.getName());
		values.put(Database.KEY_SUBLIST_SIZE, subList.getSize());
		values.put(Database.KEY_SUBLIST_LASTUSED, subList.getLastUsed().getTime());
		values.put(Database.KEY_SUBLIST_TIMESUSED, subList.getTimesUsed());
		String idString = subLists.insert(SubListProvider.CONTENT_URI, values).getLastPathSegment();
		result = Integer.parseInt(idString);
		return result;
	}
	
	public SubList get(int id) {
		if (id<0) return null;
		SubList subList = null;
		String[] projection = {Database.KEY_SUBLIST_NAME,
							Database.KEY_SUBLIST_SIZE,
							Database.KEY_SUBLIST_LASTUSED,
							Database.KEY_SUBLIST_TIMESUSED};
		Cursor cursor = subLists.query(Uri.withAppendedPath(SubListProvider.CONTENT_URI,
							String.valueOf(id)), projection, null, null, null);
	    if (cursor.moveToFirst()) {
	    	subList = new SubList(id,cursor.getString(0),cursor.getInt(1),
	    			new java.sql.Date(Long.parseLong(cursor.getString(2))),cursor.getInt(3));
	    }
	    cursor.close();
	    return subList;
	}
	
	public Cursor getCursor(int sortOrderId) {
		String sortOrder="";
		switch (sortOrderId) {
		case SubList.SORT_BY_NAME: sortOrder=Database.KEY_SUBLIST_NAME; break;
		case SubList.SORT_BY_SIZE: sortOrder=Database.KEY_SUBLIST_SIZE + " DESC"; break;
		case SubList.SORT_BY_LASTUSED: sortOrder=Database.KEY_SUBLIST_LASTUSED + " DESC"; break;
		case SubList.SORT_BY_MOSTUSED: sortOrder=Database.KEY_SUBLIST_TIMESUSED + " DESC"; break;
		}
		String[] projection = {Database.KEY_SUBLIST_NAME,
				Database.KEY_SUBLIST_SIZE,
				Database.KEY_SUBLIST_LASTUSED,
				Database.KEY_SUBLIST_TIMESUSED,
				Database.KEY_SUBLIST_ID};
		return subLists.query(SubListProvider.CONTENT_URI, projection, null, null, sortOrder);
	}
	
	public List<SubList> getAll(int sortOrderId) {
		List<SubList> result = new ArrayList<SubList>();
		Cursor cursor = getCursor(sortOrderId);
		if (cursor.moveToFirst()) {
			do {
				result.add(new SubList(cursor.getInt(4),cursor.getString(0),cursor.getInt(1),
						new java.sql.Date(Long.parseLong(cursor.getString(2))),cursor.getInt(3)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}
	
	public SubList find(String name) {
		SubList subList = null;
		String where = Database.KEY_SUBLIST_NAME+" =?";
		String[] whereArgs = new String[] {name};
		String[] projection = {Database.KEY_SUBLIST_NAME,
							Database.KEY_SUBLIST_SIZE,
							Database.KEY_SUBLIST_LASTUSED,
							Database.KEY_SUBLIST_TIMESUSED,
							Database.KEY_SUBLIST_ID};
		Cursor cursor = subLists.query(SubListProvider.CONTENT_URI, projection, where, whereArgs, null);
	    if (cursor.moveToFirst()) {
	    	subList = new SubList(cursor.getInt(4),cursor.getString(0),cursor.getInt(1),
	    			new java.sql.Date(Long.parseLong(cursor.getString(2))),cursor.getInt(3));
	    }
	    cursor.close();
	    return subList;
	}
	
	public void update(SubList subList) {
		if ((subList == null) || (subList.getId() == -1)) return;
		ContentValues values = new ContentValues();
		values.put(Database.KEY_SUBLIST_NAME, subList.getName());
		values.put(Database.KEY_SUBLIST_SIZE, subList.getSize());
		values.put(Database.KEY_SUBLIST_LASTUSED, subList.getLastUsed().getTime());
		values.put(Database.KEY_SUBLIST_TIMESUSED, subList.getTimesUsed());
		subLists.update(Uri.withAppendedPath(SubListProvider.CONTENT_URI,String.valueOf(subList.getId())), values, "", null);
	}
	
	public boolean delete(int id) {
		if (id == -1) return false;
		String where = Database.KEY_SUBLIST_ID + "=?";
		String[] whereArgs = new String[] {Integer.toString(id)};
		int rows = subLists.delete(SubListProvider.CONTENT_URI, where, whereArgs);
		return (rows>0);
	}
	
	public boolean delete(SubList subList) {
		if ((subList == null) || (subList.getId() == -1)) return false;
		return delete(subList.getId());
	}

}
