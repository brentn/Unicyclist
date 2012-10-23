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

public class SubListProvider extends ContentProvider {
	
	private static final String AUTHORITY = "com.brentandjody.prayerlist.providers.SubListProvider";
	public static final int SUBLISTS = 100;
	public static final int SUBLIST = 110;
	private static final String LISTS_BASE_PATH = "sublists";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LISTS_BASE_PATH);
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-sublist";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-sublist";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
	    sURIMatcher.addURI(AUTHORITY, LISTS_BASE_PATH, SUBLISTS);
	    sURIMatcher.addURI(AUTHORITY, LISTS_BASE_PATH + "/#", SUBLIST);
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
	    queryBuilder.setTables(Database.TABLE_SUBLISTS);
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case SUBLIST:
	        queryBuilder.appendWhere(Database.KEY_SUBLIST_ID + "=" + uri.getLastPathSegment());
	        break;
	    case SUBLISTS:
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
	    case SUBLISTS:
	        rowsAffected = sqlDB.delete(Database.TABLE_SUBLISTS,
	                selection, selectionArgs);
	        break;
	    case SUBLIST:
	        String id = uri.getLastPathSegment();
	        if (TextUtils.isEmpty(selection)) {
	            rowsAffected = sqlDB.delete(Database.TABLE_SUBLISTS,
	                    Database.KEY_SUBLIST_ID + "=" + id, null);
	        } else {
	            rowsAffected = sqlDB.delete(Database.TABLE_SUBLISTS, selection + " and " + Database.KEY_SUBLIST_ID + "=" + id, selectionArgs);
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
        case SUBLISTS:
            return CONTENT_TYPE;
        case SUBLIST:
            return CONTENT_ITEM_TYPE;
        default:
            return null;
        }
    }

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
        if (uriType != SUBLISTS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        long newID = sqlDB
                .insert(Database.TABLE_SUBLISTS, null, values);
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
	        case SUBLIST:
	            String id = uri.getLastPathSegment();
	            StringBuilder modSelection = new StringBuilder(Database.KEY_SUBLIST_ID + "=" + id);
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	            rowsAffected = sqlDB.update(Database.TABLE_SUBLISTS, values, modSelection.toString(), null);
	            break;
	        case SUBLISTS:
	            rowsAffected = sqlDB.update(Database.TABLE_SUBLISTS, values, selection, selectionArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI");
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return rowsAffected;
        }

}
