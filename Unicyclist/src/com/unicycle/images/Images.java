package com.unicycle.images;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.unicycle.UnicyclistActivity;
import com.unicycle.locations.Location;
import com.unicycle.locations.features.Feature;
import com.unicycle.locations.trails.Trail;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class Images extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_NAME = "images";
	
	//table names
	private static final String TABLE_IMAGES = "images";
	private static final String TABLE_LOCATION_IMAGES = "location_images";
	private static final String TABLE_TRAIL_IMAGES = "trail_images";
	private static final String TABLE_FEATURE_IMAGES = "feature_images";
	
	//column names
	private static final String KEY_IMAGE_ID = "id";
	private static final String KEY_IMAGE_URI = "uri";
	private static final String KEY_IMAGE_COVER = "cover";
	private static final String KEY_IMAGE_HASH = "hash";
	private static final String KEY_IMAGE_LAT = "lat";
	private static final String KEY_IMAGE_LON = "lon";
	private static final String KEY_IMAGE_DESCRIPTION = "description";
	private static final String KEY_LOCATION_IMAGES_ID = "id";
	private static final String KEY_LOCATION_IMAGES_IMAGEID = "imageId";
	private static final String KEY_LOCATION_IMAGES_LOCATIONID = "locationId";
	private static final String KEY_TRAIL_IMAGES_ID = "id";
	private static final String KEY_TRAIL_IMAGES_IMAGEID = "imageId";
	private static final String KEY_TRAIL_IMAGES_TRAILID = "trailId";
	private static final String KEY_FEATURE_IMAGES_ID = "id";
	private static final String KEY_FEATURE_IMAGES_IMAGEID = "imageId";
	private static final String KEY_FEATURE_IMAGES_FEATUREID = "featureId";

	private Context mContext;
	private List<Image> imageList;

    public Images(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
				+ KEY_IMAGE_ID + " INTEGER PRIMARY KEY, " + KEY_IMAGE_HASH + " INTEGER, "
				+ KEY_IMAGE_URI + " TEXT, " + KEY_IMAGE_COVER + " INTEGER, "
				+ KEY_IMAGE_LAT + " INTEGER, " + KEY_IMAGE_LON + " INTEGER, "
				+ KEY_IMAGE_DESCRIPTION + " TEXT)";
		String CREATE_LOCATION_IMAGES_TABLE = "CREATE TABLE " + TABLE_LOCATION_IMAGES + "("
				+ KEY_LOCATION_IMAGES_ID + " INTEGER PRIMARY KEY, " + KEY_LOCATION_IMAGES_IMAGEID + " INTEGER, "
				+ KEY_LOCATION_IMAGES_LOCATIONID + " INTEGER)";
		String CREATE_TRAIL_IMAGES_TABLE = "CREATE TABLE " + TABLE_TRAIL_IMAGES + "("
				+ KEY_TRAIL_IMAGES_ID + " INTEGER PRIMARY KEY, " + KEY_TRAIL_IMAGES_IMAGEID + " INTEGER, "
				+ KEY_TRAIL_IMAGES_TRAILID + " INTEGER)";
		String CREATE_FEATURE_IMAGES_TABLE = "CREATE TABLE " + TABLE_FEATURE_IMAGES + "("
				+ KEY_FEATURE_IMAGES_ID + " INTEGER PRIMARY KEY, " + KEY_FEATURE_IMAGES_IMAGEID + " INTEGER, "
				+ KEY_FEATURE_IMAGES_FEATUREID + " INTEGER)";
		db.execSQL(CREATE_IMAGES_TABLE);
		db.execSQL(CREATE_LOCATION_IMAGES_TABLE);
		db.execSQL(CREATE_TRAIL_IMAGES_TABLE);
		db.execSQL(CREATE_FEATURE_IMAGES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		   switch(oldVersion) {
		   case 5:
			   db.execSQL("ALTER TABLE " + TABLE_IMAGES + " ADD COLUMN " + KEY_IMAGE_COVER + " INTEGER");
		       // we want both updates, so no break statement here...
		   case 6:
		   }
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public String getNextFileName(String path) {
		String result = "image00";
		String pathLength = Integer.toString(path.length()+13);
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT SUBSTR(" + KEY_IMAGE_URI + ","+ pathLength + ") + 1 AS image_num"
				+ " FROM " + TABLE_IMAGES 
				+ " WHERE " + KEY_IMAGE_URI + " LIKE '%" + path + "%'"
				+ " ORDER BY image_num DESC"
				+ " LIMIT 1";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = "image" + Integer.toString(cursor.getInt(0));
		} 
		db.close();
		return result;
	}
	
	private int addImage(Image image) {
		if (image.getId() != -1) {
			return image.getId();
		}
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_URI, image.getUri().toString());
        values.put(KEY_IMAGE_HASH, image.getImageHash());
        values.put(KEY_IMAGE_LAT, (image.getLatitude()*1e6));
        values.put(KEY_IMAGE_LON, (image.getLongitude()*1e6));
        values.put(KEY_IMAGE_DESCRIPTION, image.getDescription());
        values.put(KEY_IMAGE_COVER, (image.isCover()?1:0));
        int id = (int) db.insert(TABLE_IMAGES, null, values);
        db.close();
        return id;
	}

	public int addImageFor(Object o, Image image) {
		int result = -1;
		int imageId = this.addImage(image);
		image.setId(imageId);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		if (o instanceof Location) {
			values.put(KEY_LOCATION_IMAGES_LOCATIONID, ((Location) o).getId());
			values.put(KEY_LOCATION_IMAGES_IMAGEID, imageId);
			result = (int) db.insert(TABLE_LOCATION_IMAGES, null, values);
		} else if (o instanceof Trail) {
			values.put(KEY_TRAIL_IMAGES_TRAILID, ((Trail) o).getId());
			values.put(KEY_TRAIL_IMAGES_IMAGEID, imageId);
			result = (int) db.insert(TABLE_TRAIL_IMAGES, null, values);			
		} else if (o instanceof Feature) {
			values.put(KEY_FEATURE_IMAGES_FEATUREID, ((Feature) o).getId());
			values.put(KEY_FEATURE_IMAGES_IMAGEID, imageId);
			result = (int) db.insert(TABLE_FEATURE_IMAGES, null, values);
		}
		db.close();
		return result;
	}
	
	public Image getImage(int id) {
		Image image = null;
		String query = "SELECT " + KEY_IMAGE_ID + ", " + KEY_IMAGE_HASH + ", "
				+ KEY_IMAGE_URI + ", " + KEY_IMAGE_LAT + ", "
				+ KEY_IMAGE_LON + ", " + KEY_IMAGE_DESCRIPTION + ", " + KEY_IMAGE_COVER
				+ " FROM " + TABLE_IMAGES
				+ " WHERE " + KEY_IMAGE_ID + " = " + id + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			image = new Image(mContext, Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
					Uri.parse(cursor.getString(2)),
					(double) Integer.parseInt(cursor.getString(3))/1e6, (double) Integer.parseInt(cursor.getString(4))/1e6,
					cursor.getString(5), (cursor.getInt(6)==1));
		}
		db.close();
		return image;

	}
	
	public List<Image> getImagesFor(Object o) {
		List<Image> images = new ArrayList<Image>();
		String joinClause = "";
		String whereClause = "";
		if (o instanceof Location) {
			joinClause = "JOIN " + TABLE_LOCATION_IMAGES + " AS li ON i." + KEY_IMAGE_ID + " = li." + KEY_LOCATION_IMAGES_IMAGEID;
			whereClause = "WHERE li." + KEY_LOCATION_IMAGES_LOCATIONID + " = " + ((Location) o).getId();			
		} else if (o instanceof Trail) {
			joinClause = "JOIN " + TABLE_TRAIL_IMAGES + " AS ti ON i." + KEY_IMAGE_ID + " = ti." + KEY_TRAIL_IMAGES_IMAGEID;
			whereClause = "WHERE ti." + KEY_TRAIL_IMAGES_TRAILID + " = " + ((Trail) o).getId();			
		} else if (o instanceof Feature) {
			joinClause = "JOIN " + TABLE_FEATURE_IMAGES + " AS fi ON i." + KEY_IMAGE_ID + " = fi." + KEY_FEATURE_IMAGES_IMAGEID;
			whereClause = "WHERE fi." + KEY_FEATURE_IMAGES_FEATUREID + " = " + ((Feature) o).getId();			
		}
		String query = "SELECT i." + KEY_IMAGE_ID + ", " + KEY_IMAGE_HASH + ", "
				+ KEY_IMAGE_URI + ", " + KEY_IMAGE_LAT + ", "
				+ KEY_IMAGE_LON + ", " + KEY_IMAGE_DESCRIPTION + ", " + KEY_IMAGE_COVER
				+ " FROM " + TABLE_IMAGES + " AS i " + joinClause + " " + whereClause;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				Image image = new Image(mContext, Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
						Uri.parse(cursor.getString(2)),
						(double) Integer.parseInt(cursor.getString(3))/1e6, (double) Integer.parseInt(cursor.getString(4))/1e6,
						cursor.getString(5), (cursor.getInt(6)==1));
				images.add(image);
			} while (cursor.moveToNext());
		}
		db.close();
		return images;
	}
	
	public int updateImage(Image image) {
		int id = image.getId();
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_URI, image.getUri().toString());
        values.put(KEY_IMAGE_HASH, image.getImageHash());
        values.put(KEY_IMAGE_LAT, (image.getLatitude()*1e6));
        values.put(KEY_IMAGE_LON, (image.getLongitude()*1e6));
        values.put(KEY_IMAGE_DESCRIPTION, image.getDescription());
        values.put(KEY_IMAGE_COVER, (image.isCover()?1:0));
        int result = db.update(TABLE_IMAGES, values, KEY_IMAGE_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
        return result;	
	}
	
	public Image getCoverImageFor(Object o) {
		Image result = null;		
		List<Image> imageList = getImagesFor(o);
		Iterator<Image> i = imageList.iterator();
		if (i.hasNext()) {
			result = i.next();
			while (i.hasNext()) {
				if (i.next().isCover()) {
					result = i.next();
					break;
				}
			}
		}
		return result;
	}
	
	public void removeImage(Image image) {
		int id = image.getId();
		String idStr = String.valueOf(id);
		if (id >= 0 ) {
			//remove this image from all tables
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_LOCATION_IMAGES, KEY_LOCATION_IMAGES_IMAGEID + " = ?", new String[] {idStr});
			db.delete(TABLE_TRAIL_IMAGES, KEY_TRAIL_IMAGES_IMAGEID + " = ?", new String[] {idStr});
			db.delete(TABLE_FEATURE_IMAGES, KEY_FEATURE_IMAGES_IMAGEID + " = ?", new String[] {idStr});
			db.delete(TABLE_IMAGES, KEY_IMAGE_ID + " = ?", new String[] {idStr});
			db.close();
			//then remove the actual image file
			image.deleteFile();
		}
	}
	
	public Gallery getImagesView(final Activity activity, Object o) {
		Gallery view = new Gallery(mContext);
		if (o instanceof Location) {
			imageList = ((Location) o).getImages();
		} else if (o instanceof Trail) {
			imageList = ((Trail) o).getImages();
		} else if (o instanceof Feature) {
			imageList = ((Feature) o).getImages();
		} else imageList = new ArrayList<Image>();
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		view.setLayoutParams(layout);
		view.setSpacing(10);
        ImageAdapter imageAdapter = new ImageAdapter(activity,imageList);
        view.setAdapter(imageAdapter);
        view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == (parent.getChildCount()-1)) {
	            	Intent intent = new Intent(activity, GetPhoto.class);
					activity.startActivityForResult(intent, UnicyclistActivity.GET_PHOTO);
				} else if (position < (parent.getChildCount()-1)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(imageList.get(position).getUri(), "image/*");
					activity.startActivity(intent);
				}
			}
        });
        activity.registerForContextMenu(view);
		return view;
		
	}

}
