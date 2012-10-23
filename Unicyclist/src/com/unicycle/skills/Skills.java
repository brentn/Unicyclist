package com.unicycle.skills;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unicycle.tags.Tags;

public class Skills extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "skills";
	
	//table names
	private static final String TABLE_SKILLS = "skills";
	
	//column names
	private static final String KEY_SKILL_ID = "id";
	private static final String KEY_SKILL_NAME = "name";
	private static final String KEY_SKILL_DESCRIPTION = "description";
	private static final String KEY_SKILL_DIFFICULTY = "difficulty";

	private Context mContext;

    public Skills(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_SKILLS + " (" + KEY_SKILL_ID + " INTEGER PRIMARY KEY, "
				+ KEY_SKILL_NAME + " TEXT, " + KEY_SKILL_DESCRIPTION + " TEXT, " + KEY_SKILL_DIFFICULTY + " TEXT)";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SKILLS);
		onCreate(db);
	}
	
	public String databaseName() {
		return DATABASE_NAME;
	}
	
	public int find(Skill skill) {
		int result = -1;
		String query = "SELECT " + KEY_SKILL_ID + " FROM " + TABLE_SKILLS 
				+ " WHERE " + KEY_SKILL_NAME + " = '" + skill.getName()
				+ "' AND " + KEY_SKILL_DESCRIPTION + " = '" + skill.getDescription() 
				+ "' LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			result = cursor.getInt(0);
		}
		db.close();
		return result;
	}
	
	public int addSkill(Skill skill) {
		int result = find(skill);
		if (result == -1) {
	    	SQLiteDatabase db = this.getWritableDatabase(); 	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_SKILL_NAME, skill.getName());
	        values.put(KEY_SKILL_DESCRIPTION, skill.getDescription());
	        values.put(KEY_SKILL_DIFFICULTY, skill.getDifficulty());
	        result = (int) db.insert(TABLE_SKILLS, null, values);
	        db.close();
		}
		return result;
	}
	
	public Skill getSkill(int skillId) {
		Skill result = null;
		String query = "SELECT " + KEY_SKILL_ID + ", " + KEY_SKILL_NAME + ", " + KEY_SKILL_DESCRIPTION + ", " + KEY_SKILL_DIFFICULTY
				+ " FROM " + TABLE_SKILLS
				+ " WHERE " + KEY_SKILL_ID + " = " + Integer.toString(skillId)
				+ " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		if (cursor.moveToFirst()) {
			result = new Skill(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
			Tags tags = new Tags(mContext);
			result.setTags(tags.getTagsFor(result));		
		}
		db.close();
		return result;
	}
	
	public List<Skill> getAllSkills() {
		List<Skill> result = new ArrayList<Skill>();
		String query = "SELECT " + KEY_SKILL_ID + " FROM " + TABLE_SKILLS + " ORDER BY " + KEY_SKILL_DIFFICULTY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				result.add(getSkill(cursor.getInt(0)));
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}
	
	public int updateSkill(Skill skill) {
		int result = find(skill);
		if (result == -1) {
	    	SQLiteDatabase db = this.getWritableDatabase(); 	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_SKILL_NAME, skill.getName());
	        values.put(KEY_SKILL_DESCRIPTION, skill.getDescription());
	        values.put(KEY_SKILL_DIFFICULTY, skill.getDifficulty());
	        result =  db.update(TABLE_SKILLS, values, KEY_SKILL_ID + " =? ", new String[] { String.valueOf(result)});
	        db.close();
		}
		return result;
	}
	
	public void deleteSkill(Skill skill) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SKILLS, KEY_SKILL_ID + " = ?", new String[] { String.valueOf(skill.getId())});
		db.close();

	}

}
