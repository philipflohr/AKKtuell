package org.akk.akktuell.database;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;

import java.util.GregorianCalendar;

import android.net.Uri;

/**
 * This interface provides access to the database functions.
 * @author Florian Muenchbach
 */
public interface DBInterface {
	public static final int ASCENDING = 0;
	public static final int DESCENDING = 1;

    /**
     * Opens the database. If it cannot be opened, this method tries to create a new
     * instance of the database. If it cannot be created, it will throw an exception.
     * The database needs to be opened before it can be used.
     * @throws DBException in case no writable database can be opened.
     */
	public void open() throws DBException;

	/**
	 * Closes the database.
	 */
	public void close();

	/**
	 * Inserts a given {@link AkkEvent} into the database.
	 * Returns true if the event was added successfully.
	 * This method will automatically avoid the creation of duplicates.
	 * @param event the event to insert.
	 * @return true, if successful.
	 * @throws DBException in case the event could not be inserted.
	 */
	public boolean insertAkkEvent(AkkEvent event) throws DBException;

	/**
	 * Convenience method that inserts a given set of {@link AkkEvent AkkEvents}
	 * into the database.
	 * Inserting will be stopped in case an error occurred.
	 * Returns true if the event was added successfully.
	 * This method will automatically avoid the creation of duplicates.
	 * @param event the event to insert.
	 * @return true, if successful.
	 * @throws DBException in case the event could not be inserted.
	 */
	public boolean insertAkkEvents(AkkEvent[] events) throws DBException;

	/**
	 * This method takes a set of {@link AkkEvent AkkEvents} and inserts them into
	 * the database.
	 * Existing {@link AkkEvent AkkEvents} will be kept, updated events will be
	 * inserted and events without a match will be flushed.
	 * Inserting will be stopped in case an error occurred.
	 * Returns true if the event was added successfully.
	 * This method will automatically avoid the creation of duplicates.
	 * @param event the event to insert.
	 * @return true, if successful.
	 * @throws DBException in case the event could not be inserted.
	 */
	public boolean updateAkkEventsAndFlush(AkkEvent[] events) throws DBException;

	/**
	 * Inserts the given {@code eventName}, {@code eventDescription},
	 * {@code eventBeginTime} and {@code eventUri} into the database.
	 * Returns true if the event was added successfully.
	 * This method will automatically avoid the creation of duplicates.
	 * @param eventName the name of the event.
	 * @param eventDescription the description of the event.
	 * @param eventBeginTime the starting date of the event.
	 * @param eventPictureUri the uri to the event.
	 * @return true, if successful.
	 * @throws DBException in case the event could not be inserted.
	 */
	public boolean insertEvent(String eventName, String eventDescription,
			AkkEventType eventType, GregorianCalendar eventBeginTime,
			Uri eventPictureUri) throws DBException;

	/**
	 * Deletes the given {@link AkkEvent} from the database.
	 * Returns the number of database entries affected by this call.
	 * @param event the event to delete.
	 * @return number of affected database entries.
	 */
	public int deleteAkkEvent(AkkEvent event);

	/**
	 * Deletes the given set of events.
	 * Returns the number of database entries affected by this call.
	 * @param events the events to delete.
	 * @return number of affected database entries.
	 */
	public int deleteEvents(AkkEvent[] events);

	/**
	 * Deletes all events before the given date. Events starting at the given
	 * date will NOT be deleted.
	 * Returns the number of database entries affected by this call.
	 * @param date delete all events before date.
	 * @return number of affected database entries.
	 */
	public int deleteAllEventsBefore(GregorianCalendar date);

	/**
	 * Deletes all events that have been inserted before the given timestamp.
	 * Events inserted at the given date will NOT be deleted.
	 * Returns the number of database entries affected by this call.
	 * @param timestamp delete all events before timestamp.
	 * @return number of affected database entries.
	 */
	public int deleteAllEventsInsertedBefore(long timestamp);

	/**
	 * Deletes all events in the database and returns the number of deleted events.
	 * @return the number of deleted events.
	 */
	public int deleteAllEvents();

	/**
	 * Returns all {@link AkkEvent AkkEvents} in the database.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getAllEvents(DBFields orderBy, int direction);
	
	/**
	 * Returns all {@link AkkEvent AkkEvents} with a start date on or after the given
	 * date.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param date only events beginning at this date will be returned.
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getAllEventsFromDate(GregorianCalendar date,
			DBFields orderBy, int direction);


	/**
	 * Returns all {@link AkkEvent AkkEvents} starting on a day in the given month
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param month only events starting in this month will be returned.
	 * @param year the year to use, null or {@code year} > 9999 or @code year} < 0 will use the 
	 * current year.
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getAllEventsInMonth(int month, int year,
			DBFields orderBy, int direction);

	/**
	 * Returns all {@link AkkEvent AkkEvents} with the given name.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param name only events with this name will be returned.
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getEventsByName(String name, DBFields orderBy, int direction);

	/**
	 * Returns all {@link AkkEvent AkkEvents} matching the given sets of filters.
	 * A database entry has to match at least one entry of each filter set.
	 * {@code null} will turn off the filter type.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param nameContains filter for the names. Only entries containing at least
	 * 		one of the given strings
	 * 		will be used.
	 * @param descriptionContains filter for the descriptions. Only entries
	 * 		containing at least one of the
	 * 		given strings will be used.
	 * @param daysOfWeek filter for the weekdays of the beginning date. Only entries
	 * 		containing at least one of the
	 * 		given values will be used.
	 * @param monthes filter for the month of the beginning date. Only entries
	 * 		containing at least one of the
	 * 		given values will be used.
	 * @param years filter for the years of the beginning date. Only entries
	 * 		containing at least one of the
	 * 		given values will be used.
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getEventsFiltered(String[] nameContains,
			String[] descriptionContains,
			int[] daysOfWeek, int[] monthes, int[] years,
			DBFields orderBy, int direction);

	/**
	 * Returns all {@link AkkEvent AkkEvents} beginning at the given date.
	 * This method does not take care of the time component of the given
	 * {@link GregorianCalendar} object. This means, it will return all events between
	 * 0:01 am and 23:59 on the given day.
	 * If you need events on a specific date use
	 * {@link #getEventsByDateAndTime(GregorianCalendar, DBFields, int)}
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param the start date to search
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getEventsByDate(GregorianCalendar date,
			DBFields orderBy, int direction);

	/**
	 * Returns all {@link AkkEvent AkkEvents} beginning at the given date.
	 * This will also use the time component of the given {@link GregorianCalendar}
	 * object.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param the start date to search
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	AkkEvent[] getEventsByDateAndTime(GregorianCalendar date, DBFields orderBy,
			int direction);
	/**
	 * Searching the database for the given searchString using a full text search.
	 * Returns the results.
	 * @return a list of {@link AkkEvent AkkEvents}
	 * @param searchString the string to search for.
	 * @param fields the fields to be searched.
	 * @param orderBy the field to sort by.
	 * @param direction the direction of the ordering.
	 * 		({@link DBInterface#ASCENDING}, {@link DBInterface#DESCENDING})
	 * @see DBFields
	 */
	public AkkEvent[] getEventsByFulltextsearch(String searchString, DBFields[] fields,
			DBFields orderBy, int direction);

}
