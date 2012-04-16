package org.akk.akktuell.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.akk.akktuell.Model.AkkEvent;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * This an implementation of the {@link DBInterface}.
 * This class provides access to the database (SQLite in this case).
 * @author Florian Muenchbach
 *
 */
class SQLiteImpl implements DBInterface {
	/**Database's name.*/
	private static final String DATABASE_NAME 	= "akktuell";
	private static final String TABLE			= "events";
    private static final int DATABASE_VERSION 	= 2;


    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Creates a new DBAdapter using the given {@link Context}.
     * The database needs to be opened before it can be used and closed after its use.
     * You might want to do so in the {@link Activity#onCreate(Bundle)}
     * (respectively {@link Activity#onDestroy()}) method of your program.
     * @param ctx the Context to use (usually the owning Activity).
     */
    public SQLiteImpl(Context ctx) {
    	this.context = ctx;
    }
    
    @Override
    public void open() throws DBException {
        dbHelper = new DatabaseHelper(context);
        try {
        	db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
        	throw new DBException(e, "Could not get a writable database instance.");
        }
        Log.w("AKKtuell", "DB Created: " + db.getPath() + ", " + db.isOpen() + ", ");
    }

    /**
     * Resets the database.
     * <b>Warning:</b> This will drop all tables and delete all data.
     */
    public void resetDatabase() {
    	DatabaseHelper.resetTables(db);
    }

    /**
     * Closes the database access.
     */
	public void close() {
		if (db != null)
			db.close();
		if (dbHelper != null) 
			dbHelper.close();
    }
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////   Helper Methods   //////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////


    /**Returns an ISO8601 conform string representation of a given GregorianCalendar Object.
     * @param date the {@link GregorianCalendar} object to change to string.
     * @return  an ISO8601 conform string representation of a given GregorianCalendar Object.
     */
	private static String dateToIso8601(GregorianCalendar date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		return sdf.format(date.getTime());
	}

	/**
	 * Returns a {@link GregorianCalendar} object created from the given ISO8601 string.
	 * @param iso8601 the string to convert
	 * @return a {@link GregorianCalendar} object or null in case the given string
	 * could not be parsed.
	 */
	private static GregorianCalendar iso8601ToGregorianCalendar(String iso8601) {
		GregorianCalendar returnValue = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		returnValue = (new GregorianCalendar());
		try {
			returnValue.setTime(sdf.parse(iso8601));
			return returnValue;
		} catch (ParseException e) {
			return returnValue = null;
		}
	}

	/**
	 * Converts a given direction ({@link DBInterface#ASCENDING},
	 * {@link DBInterface#DESCENDING}) to a string.
	 * @param direction the direction to convert.
	 * @return the String representation.
	 */
	private String orderByDirectionToStr(int direction) {
		switch (direction) {
			case(0): return "ASC";
			case(1):
			default:
				return "DESC";
		}
	}

	/**
	 * Converts a cursor content to an {@link AkkEvent} array.
	 * @param cursor the cursor to convert.
	 * @return the {@link AkkEvent} array.
	 */
	private AkkEvent[] cursorToAkkEventArray(Cursor cursor) {
		if (cursor == null)
			return null;

		AkkEvent[] events = new AkkEvent[cursor.getCount()];
		
		for (int i = 0; cursor.moveToNext(); i++) {
			events[i] = new AkkEvent(
							cursor.getString(cursor.getColumnIndex("name")),
							cursor.getString(cursor.getColumnIndex("description")),
							//TODO cursor.getString(cursor.getColumnIndex("type")),
							iso8601ToGregorianCalendar(
									cursor.getString(cursor.getColumnIndex("date"))),
							Uri.parse(cursor.getString(cursor.getColumnIndex("uri")))
						);
		}
		cursor.close();
		return events;
	}


	/**
	 * Executes the given raw sql query and returns the resulting {@link Cursor}.
	 * @param query the query to execute.
	 * @param searchCriteria the search criteria.
	 * @param direction the direction elements are ordered by.
	 * @param orderBy the field to order by.
	 * @return the resulting {@link Cursor}, might be {@code null}.
	 * @see SQLiteDatabase#rawQuery(String, String[])
	 */
	private Cursor executeRawQuery(String query, String[] searchCriteria,
			DBFields orderBy, int direction) {
		String q = query 
				+ ((orderBy != null)
				? " ORDER BY " + orderBy 
					+ " " + orderByDirectionToStr(direction)
				: "");
		String debug = "";
		if (searchCriteria != null) {
			for (String s : searchCriteria) {
				debug += s + ", ";
			}
		}
		Log.w("AKKtuell - debug", q + "; " + debug);
		return db.rawQuery(q, searchCriteria);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////// Interface Methods /////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	

	
	@Override
	public boolean insertAkkEvent(AkkEvent event) throws DBException {
		if (event == null) {
			throw new IllegalArgumentException("The given event must not be null.");
		}
		return this.insertEvent(event.getEventName(), event.getEventDescription(),
				event.getEventBeginTime(), event.getEventPicUri());
	}
	
	@Override
	public boolean insertAkkEvents(AkkEvent[] events) throws DBException {
		if (events == null) {
			throw new IllegalArgumentException("events must not be null.");
		}
		boolean returnValue = true;
		for (int i = 0; i < events.length && returnValue; i++ ) {
			returnValue = this.insertAkkEvent(events[i]);
		}
		return returnValue;
	}

	@Override
	public boolean insertEvent(String eventName, String eventDescription,
			GregorianCalendar eventBeginTime, Uri eventPictureUri) throws DBException {
		if (eventName == null
				|| eventDescription == null
				//TODO || eventType == null
				|| eventBeginTime == null
				) {
			throw new IllegalArgumentException("The given event " +
					"properties must not be null.");
		}

		long rowid = -1;
		
    	//Set new line if not existent
    	ContentValues values 	= new ContentValues();
    	values.put("name", eventName);
    	values.put("description", eventDescription);
    	//TODO values.put("type", eventName);
    	values.put("date", dateToIso8601(eventBeginTime));
    	values.put("uri", (eventPictureUri != null) ? eventPictureUri.toString() : "");

    	try {
    		rowid = db.insertOrThrow(TABLE, null, values);
    	} catch (SQLException e) {
    		throw new DBException(e, "Could not insert the new event into the database.");
    	}

		return (rowid >= 0);
	}

	@Override
	public int deleteAkkEvent(AkkEvent event) {
		if (event == null) {
			return 0;
		}

		return db.delete(TABLE,
				//TODO maybe only name and description?
				"name=? and description=? and date=?",
				new String[]{event.getEventName(), event.getEventDescription(),
				dateToIso8601(event.getEventBeginTime())});
	}

	@Override
	public int deleteEvents(AkkEvent[] events) {
		int affectedRows = 0;
		for (AkkEvent event : events)
			affectedRows += this.deleteAkkEvent(event);
		return affectedRows;
	}

	@Override
	public int deleteAllEventsBefore(GregorianCalendar date) {
		if (date == null)
			throw new IllegalArgumentException("The date may not be null.");
		return db.delete(TABLE,
				"date < ?",
				new String[]{dateToIso8601(date)}
				);
	}

	@Override
	public int deleteAllEvents() {
		return db.delete(TABLE, "", null);
	}

	@Override
	public AkkEvent[] getAllEvents(DBFields orderBy, int direction) {
		return cursorToAkkEventArray(
				executeRawQuery(
					"select * from " + TABLE ,
					null,
					orderBy,
					direction
				)
			);
	}

	@Override
	public AkkEvent[] getAllEventsFromDate(GregorianCalendar date,
			DBFields orderBy, int direction) {
		return (date != null)
			? cursorToAkkEventArray(
				executeRawQuery(
					"select * from " + TABLE + " where date >= ?",
					new String[]{ dateToIso8601(date) },
					orderBy,
					direction
				))
			: null;
	}

	@Override
	public AkkEvent[] getAllEventsInMonth(int month, int year, DBFields orderBy,
			int direction) {
		if ((month >= 1) && (month <= 12)) {
			int y = year;
			if ((year < 0) || (year > 9999)) {
				y = Calendar.getInstance().get(Calendar.YEAR);
			}

			//where date >= 'year-month'; leading 0s will be added to month
			String from_date = y + "-" + String.format("%02d", month);

			//'year-(next month)'; leading 0s will be added to month,
			//month and year will be adjusted to take care of year change
			String to_date	 = (month == 12) ? (++y + "-01")
					:  y + "-" + String.format("%02d", ++month);


			return cursorToAkkEventArray(
					executeRawQuery(
						"select * from " + TABLE + " where date >= ? and date < ?",
						new String[]{from_date, to_date},
						orderBy,
						direction
						)
					);
		}
		return null;
	}

	@Override
	public AkkEvent[] getEventsByName(String name, DBFields orderBy,
			int direction) {
		return cursorToAkkEventArray(
				executeRawQuery(
					"select * from " + TABLE + " where name=?",
					new String[]{name},
					orderBy,
					direction
				)
			);
	}

	@Override
	public AkkEvent[] getEventsFiltered(String[] nameContains,
			String[] descriptionContains, int[] daysOfWeek, int[] monthes,
			int[] years, DBFields orderBy, int direction) {
		//TODO not yet implemented
		return null;
	}

	@Override
	public AkkEvent[] getEventsByDate(GregorianCalendar date, DBFields orderBy,
			int direction) {
		String d = dateToIso8601(date).substring(0, 10);
		String from_dusk = d + " 00:00:00";
		String till_dawn = d + " 23:59:59";

		return cursorToAkkEventArray(
				executeRawQuery(
					"select * from " + TABLE + " where date >= ? and date <= ?",
					new String[]{from_dusk, till_dawn},
					orderBy,
					direction
				)
			);
	}
	
	@Override
	public AkkEvent[] getEventsByDateAndTime(GregorianCalendar date, DBFields orderBy,
			int direction) {
		return cursorToAkkEventArray(
				executeRawQuery(
					"select * from " + TABLE + " where date=?",
					new String[]{dateToIso8601(date)},
					orderBy,
					direction
				)
			);
	}

	@Override
	public AkkEvent[] getEventsByFulltextsearch(String searchString, DBFields[] fields,
			DBFields orderBy, int direction) {
		if (searchString.equals("") || fields == null) {
			return null;
		}

		String q = "";
		for (DBFields f : fields) {
			q += ((q.equals("")) ? "": " union ")
					+ "select * from " + TABLE + " where " + f + " match ?";
		}

		//add a Search string for each field
		String[] searchStrings;
		if (fields.length > 1) {
			searchStrings = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				searchStrings[i] = searchString;
			}
		} else
			searchStrings = new String[]{searchString};

		return cursorToAkkEventArray(
				executeRawQuery(q, searchStrings, orderBy, direction)
			);
	}
	
	
	/**
	 * This class is a convenience class used for simple database creation and handling.
	 * 
	 * @author Florian Muenchbach
	 *
	 */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*
         * Database Layout:
         * events:
         * 		_______________________________________________
         * 		| id | name | description | type | date | uri |
         * 		-----------------------------------------------
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
        	try {
        		Log.w("AKKtuell", "Starting to create DB");
        		db.execSQL("create virtual table events using fts3("
        				+ "name " + SqlDataTypes.STRING + " not null, "
        				+ "description " + SqlDataTypes.STRING + " not null, "
        				+ "type " + SqlDataTypes.STRING + " not null, "
        				+ "date " + SqlDataTypes.DATE + " not null, "
        				+ "uri " + SqlDataTypes.STRING
        				+ "); "
        			);
        		Log.w("AKKtuell", "finished creating DB");
        	} catch (SQLException e) {
        		Log.w("AKKtuell", e + "\n" + e.getMessage());
        		throw new SQLException(e + "\n" + e.getMessage());
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DATABASE_NAME, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            resetTables(db);
            onCreate(db);
            //TODO notify() the Downloader?
        }

        /**
         * Drops all tables.
         * @param db the database containing the tables to drop.
         */
        public static void resetTables(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS events");
        }
    }
}
