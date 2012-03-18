package fr.arolla.afterwork.whereisit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import fr.arolla.afterwork.whereisit.WereIsItPhoto;

public class WhereIsItDBAdapter {
	private static final String DATABASE_NAME = "whereIsIt.db";
	private static final String DATABASE_TABLE = "photos";
	private static final int DATABASE_VERSION = 1;

	public static final String KEY_ID = "_id";
	public static final String KEY_LABEL = "label";
	public static final String KEY_DESCRIPTION = "desciption";
	public static final String KEY_TIPS = "tips";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";

	private SQLiteDatabase db;
	private final Context context;
	private WhereIsItDBOpenHelper dbHelper;
	private static final String[] allColumnsProjections = new String[] {
			KEY_ID, KEY_LABEL, KEY_DESCRIPTION, KEY_TIPS, KEY_LAT, KEY_LNG };

	public WhereIsItDBAdapter(Context _context) {
		this.context = _context;
		dbHelper = new WhereIsItDBOpenHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}

	public long insertPhoto(WereIsItPhoto photo) {
		ContentValues newPhotoValues = new ContentValues();
		newPhotoValues.put(KEY_LABEL, photo.getLabel());
		newPhotoValues.put(KEY_DESCRIPTION, photo.getDescription());
		newPhotoValues.put(KEY_TIPS, photo.getTips());
		newPhotoValues.put(KEY_LAT, photo.getLat());
		newPhotoValues.put(KEY_LNG, photo.getLng());
		// Insère la ligne.
		return db.insert(DATABASE_TABLE, null, newPhotoValues);
	}

	public boolean removePhoto(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}

	public Cursor getAllPhotosCursor() {
		return db.query(DATABASE_TABLE, allColumnsProjections, null, null,
				null, null, null);
	}

	public Cursor setCursorPhoto(long _rowIndex) throws SQLException {
		Cursor result = db.query(true, DATABASE_TABLE, allColumnsProjections,
				KEY_ID + "=" + _rowIndex, null, null, null, null, null);
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("Aucune photo pour la ligne : " + _rowIndex);
		}
		return result;
	}

	public WereIsItPhoto getPhoto(long _rowIndex) throws SQLException {
		Cursor cursor = setCursorPhoto(_rowIndex);

		String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
		String description = cursor.getString(cursor
				.getColumnIndex(KEY_DESCRIPTION));
		String tips = cursor.getString(cursor.getColumnIndex(KEY_TIPS));
		double lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
		double lng = cursor.getDouble(cursor.getColumnIndex(KEY_LNG));

		WereIsItPhoto result = new WereIsItPhoto(label, description, tips, lat,
				lng);
		return result;
	}

	private static class WhereIsItDBOpenHelper extends SQLiteOpenHelper {

		public WhereIsItDBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Crée une nouvelle base.
		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, " + KEY_LABEL
				+ " text not null, " + KEY_DESCRIPTION + " text, " + KEY_TIPS
				+ " text, " + KEY_LAT + " double, " + KEY_LNG + " double);";

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			Log.w("WhereIsItDBAdapter", "Mise à jour de la version "
					+ _oldVersion + " vers la version " + _newVersion
					+ ", les anciennes données seront détruites");

			// Détruit l’ancienne table.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Crée la nouvelle.
			onCreate(_db);
		}
	}
}
