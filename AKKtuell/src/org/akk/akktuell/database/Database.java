package org.akk.akktuell.database;

import java.util.GregorianCalendar;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;

import android.content.Context;
import android.net.Uri;

/**
 * This class provides access to the data storage.
 * It is designed according to the Singleton pattern to ensure non-competitive
 * access.
 * 
 * @author Florian Muenchbach
 *
 */
public class Database implements DBInterface {
	private final DBInterface dbImplementation;
	private static Database instance = null;

	/**
	 * Creates a new {@link Database}.
	 * This constructor is final due to the singleton pattern this class uses.
	 * @param ctx the {@link Context} to use.
	 */
	private Database(Context ctx) {
		dbImplementation = new SQLiteImpl(ctx);
	}

	/**
	 * Returns an instance of the {@link Database} adapter.
	 * @param ctx the {@link Context} to use. Usually the App calling.
	 * @return an instance of the {@link Database}.
	 * @see Database#open()
	 */
	public synchronized static Database getInstance(Context ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("Context must not be null.");
		}

		if (instance == null) {
			instance = new Database(ctx);
		}

		return instance;
	}

	@Override
	public void open() throws DBException {
		dbImplementation.open();
	}

	@Override
	public void close() {
		dbImplementation.close();
	}

	@Override
	public boolean insertAkkEvent(AkkEvent event) throws DBException {
		return dbImplementation.insertAkkEvent(event);
	}

	@Override
	public boolean insertAkkEvents(AkkEvent[] events) throws DBException {
		return dbImplementation.insertAkkEvents(events);
	}

	@Override
	public boolean insertEvent(String eventName, String eventDescription,
			AkkEventType eventType, GregorianCalendar eventBeginTime, Uri eventPictureUri)
			throws DBException {
		return dbImplementation.insertEvent(eventName, eventDescription, eventType,
				eventBeginTime, eventPictureUri);
	}

	@Override
	public boolean updateAkkEventsAndFlush(AkkEvent[] events) throws DBException {
		return dbImplementation.updateAkkEventsAndFlush(events);
	}
	
	@Override
	public int deleteAkkEvent(AkkEvent event) {
		return dbImplementation.deleteAkkEvent(event);
	}

	@Override
	public int deleteEvents(AkkEvent[] events) {
		return dbImplementation.deleteEvents(events);
	}

	@Override
	public int deleteAllEventsBefore(GregorianCalendar date) {
		return dbImplementation.deleteAllEventsBefore(date);
	}

	@Override
	public int deleteAllEventsInsertedBefore(long timestamp) {
		return dbImplementation.deleteAllEventsInsertedBefore(timestamp);
	}

	@Override
	public int deleteAllEvents() {
		return dbImplementation.deleteAllEvents();
	}

	@Override
	public AkkEvent[] getAllEvents(DBFields orderBy, int direction) {
		return dbImplementation.getAllEvents(orderBy, direction);
	}

	@Override
	public AkkEvent[] getAllEventsFromDate(GregorianCalendar date,
			DBFields orderBy, int direction) {
		return dbImplementation.getAllEventsFromDate(date, orderBy, direction);
	}

	@Override
	public AkkEvent[] getAllEventsInMonth(int month, int year,
			DBFields orderBy, int direction) {
		return dbImplementation.getAllEventsInMonth(month, year, orderBy, direction);
	}

	@Override
	public AkkEvent[] getEventsByName(String name, DBFields orderBy,
			int direction) {
		return dbImplementation.getEventsByName(name, orderBy, direction);
	}

	@Override
	public AkkEvent[] getEventsFiltered(String[] nameContains,
			String[] descriptionContains, int[] daysOfWeek, int[] monthes,
			int[] years, DBFields orderBy, int direction) {
		return dbImplementation.getEventsFiltered(nameContains, descriptionContains,
				daysOfWeek, monthes, years, orderBy, direction);
	}

	@Override
	public AkkEvent[] getEventsByDate(GregorianCalendar date, DBFields orderBy,
			int direction) {
		return dbImplementation.getEventsByDate(date, orderBy, direction);
	}

	@Override
	public AkkEvent[] getEventsByDateAndTime(GregorianCalendar date,
			DBFields orderBy, int direction) {
		return dbImplementation.getEventsByDateAndTime(date, orderBy, direction);
	}

	@Override
	public AkkEvent[] getEventsByFulltextsearch(String searchString,
			DBFields[] fields, DBFields orderBy, int direction) {
		return dbImplementation.getEventsByFulltextsearch(searchString,
				fields, orderBy, direction);
	}

}
