package fr.quoteBrowser.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String TAG = "quoteBrowser";
	public static final String TABLE_QUOTES= "QUOTES";
	public static final String COL_ID = "ID";
	public static final String COL_TITLE = "TITLE";
	public static final String COL_SOURCE = "SOURCE";
	public static final String COL_SCORE = "SCORE";
	public static final String COL_TEXT = "TEXT";
	public static final String COL_TEXT_MD5 = "TEXT_MD5";

 
	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_QUOTES + " ("
	+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TITLE + " TEXT, "
	+ COL_SOURCE + " TEXT, "+ COL_SCORE + " TEXT, "+ COL_TEXT_MD5 + " TEXT, "
	+ COL_TEXT + ");";
 
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		//on cr�� la table � partir de la requ�te �crite dans la variable CREATE_BDD
		db.execSQL(CREATE_BDD);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//On peut fait ce qu'on veut ici moi j'ai d�cid� de supprimer la table et de la recr�er
		//comme �a lorsque je change la version les id repartent de 0
		db.execSQL("DROP TABLE " + TABLE_QUOTES + ";");
		onCreate(db);
	}
	
	
	
	public static List<Quote> getQuotes(SQLiteDatabase db){
		List<Quote> quotes=new ArrayList<Quote>();
		Cursor c=db.query(TABLE_QUOTES, new String[]{COL_TITLE,COL_SOURCE,COL_SCORE,COL_TEXT,COL_TEXT_MD5}, null, null, null, null, COL_ID);
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Quote q = new Quote();
				q.setQuoteTitle(c.getString(0));
				q.setQuoteSource(c.getString(1));
				q.setQuoteScore(c.getString(2));
				q.setQuoteText(c.getString(3));
				q.setQuoteTextMD5(c.getString(4));
				quotes.add(q);
				c.moveToNext();
			}
		}
		c.close();
		return quotes;
	}
	
	public static void putQuotes(SQLiteDatabase db,List<Quote> quotes){
		for (Quote q:quotes){
			putQuote(db, q);
		}
	}
	
	public static void putQuote(SQLiteDatabase db,Quote q){
			Log.d(TAG,"putting quote "+q.getQuoteTitle()+" in databese");
			ContentValues values = new ContentValues();
			values.put(COL_TITLE, q.getQuoteTitle().toString());
			values.put(COL_SOURCE, q.getQuoteSource().toString());
			values.put(COL_SCORE, q.getQuoteScore().toString());
			values.put(COL_TEXT, q.getQuoteText().toString());
			values.put(COL_TEXT_MD5,q.getQuoteTextMD5());
			db.insert(TABLE_QUOTES, null, values);
		
	}

}