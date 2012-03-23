package fr.quoteBrowser.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class DatabaseHelper {

	private static final String DB_NAME = "QUOTES.db";
	private static String TAG = "quoteBrowser";
	private InternalDBHelper internalDBHelper;
	private SQLiteDatabase db;
	private static ReentrantLock LOCK = new ReentrantLock();

	public static DatabaseHelper connect(Context context) {
		LOCK.lock();
		return new DatabaseHelper(context);
	}

	private DatabaseHelper(Context context) {
		super();
		internalDBHelper = new InternalDBHelper(context, DB_NAME, null, 1);
		db = internalDBHelper.getWritableDatabase();
	}

	public void release() {
		try {
			db.close();
		} finally {
			LOCK.unlock();
		}
	}

	public List<Quote> getQuotes() {
		return getQuotes("%");
	}

	public List<Quote> getQuotes(String source) {
		List<Quote> quotes = new ArrayList<Quote>();
		Cursor c = db.query(InternalDBHelper.TABLE_QUOTES, new String[] {
				InternalDBHelper.COL_QUOTE_ID, InternalDBHelper.COL_SOURCE,
				InternalDBHelper.COL_SCORE, InternalDBHelper.COL_TEXT}, InternalDBHelper.COL_SOURCE
				+ " like '" + source + "'", null, null, null,
				InternalDBHelper.COL_QUOTE_ID + " DESC");
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Quote q = new Quote();
				q.setQuoteId(c.getInt(0));
				q.setQuoteSource(c.getString(1));
				q.setQuoteScore(c.getString(2));
				q.setQuoteText(c.getString(3));
				quotes.add(q);
				c.moveToNext();
			}
		}
		c.close();
		return quotes;
	}

	public void putQuotes(List<Quote> quotes) {
		for (Quote q : quotes) {
			putQuote(q);
		}
	}

	public void putQuote(Quote q) {
		ContentValues values = new ContentValues();
		values.put(InternalDBHelper.COL_QUOTE_ID, q.getQuoteId());
		values.put(InternalDBHelper.COL_SOURCE, q.getQuoteSource().toString());
		values.put(InternalDBHelper.COL_SCORE, q.getQuoteScore().toString());
		values.put(InternalDBHelper.COL_TEXT, q.getQuoteText().toString());
		try {
			db.insert(InternalDBHelper.TABLE_QUOTES, null, values);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	public boolean isDatabaseEmpty() {
		long count = db.compileStatement(
				"SELECT COUNT(*) FROM " + InternalDBHelper.TABLE_QUOTES)
				.simpleQueryForLong();
		return count == 0;
	}

	public void copyDatabase() throws IOException {
		internalDBHelper.copyDatabase();
	}

}

class InternalDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_QUOTES = "QUOTES";
	public static final String COL_ID = "ID";
	public static final String COL_QUOTE_ID = "QUOTE_ID";
	public static final String COL_SOURCE = "SOURCE";
	public static final String COL_SCORE = "SCORE";
	public static final String COL_TEXT = "TEXT";
	private static String TAG = "quoteBrowser";
	private Context context;
	private String name;

	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_QUOTES
			+ " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_QUOTE_ID + " TEXT, " + COL_SOURCE + " TEXT, " + COL_SCORE
			+ " TEXT, " + COL_TEXT + ");";

	public InternalDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
		this.name = name;
	}

	public void copyDatabase() throws IOException {
		Log.i(TAG, "copying database");
		File dbFile = context.getDatabasePath(name);
		InputStream is;
		is = context.getAssets().open(name);

		OutputStream os = new FileOutputStream(dbFile);

		byte[] buffer = new byte[1024];
		while (is.read(buffer) > 0) {
			os.write(buffer);
		}
		os.flush();
		os.close();
		is.close();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_QUOTES + ";");
		onCreate(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
	}

}
