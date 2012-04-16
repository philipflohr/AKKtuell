package org.akk.akktuell.test;


import java.util.GregorianCalendar;

import org.akk.akktuell.Activity.AKKtuellMainActivity;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.database.DBException;
import org.akk.akktuell.database.Database;
import org.akk.akktuell.database.DBFields;
import org.akk.akktuell.database.DBInterface;

import android.app.Activity;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;


public class DBAdapterTest extends
ActivityInstrumentationTestCase2<AKKtuellMainActivity> {
	private Database db;

	//Months are 0-BASED!!
	private AkkEvent event1 = new AkkEvent("event1",
			"erstes event im januar",
			new GregorianCalendar(2012, 0, 1, 20, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event2 = new AkkEvent("event2",
			"zwotes event im februar",
			new GregorianCalendar(2012, 1, 2, 29, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event3 = new AkkEvent("event3",
			"drittes event im märz",
			new GregorianCalendar(2012, 2, 1, 20, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event4 = new AkkEvent("event4",
			"viertes event im april",
			new GregorianCalendar(2012, 3, 24, 20, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event5 = new AkkEvent("event5",
			"fünftes event im april",
			new GregorianCalendar(2012, 3, 30, 20, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event6 = new AkkEvent("event6",
			"sehr altes event",
			new GregorianCalendar(1900, 3, 30, 20, 0),
			Uri.parse("http://www.akk.org"));
	private AkkEvent event7 = new AkkEvent("januar event",
			"siebtes event im april",
			new GregorianCalendar(2012, 3, 30, 19, 0),
			Uri.parse("http://www.akk.org"));
	private Activity activity;

	public DBAdapterTest() {
		super("org.akk.akktuell.Activity", AKKtuellMainActivity.class);
	}


	public void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		db = Database.getInstance(activity);
	}	

	public void tearDown() {
		//		db.close();
		activity.finish();
	}



	/**
	 * Compares two given {@link AkkEvent AkkEvents}, returns true if equal.
	 */
	public boolean compareAkkEvents(AkkEvent e1, AkkEvent e2) {
		//		boolean e = false;
		//		if (e1.getEventName().equals(e2.getEventName())) {
		//			if (e1.getEventDescription().equals(e2.getEventDescription())) {
		//				if (e1.getEventBeginTime().equals(e2.getEventBeginTime())) {
		//					if (e1.getEventPicUri().equals(e2.getEventPicUri())) {
		//						e = true;
		//					} else Log.w("AkktuellTest", "uri");
		//				} else Log.w("AkktuellTest", "time");
		//			} else Log.w("AkktuellTest", "description");
		//		} else 
		//			Log.w("AkktuellTest", "name: " + e1.getEventName() + ", " + e2.getEventName());
		//		return e;
		//		Log.w("AKKtuellTest - debug", "Event: " + e.getEventName()
		//				+ " - " + e.getEventDescription()
		//				+ " - " + e.getEventBeginTime()
		//				+ " - " + e.getEventPicUri()
		//				);
		return (e1.getEventName().equals(e2.getEventName()))
				&& (e1.getEventDescription().equals(e2.getEventDescription()))
				&& (e1.getEventBeginTime().equals(e2.getEventBeginTime()))
				&& (e1.getEventPicUri().equals(e2.getEventPicUri()));
	}

	/**
	 * Tries to open the db.
	 * @throws Exception 
	 */
	public void testAOpen() throws Exception {
		try {
			db.open();
		} catch (DBException e) {
			fail(e.getFullMessage());
			e.printStackTrace();
		}
		//delete everything
		db.deleteAllEvents();
		results = db.getAllEvents(null, 0);
		if (results != null && results.length > 0) {
			throw new Exception();
		}
		results = null;

		//close db
		db.close();
	}

	/**
	 * Tries to insert {@link AkkEvent AkkEvents}.
	 */
	public void testBInsertAkkEvent() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		//try to insert null
		try {
			db.insertAkkEvent(null);
			fail("Should thorw exceptions");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		} catch (DBException e) {
			fail();
			e.printStackTrace();
		}

		try {
			db.insertAkkEvents(null);
			fail("Should thorw exceptions");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		} catch (DBException e) {
			fail(e.getFullMessage());
			e.printStackTrace();
		}

		//try to insert a valid element
		try {
			db.insertAkkEvent(event1);
		} catch (DBException e) {
			fail("Could not insert the event. - " + e.getFullMessage());
			e.printStackTrace();
		}

		//insert many elements
		AkkEvent[] array = new AkkEvent[]{event2, event3, event4};
		try {
			db.insertAkkEvents(array);
		} catch (DBException e) {
			fail("Could not insert many events at once. " + e.getFullMessage());
			e.printStackTrace();
		}

		//insert element properties one by one.
		try {
			try {
				db.insertEvent(null,
						event5.getEventDescription(),
						event5.getEventBeginTime(),
						event5.getEventPicUri());
				fail("Should thorw exceptions");
			} catch (IllegalArgumentException e) {
				assertTrue(true);
			}
			try {
				db.insertEvent(event5.getEventName(),
						null,
						event5.getEventBeginTime(),
						event5.getEventPicUri());
				fail("Should thorw exceptions");
			} catch (IllegalArgumentException e) {
				assertTrue(true);
			}
			try {
				db.insertEvent(event5.getEventName(),
						event5.getEventDescription(),
						null,
						event5.getEventPicUri());
				fail("Should thorw exceptions");
			} catch (IllegalArgumentException e) {
				assertTrue(true);
			}


			db.insertEvent(event5.getEventName(),
					event5.getEventDescription(),
					event5.getEventBeginTime(),
					event5.getEventPicUri());
		} catch (DBException e) {
			fail("Could not insert the event using its components. - " + e.getFullMessage());
			e.printStackTrace();
		}
		//close db
		db.close();
	}

	AkkEvent[] results = null;
	/**
	 * Tries to get all events.
	 */
	public void testCGetAllEvents() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		//no sorting
		results = db.getAllEvents(null, DBInterface.DESCENDING);
		if (results != null && results.length == 5) {
			for (int i = 0; i <results.length; i++) {
				Log.w("AKKtuellTest - debug", "Event " + i + ": "
						+ results[i].getEventName());
			}
			assertTrue(compareAkkEvents(results[0], event1));
			assertTrue(compareAkkEvents(results[1], event2));
			assertTrue(compareAkkEvents(results[2], event3));
			assertTrue(compareAkkEvents(results[3], event4));
			assertTrue(compareAkkEvents(results[4], event5));
		} else {
			fail("got not a single result");
		}
		results = null;
		//sort by date, descending
		results = db.getAllEvents(DBFields.EVENT_DATE, DBInterface.DESCENDING);
		if (results != null && results.length == 5) {
			for (AkkEvent e : results) {
				Log.w("AKKtuellTest - debug", "Event: " + e.getEventName()
						+ " - " + e.getEventDescription()
						+ " - " + e.getEventBeginTime()
						+ " - " + e.getEventPicUri()
						);
			}
			assertTrue(compareAkkEvents(results[0], event5));
			assertTrue(compareAkkEvents(results[1], event4));
			assertTrue(compareAkkEvents(results[2], event3));
			assertTrue(compareAkkEvents(results[3], event2));
			assertTrue(compareAkkEvents(results[4], event1));
		} else {
			fail("got not a single result");
		}
		results = null;
		//sort by description, ascending
		results = db.getAllEvents(DBFields.EVENT_DESC, DBInterface.ASCENDING);
		if (results != null && results.length == 5) {
			for (AkkEvent e : results) {
				Log.w("AKKtuellTest - debug", "Event: " + e.getEventName());
			}
			assertTrue(compareAkkEvents(results[0], event3));
			assertTrue(compareAkkEvents(results[1], event1));
			assertTrue(compareAkkEvents(results[2], event5));
			assertTrue(compareAkkEvents(results[3], event4));
			assertTrue(compareAkkEvents(results[4], event2));
		} else {
			fail("got not a single result");
		}
		results = null;	
		//close db
		db.close();	
	}

	/**
	 * Tries to get all events starting from 2012-03-01 00:00:00 and for null.
	 */
	public void testDGetAllEventsFromDate() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		GregorianCalendar date = new GregorianCalendar(2012, 2, 1);
		results = db.getAllEventsFromDate(date, null, 0);
		assertTrue(results != null && results.length == 3);
		results = null;	

		assertTrue(db.getAllEventsFromDate(null, null, 0) == null);
		//close db
		db.close();
	}

	/**
	 * Tries to get all events in April, 2012 and for invalid parameters.
	 */
	public void testEGetAllEventsInMonth() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		try {
			db.insertAkkEvent(event6);
			results = db.getAllEventsInMonth(4, 2012, null, 0);
			assertTrue(results != null && results.length == 2);
			results = null;	
		} catch (DBException e) {
			fail(e.getFullMessage());
			e.printStackTrace();
		}

		results = db.getAllEventsInMonth(6, 2012, null, 0);
		assertTrue(results == null || results.length == 0);
		results = null;

		assertTrue(db.getAllEventsInMonth(35, 2012, null, 0) == null);
		//close db
		db.close();
	}

	/**
	 * Tries to get the events named as the third one and for an invalid one.
	 */
	public void testFGetEventsByNameTest() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		results = db.getEventsByName(event3.getEventName(), null, 0);
		assertTrue(results != null && results.length == 1);
		assertTrue(results != null && compareAkkEvents(results[0], event3));
		results = null;	

		results = db.getEventsByName(event3.getEventName().substring(1), null, 0);
		assertTrue(results != null);
		results = null;	
		//close db
		db.close();
	}

	/**
	 * Tries to get the events filtered.
	 */
	public void testGGetEventsFiltered() {
		//TODO
	}

	/**
	 * Tries to get the events at the date, either general or specific.
	 */
	public void testHGetEventsByDate() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		GregorianCalendar date = new GregorianCalendar(2012, 3, 30, 19, 0);

		try {
			db.insertAkkEvent(event7);
			results = db.getEventsByDate(date, null, 0);
			assertTrue(results != null && results.length == 2);
			results = null;	

			results = db.getEventsByDateAndTime(date, null, 0);
			assertTrue(results != null && results.length == 1);
			assertTrue(results != null && compareAkkEvents(results[0], event7));
			results = null;	
		} catch (DBException e) {
			fail(e.getFullMessage());
			e.printStackTrace();
		}
		//close db
		db.close();
	}

	/**
	 * Tries the full text search.
	 */
	public void testIGetEventsByFullTextSearch() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		//search in names only
		results = db.getEventsByFulltextsearch("januar",
				new DBFields[]{DBFields.EVENT_NAME}, null, 0);
		assertTrue(results != null && results.length == 1);
		assertTrue(results != null && compareAkkEvents(results[0], event7));
		results = null;	

		//search in descriptions only 
		results = db.getEventsByFulltextsearch("januar",
				new DBFields[]{DBFields.EVENT_DESC}, null, 0);
		assertTrue(results != null && results.length == 1);
		assertTrue(results != null && compareAkkEvents(results[0], event1));
		results = null;	

		//search in both and sort by date, ascending
		results = db.getEventsByFulltextsearch("januar",
				new DBFields[]{DBFields.EVENT_DESC, DBFields.EVENT_NAME},
				DBFields.EVENT_DATE, DBInterface.ASCENDING);
		if (results != null && results.length == 2) {
			assertTrue(results != null && compareAkkEvents(results[0], event1));
			assertTrue(results != null && compareAkkEvents(results[1], event7));
		} else {
			fail("result should not be null and length should be 2 (is "
					+ results.length + ").");
		}
		results = null;	
		//close db
		db.close();
	}

	/**
	 * Deletes all events before 2012-04-01
	 */
	public void testJDeleteAllEventsBefore() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		GregorianCalendar date = new GregorianCalendar(2012, 3, 1);
		assertTrue(db.deleteAllEventsBefore(date) == 4);

		try {
			db.deleteAllEventsBefore(null);
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		//close db
		db.close();
	}

	/**
	 * Tries to delete all events (one by one or all together).
	 */
	public void testKDeleteEvents() {
		try {
			db.open();
		} catch (DBException e1) {
			fail();
			e1.printStackTrace();
		}
		try {
			db.deleteAkkEvent(null);
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}

		assertTrue(db.deleteAkkEvent(event7) == 1);

		assertTrue(db.deleteAkkEvent(event1) == 0);

		db.deleteAllEvents();
		results = db.getAllEvents(null, 0);
		assertTrue((results == null) || (results.length == 0));
		results = null;	
		//close db
		db.close();
	}
}
