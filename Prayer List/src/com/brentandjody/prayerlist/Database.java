package com.brentandjody.prayerlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "prayerjournal.db";
	
	//table names
	public static final String TABLE_REQUESTS = "requests";
	public static final String TABLE_SUBLISTS = "sublists";
	private static final String TABLE_REQUEST_SUBLISTS = "request_sublists";
	private static final String TABLE_JOURNALENTRIES = "journalentries";
	private static final String TABLE_ANSWERS = "answers";
	
	//column names
	public static final String KEY_REQUEST_ID = "_id";
	public static final String KEY_REQUEST_DESCRIPTION = "description";
	public static final String KEY_REQUEST_CHECKED = "checked";
	public static final String KEY_REQUEST_ANSWERED = "answered";
	public static final String KEY_REQUEST_REQUESTDATE = "requestdate";
	
	public static final String KEY_SUBLIST_ID = "_id";
	public static final String KEY_SUBLIST_NAME = "name";
	public static final String KEY_SUBLIST_SIZE = "size";
	public static final String KEY_SUBLIST_LASTUSED = "last_used";
	public static final String KEY_SUBLIST_TIMESUSED = "times_used";
	
	private static final String KEY_REQUEST_SUBLIST_ID = "_id";
	private static final String KEY_REQUEST_SUBLIST_REQUESTID = "requestid";
	private static final String KEY_REQUEST_SUBLIST_SUBLISTID = "sublistid";
	
	private static final String KEY_JOURNALENTRY_ID = "_id";
	private static final String KEY_JOURNALENTRY_REQUESTID = "requestid";
	private static final String KEY_JOURNALENTRY_DATE = "date";
	private static final String KEY_JOURNALENTRY_ENTRY = "entry";
	
	private static final String KEY_ANSWER_ID = "_id";
	private static final String KEY_ANSWER_REQUESTID = "requestid";
	private static final String KEY_ANSWER_ANSWERDATE = "answerdate";
	private static final String KEY_ANSWER_RATING = "rating";
	private static final String KEY_ANSWER_DETAILS = "details";
	
    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + "("
				+ KEY_REQUEST_ID + " INTEGER PRIMARY KEY," + KEY_REQUEST_DESCRIPTION + " TEXT," + KEY_REQUEST_CHECKED + " INTEGER,"
				+ KEY_REQUEST_ANSWERED + " INTEGER," + KEY_REQUEST_REQUESTDATE + " INTEGER)";
		String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_SUBLISTS + "(" + KEY_SUBLIST_ID + " INTEGER PRIMARY KEY," + KEY_SUBLIST_NAME + " TEXT,"
				+ KEY_SUBLIST_SIZE + " INTEGER," + KEY_SUBLIST_LASTUSED + " INTEGER," + KEY_SUBLIST_TIMESUSED + " INTEGER)";
		String CREATE_REQUEST_SUBLISTS_TABLE = "CREATE TABLE " + TABLE_REQUEST_SUBLISTS + "(" + KEY_REQUEST_SUBLIST_ID + " INTEGER PRIMARY KEY,"
				+ KEY_REQUEST_SUBLIST_REQUESTID + " INTEGER," + KEY_REQUEST_SUBLIST_SUBLISTID + " INTEGER)";
		String CREATE_JOURNALENTRIES_TABLE = "CREATE TABLE " + TABLE_JOURNALENTRIES + "(" + KEY_JOURNALENTRY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_JOURNALENTRY_REQUESTID + " INTEGER, " + KEY_JOURNALENTRY_DATE + " INTEGER," + KEY_JOURNALENTRY_ENTRY + " TEXT)";
		String CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_ANSWERS + "(" + KEY_ANSWER_ID + " INTEGER PRIMARY KEY," + KEY_ANSWER_REQUESTID + " INTEGER,"
				+ KEY_ANSWER_ANSWERDATE + " INTEGER," + KEY_ANSWER_RATING + " INTEGER," + KEY_ANSWER_DETAILS + " TEXT)";
		db.execSQL(CREATE_REQUESTS_TABLE);
		db.execSQL(CREATE_TAGS_TABLE);
		db.execSQL(CREATE_REQUEST_SUBLISTS_TABLE);
		db.execSQL(CREATE_JOURNALENTRIES_TABLE);
		db.execSQL(CREATE_ANSWER_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	switch (oldVersion) {
    	case 1:
    		//String CREATE_ANSWER_TABLE = "CREATE TABLE " + TABLE_ANSWERS + "(" + KEY_ANSWER_ID + " INTEGER PRIMARY KEY," + KEY_ANSWER_REQUESTID + " INTEGER,"
    		//		+ KEY_ANSWER_ANSWERDATE + " INTEGER," + KEY_ANSWER_RATING + " INTEGER," + KEY_ANSWER_DETAILS + " TEXT)";
    		//db.execSQL(CREATE_ANSWER_TABLE);
    		//no break here
    	}
	}

	//
	// REQUESTS
	//
	
//	public int addRequest(PrayerRequest request) {
//		assert request != null;
//		int id = request.getId();
//		//don't add a duplicate
//		if ((id != -1) && (getRequest(id) != null)) {
//			return id;
//		}
//		SQLiteDatabase db = this.getWritableDatabase(); 	 
//        ContentValues values = new ContentValues();
//        values.put(KEY_REQUEST_DESCRIPTION, request.getDescription());
//        values.put(KEY_REQUEST_CHECKED, request.getChecked()?1:0);
//        values.put(KEY_REQUEST_ANSWERED, (int) request.getAnswered());
//        values.put(KEY_REQUEST_REQUESTDATE, request.getRequestDate().getTime());
//        id = (int) db.insert(TABLE_REQUESTS, null, values);
//        db.close();
//        addRequestToLists(request,request.getSubLists());
//        addJournal(request.getJournal());
//        return id;
//	}
//	
//	public PrayerRequest getRequest(int id) {
//		if (id == -1) {
//			return null;
//		}
//		PrayerRequest result = null;
//		String query = "SELECT " + KEY_REQUEST_ID + ", " + KEY_REQUEST_DESCRIPTION + ", "
//				+ KEY_REQUEST_CHECKED + ", " + KEY_REQUEST_ANSWERED + ", "
//				+ KEY_REQUEST_REQUESTDATE + " FROM " + TABLE_REQUESTS
//				+ " WHERE " + KEY_REQUEST_ID + " = " + Integer.toString(id) + " LIMIT 1";
//		SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//	        result = new PrayerRequest(id,cursor.getString(1),(cursor.getInt(2)==1),cursor.getInt(3),
//	        		new java.sql.Date(Long.parseLong(cursor.getString(4))),getListsForRequest(id),getJournalForRequest(id));
//        } 
//        db.close();
//        //PrayerRequest or null
//        return result;
//	}
//	
//	public boolean updateRequest(PrayerRequest request) {
//		if (request == null) {
//			return false;
//		}
//		int id = request.getId();
//		// instead add request, if it doesn't already exist
//		if ((id == -1) || (getRequest(id) == null)) {
//			return (addRequest(request) != -1);
//		} else {
//			String[] whereArgs = {Integer.toString(request.getId())};
//	        ContentValues values = new ContentValues();
//	        values.put(KEY_REQUEST_DESCRIPTION, request.getDescription());
//	        values.put(KEY_REQUEST_CHECKED, request.getChecked()?1:0);
//	        values.put(KEY_REQUEST_ANSWERED, (int) request.getAnswered());
//	        values.put(KEY_REQUEST_REQUESTDATE, request.getRequestDate().getTime());
//			SQLiteDatabase db = this.getWritableDatabase(); 	 
//	        db.update(TABLE_REQUESTS, values, "id=?",whereArgs);
//	        db.close();
//	        removeRequestFromAllLists(request);
//	        addRequestToLists(request,request.getSubLists());
//	        updateJournal(request.getJournal());
//	        return true;
//		}
//	}
//	
//	public boolean deleteRequest(PrayerRequest request) {
//		boolean result = false;
//		if ((request == null) || (request.getId() == -1)) {
//			return false;
//		}
//		deleteJournalFromRequest(request);
//		removeRequestFromAllLists(request);
//		// return true if it's already gone
//		if (getRequest(request.getId()) == null) {
//			return true;
//		}
//		SQLiteDatabase db = this.getWritableDatabase();
//		result = (db.delete(TABLE_REQUESTS, KEY_REQUEST_ID + " = ?", new String[] { String.valueOf(request.getId())}) > 0);
//		db.close();
//		return result;
//	}
//	
//	public List<PrayerRequest> getAllRequests() {
//		List<PrayerRequest> result = new ArrayList<PrayerRequest>();
//		String query = "SELECT " + KEY_REQUEST_ID + " FROM " + TABLE_REQUESTS;
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query,null);
//		if (cursor.moveToFirst()) {
//			do {
//				PrayerRequest r = getRequest(cursor.getInt(0));
//				result.add(r);
//			} while (cursor.moveToNext());
//		}
//		db.close();
//		//possibly empty list
//		assert result != null;
//		return result;
//	}
//	
//	public Cursor getAllRequestsCursor() {
//		SQLiteDatabase db = this.getReadableDatabase();
//		return db.rawQuery("SELECT " + KEY_REQUEST_ID + ", " + KEY_REQUEST_DESCRIPTION + ", " + KEY_REQUEST_CHECKED +" FROM " + TABLE_REQUESTS,null);
//	}
//	
//	public int requestsCount() {
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REQUESTS, null);
//		cursor.moveToFirst();
//		return cursor.getInt(0);
//	}
	
	//
	// Answers
	//
	
	public int addAnswer(Answer answer) {
		assert answer != null;
		int id = -1;
		SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_ANSWER_REQUESTID, answer.getRequestId()); 
        values.put(KEY_ANSWER_ANSWERDATE, answer.getAnswerDate().getTime());
        values.put(KEY_ANSWER_DETAILS, answer.getDetails());
        values.put(KEY_ANSWER_RATING,  answer.getRating());
        id = (int) db.insert(TABLE_ANSWERS, null, values);
        db.close();
		return id;
	}
	
	public Answer getAnswer(int id) {
		assert id != -1;
		Answer result = null;
		String query = "SELECT " + KEY_ANSWER_ID + ", " + KEY_ANSWER_REQUESTID + ", " + KEY_ANSWER_ANSWERDATE + ", " + KEY_ANSWER_DETAILS + ", " + KEY_ANSWER_RATING
				+ " FROM " + TABLE_ANSWERS
				+ " WHERE " + KEY_ANSWER_ID + " = '" + id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = new Answer(id,cursor.getInt(1),new Date(Long.parseLong(cursor.getString(2))),cursor.getInt(4),cursor.getString(3));
		}
		return result;	}
	
	public boolean updateAnswer(Answer answer) {
		if (answer == null) {
			return false;
		}
		int id = answer.getId();
		// instead add entry, if it doesn't already exist
		if ((id == -1) || (getAnswer(id) == null)) {
			return (addAnswer(answer) != -1);
		} else {
			String[] whereArgs = {Integer.toString(answer.getId())};
	        ContentValues values = new ContentValues();
	        values.put(KEY_ANSWER_REQUESTID, answer.getRequestId());
	        values.put(KEY_ANSWER_ANSWERDATE, answer.getAnswerDate().getTime());
	        values.put(KEY_ANSWER_DETAILS, answer.getDetails());
	        values.put(KEY_ANSWER_RATING,  answer.getRating());
			SQLiteDatabase db = this.getWritableDatabase(); 	 
	        db.update(TABLE_ANSWERS, values, "id=?",whereArgs);
	        db.close();
	        return true;
		}	}
	
	public boolean deleteAnswer(Answer answer) {
		assert answer != null;
		SQLiteDatabase db = this.getWritableDatabase();
		boolean result = (db.delete(TABLE_ANSWERS, KEY_ANSWER_ID + " = ?", new String[] { String.valueOf(answer.getId())}) > 0);
		db.close();
		return result;	}
	
	//
	// Journal Entries
	//
	
	public int addJournalEntry(JournalEntry entry) {
		int id = -1;
		SQLiteDatabase db = this.getWritableDatabase(); 	 
        ContentValues values = new ContentValues();
        values.put(KEY_JOURNALENTRY_REQUESTID, entry.getRequestId()); 
        values.put(KEY_JOURNALENTRY_DATE, entry.getDate().getTime());
        values.put(KEY_JOURNALENTRY_ENTRY, entry.getEntry());
        id = (int) db.insert(TABLE_JOURNALENTRIES, null, values);
        db.close();
		return id;
	}
	
	public JournalEntry getJournalEntry(int id) {
		assert id != -1;
		JournalEntry result = null;
		String query = "SELECT " + KEY_JOURNALENTRY_ID + ", " + KEY_JOURNALENTRY_DATE + ", " + KEY_JOURNALENTRY_ENTRY
				+ " FROM " + TABLE_JOURNALENTRIES
				+ " WHERE " + KEY_JOURNALENTRY_ID + " = '" + id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = new JournalEntry(cursor.getInt(0),id,new Date(Long.parseLong(cursor.getString(1))),cursor.getString(2));
		}
		return result;
	}
	
	public boolean updateJournalEntry(JournalEntry entry) {
		if (entry == null) {
			return false;
		}
		int id = entry.getId();
		// instead add entry, if it doesn't already exist
		if ((id == -1) || (getJournalEntry(id) == null)) {
			return (addJournalEntry(entry) != -1);
		} else {
			String[] whereArgs = {Integer.toString(entry.getId())};
	        ContentValues values = new ContentValues();
	        values.put(KEY_JOURNALENTRY_REQUESTID, entry.getRequestId());
	        values.put(KEY_JOURNALENTRY_DATE, entry.getDate().getTime());
	        values.put(KEY_JOURNALENTRY_ENTRY, entry.getEntry());
			SQLiteDatabase db = this.getWritableDatabase(); 	 
	        db.update(TABLE_JOURNALENTRIES, values, "id=?",whereArgs);
	        db.close();
	        return true;
		}
	}
	
	public boolean deleteJournalEntry(JournalEntry entry) {
		assert entry != null;
		SQLiteDatabase db = this.getWritableDatabase();
		boolean result = (db.delete(TABLE_JOURNALENTRIES, KEY_JOURNALENTRY_ID + " = ?", new String[] { String.valueOf(entry.getId())}) > 0);
		db.close();
		return result;
	}
	
	// Journal
	
	public void addJournal(List<JournalEntry> journal) {
		assert journal != null;
		for (JournalEntry entry : journal) {
			addJournalEntry(entry);
		}
	}
	
	public List<JournalEntry> getJournal(PrayerRequest request) {
		assert request != null;
		assert request.getId() != -1;
		return getJournalForRequest(request.getId());
	}
	
	public List<JournalEntry> getJournalForRequest(int id) {
		assert id != -1;
		List<JournalEntry> result = new ArrayList<JournalEntry>();
		String query = "SELECT " + KEY_JOURNALENTRY_ID + ", " + KEY_JOURNALENTRY_DATE + ", " + KEY_JOURNALENTRY_ENTRY
				+ " FROM " + TABLE_JOURNALENTRIES
				+ " WHERE " + KEY_JOURNALENTRY_REQUESTID + " = '" + id +"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				result.add(new JournalEntry(cursor.getInt(0),id,new Date(Long.parseLong(cursor.getString(1))),cursor.getString(2)));
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}
	
	public void updateJournal(List<JournalEntry> journal) {
		assert journal != null;
		for (JournalEntry entry : journal) {
			updateJournalEntry(entry);
		}
	}
	
	public void deleteJournal(List<JournalEntry> journal) {
		for (JournalEntry entry : journal) {
			deleteJournalEntry(entry);
		}
	}
	
	public void deleteJournalFromRequest(PrayerRequest request) {
		assert request != null;
		String query = "DELETE FROM " + TABLE_JOURNALENTRIES + " WHERE " + KEY_JOURNALENTRY_REQUESTID + " = " +  request.getId();
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(query);
		db.close(); 
	}
	
	//
	// SubLists
	//
	
//	public int addSubList(SubList subList) {
//		assert subList != null;
//		int id = subList.getId();
//		// if sublist already exists, return the id
//		if (findSubListByName(subList.getName()) == null) {
//			ContentValues values = new ContentValues();
//	        values.put(KEY_SUBLIST_NAME, subList.getName());
//	        values.put(KEY_SUBLIST_SIZE, subList.getSize());
//	        values.put(KEY_SUBLIST_LASTUSED, subList.getLastUsed().getTime());
//	        values.put(KEY_SUBLIST_TIMESUSED, subList.getTimesUsed());
//	        SQLiteDatabase db = this.getWritableDatabase(); 	 
//	        id = (int) db.insert(TABLE_SUBLISTS, null, values);
//	        db.close();
//		} else {
//			id = findSubListByName(subList.getName()).getId();
//		}
//		assert id != -1;
//		//sublistId
//        return id;
//	}
//
//	public SubList getSubList(int id) {
//		assert id != -1;
//		SubList result = null;
//		String query = "SELECT " + KEY_SUBLIST_ID + ", " + KEY_SUBLIST_NAME + ", " + KEY_SUBLIST_SIZE + ", " + KEY_SUBLIST_LASTUSED + ", " 
//		+ KEY_SUBLIST_TIMESUSED + " FROM " + TABLE_SUBLISTS + " WHERE " + KEY_SUBLIST_ID + " = '" + id + "'";
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			result = new SubList(id,cursor.getString(1),cursor.getInt(2),new java.sql.Date(Long.parseLong(cursor.getString(3))),cursor.getInt(4));
//		}
//		db.close();
//		//subList or null
//		return result;
//	}
//	
//	public boolean updateSubList(SubList subList) {
//		if (subList == null) {
//			return false;
//		}
//		int id = subList.getId();
//		// instead add subList, if it doesn't already exist
//		if ((id == -1) || (getSubList(id) == null)) {
//			return (addSubList(subList) != -1);
//		} else {
//			String[] whereArgs = {Integer.toString(subList.getId())};
//	        ContentValues values = new ContentValues();
//	        values.put(KEY_SUBLIST_NAME, subList.getName());
//	        values.put(KEY_SUBLIST_SIZE, subList.getSize());
//	        values.put(KEY_SUBLIST_LASTUSED, subList.getLastUsed().getTime());
//	        values.put(KEY_SUBLIST_TIMESUSED, subList.getTimesUsed());
//			SQLiteDatabase db = this.getWritableDatabase(); 	 
//	        db.update(TABLE_REQUESTS, values, "id=?",whereArgs);
//	        db.close();
//	        return true;
//		}	
//	}
//	
//	public boolean deleteSubList(SubList subList) {
//		boolean result = false;
//		if ((subList == null) || (subList.getId() == -1)) {
//			return false;
//		}
//		//return true if it's already gone
//		if (getSubList(subList.getId()) == null) {
//			return true;
//		}
//		SQLiteDatabase db = this.getWritableDatabase();
//		result = (db.delete(TABLE_SUBLISTS, KEY_SUBLIST_ID + " = ?", new String[] { String.valueOf(subList.getId())}) > 0);
//		db.close();
//		return result;
//	}
//	
//	public void addSubLists(List<SubList> subLists) {
//		if (subLists == null) return;
//		for (SubList subList : subLists) {
//			addSubList(subList);
//		}
//	}
//	
//	public List<SubList> getAllSubLists(int sortOrder) {
//		String query = "SELECT " + KEY_SUBLIST_ID + " FROM " + TABLE_SUBLISTS;
//		List<SubList> result = new ArrayList<SubList>();
//		switch (sortOrder) {
//		case SubList.SORT_BY_NAME:
//			query += " ORDER BY " + KEY_SUBLIST_NAME;
//			break;
//		case SubList.SORT_BY_SIZE:
//			query += " ORDER BY DESCENDING " + KEY_SUBLIST_SIZE;
//			break;
//		case SubList.SORT_BY_LASTUSED:
//			query += " ORDER BY DESCENDING " + KEY_SUBLIST_LASTUSED;
//			break;
//		case SubList.SORT_BY_MOSTUSED:
//			query += " ORDER BY DESCENDING " + KEY_SUBLIST_TIMESUSED;
//			break;
//		}
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			do {
//				result.add(getSubList(cursor.getInt(0)));
//			} while (cursor.moveToNext());
//		}
//		db.close();
//		//possibly empty tag list
//		return result;
//	}
//	
//	public SubList findSubListByName(String name) {
//		assert ! name.equals("");
//		SubList result = null;
//		String query = "SELECT " + KEY_SUBLIST_ID 
//				+ " FROM " + TABLE_SUBLISTS + " WHERE " + KEY_SUBLIST_NAME + " = '" + name + "'";
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			result = getSubList(cursor.getInt(0));
//		}
//		db.close();
//		//SubList or null
//		return result;
//	}

	//
	// REQUEST_SUBLISTS
	//
	
//	public int addRequestToList(PrayerRequest request, SubList subList) {
//		assert request != null;
//		assert subList != null;
//		int result;
//		//Add the list, if necessary
//		if (subList.getId() == -1) {
//			subList.setId(addSubList(subList));
//		}
//		//Add the request, if necessary
//		if (request.getId() == -1) {
//			request.setId(addRequest(request));
//		}
//		//Don't add duplicate
//		result = findRequestOnList(request,subList);
//		if (result == -1) {
//	        ContentValues values = new ContentValues();
//	        values.put(KEY_REQUEST_SUBLIST_REQUESTID, request.getId());
//	        values.put(KEY_REQUEST_SUBLIST_SUBLISTID, subList.getId());
//			SQLiteDatabase db = this.getWritableDatabase(); 	 
//	        db.insert(TABLE_REQUEST_SUBLISTS, null, values);
//	        db.close();
//	        //record sublist activity
//			subList.use();
//			updateSubList(subList);
//		}
//		return result;
//	}
//	
//	public void addRequestToLists(PrayerRequest request, List<SubList> subLists) {
//		if ((request == null) || (subLists == null)) return;
//		for (SubList subList : subLists) {
//			addRequestToList(request,subList);
//		}
//	}
//	
//	public void addRequestsToList(List<PrayerRequest> requests, SubList subList) {
//		if ((requests == null) || (subList == null)) return;
//		for (PrayerRequest request : requests) {
//			addRequestToList(request,subList);
//		}
//	}
//	
//	public List<SubList> getListsForRequest(int id) {
//		assert id != -1;
//		List<SubList> result = new ArrayList<SubList>();
//		String query = "SELECT " + KEY_REQUEST_SUBLIST_SUBLISTID + " FROM " + TABLE_REQUEST_SUBLISTS + " WHERE " + KEY_REQUEST_SUBLIST_REQUESTID + " = '" + id + "'";
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			do {
//				result.add(getSubList(cursor.getInt(0)));
//			} while (cursor.moveToNext());
//		}
//		db.close();
//		return result;
//	}
//	
//	public List<SubList> getListsForRequest(PrayerRequest request) {
//		assert request != null;
//		int id = request.getId();
//		return getListsForRequest(id);
//	}
//	
//	public List<PrayerRequest> getRequestsFromList(SubList subList) {
//		List<PrayerRequest> result = new ArrayList<PrayerRequest>();
//		String query;
//		//if subList == null, then return requests that aren't on any list
//		if (subList == null) {
//			// SELECT id FROM requests WHERE (NOT id IN (SELECT DISTINCT requestId FROM requests_sublists)); 
//			query = "SELECT " + KEY_REQUEST_ID 
//				+ " FROM " + TABLE_REQUESTS
//				+ " WHERE (NOT " + KEY_REQUEST_ID + " IN (SELECT DISTINCT " + KEY_REQUEST_SUBLIST_REQUESTID + " FROM " + TABLE_REQUEST_SUBLISTS + "))";
//		} else {
//			int id = subList.getId();
//			query = "SELECT " + KEY_REQUEST_SUBLIST_REQUESTID + " FROM " + TABLE_REQUEST_SUBLISTS + " WHERE " + KEY_REQUEST_SUBLIST_SUBLISTID + " = '" + id + "'";
//		}
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			do {
//				result.add(getRequest(cursor.getInt(0)));
//			} while (cursor.moveToNext());
//		}
//		db.close();
//		return result;
//	}
//	
//	public List<PrayerRequest> getRequestsWithoutList() {
//		return getRequestsFromList(null);
//	}
//	
//	public boolean removeRequestFromList(PrayerRequest request,SubList subList) {
//		assert request != null;
//		assert subList != null;
//		if ((request == null) || subList == null) {
//			return false;
//		}
//		int id = findRequestOnList(request,subList);
//		if (id == -1) {
//			return true;
//		} 
//		SQLiteDatabase db = this.getWritableDatabase();
//		int count = db.delete(TABLE_REQUEST_SUBLISTS, KEY_REQUEST_SUBLIST_ID + " = ?", new String[] { String.valueOf(id)});
//		db.close();
//		for (int x=0;x<count;x++) {
//			subList.disuse(); //decrement the lists size
//		}
//		return (count>0);
//	}
//	
//	public void removeRequestFromAllLists(PrayerRequest request) {
//		assert request != null;
//		if ((request == null) || (request.getId() == -1)) {
//			return;
//		}
//		String query = "SELECT " + KEY_REQUEST_SUBLIST_ID + " FROM " + TABLE_REQUEST_SUBLISTS + " WHERE " + KEY_REQUEST_SUBLIST_REQUESTID + " = '" + request.getId() + "'";
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			do {
//				removeRequestFromList(request,getSubList(cursor.getInt(0)));
//			} while (cursor.moveToNext());
//		}
//		db.close();
//	}
//	
//	public int findRequestOnList(PrayerRequest request, SubList subList) {
//		if ((request == null) || (request.getId()==-1) || (subList == null) || (subList.getId() == -1)) {
//			return -1;
//		}
//		int result = -1;
//		String query = "SELECT " + KEY_REQUEST_SUBLIST_ID + " FROM " + TABLE_REQUEST_SUBLISTS 
//				+ " WHERE " + KEY_REQUEST_SUBLIST_REQUESTID + " = '" + request.getId() + "' AND " 
//				+ KEY_REQUEST_SUBLIST_SUBLISTID + " = '" + subList.getId() + "'";
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst()) {
//			result = cursor.getInt(0);
//		}
//		db.close();
//		//returns id or -1
//		return result;
//	}

	
}
