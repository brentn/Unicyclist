package com.brentandjody.prayerlist;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PrayerRequestProvider extends ContentProvider {
	
	private static final String AUTHORITY = "com.brentandjody.prayerlist.providers.PrayerRequestProvider";
	public static final int REQUESTS = 100;
	public static final int REQUEST = 110;
	private static final String REQUESTS_BASE_PATH = "prayerrequests";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + REQUESTS_BASE_PATH);
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-prayerrequest";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-prayerrequest";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
	    sURIMatcher.addURI(AUTHORITY, REQUESTS_BASE_PATH, REQUESTS);
	    sURIMatcher.addURI(AUTHORITY, REQUESTS_BASE_PATH + "/#", REQUEST);
	}
	
	private Database db;

	@Override
	public boolean onCreate() {
		db = new Database(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    queryBuilder.setTables(Database.TABLE_REQUESTS);
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case REQUEST:
	        queryBuilder.appendWhere(Database.KEY_REQUEST_ID + "=" + uri.getLastPathSegment());
	        break;
	    case REQUESTS:
	        // no filter
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
	    Cursor cursor = queryBuilder.query(db.getReadableDatabase(),projection, selection, selectionArgs, null, null, sortOrder);
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = db.getWritableDatabase();
	    int rowsAffected = 0;
	    switch (uriType) {
	    case REQUESTS:
	        rowsAffected = sqlDB.delete(Database.TABLE_REQUESTS,
	                selection, selectionArgs);
	        break;
	    case REQUEST:
	        String id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(Database.TABLE_REQUESTS,
	                    Database.KEY_REQUEST_ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(Database.TABLE_REQUESTS, selection + " and " + Database.KEY_REQUEST_ID + "=" + id, selectionArgs);
	        }
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsAffected;
    }

	@Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case REQUESTS:
            return CONTENT_TYPE;
        case REQUEST:
            return CONTENT_ITEM_TYPE;
        default:
            return null;
        }
    }

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
        if (uriType != REQUESTS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        long newID = sqlDB
                .insert(Database.TABLE_REQUESTS, null, values);
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }	
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		 int uriType = sURIMatcher.match(uri);
	        SQLiteDatabase sqlDB = db.getWritableDatabase();

	        int rowsAffected;

	        switch (uriType) {
	        case REQUEST:
	            String id = uri.getLastPathSegment();
	            StringBuilder modSelection = new StringBuilder(Database.KEY_REQUEST_ID + "=" + id);
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	            rowsAffected = sqlDB.update(Database.TABLE_REQUESTS, values, modSelection.toString(), null);
	            break;
	        case REQUESTS:
	            rowsAffected = sqlDB.update(Database.TABLE_REQUESTS, values, selection, selectionArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI");
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return rowsAffected;
        }

}
