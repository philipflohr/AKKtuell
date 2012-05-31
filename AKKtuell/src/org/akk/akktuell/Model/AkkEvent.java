package org.akk.akktuell.Model;

import java.util.GregorianCalendar;

import android.net.Uri;

/**
 * The Class AkkEvent. 
 * 
 * @author Philip Flohr
 */
public class AkkEvent {
	
	/**
	 * The Enum AkkEventType.
	 */
	public enum AkkEventType {
		Schlonz("Schlonz"),
		Workshop("Workshop"),
		Tanzen("Tanzen"),
		Sonderveranstaltung("Sonderveranstaltung"),
		Veranstaltungshinweis("Veranstaltungshinweis"),
		Default("Schlonz");
		
		/** The desc. */
		private final String desc;
		
		/**
		 * Instantiates a new akk event type.
		 *
		 * @param desc the desc
		 */
		AkkEventType(String desc) {
			this.desc = desc;
		}

		/**
		 * Checks if is equal.
		 *
		 * @param o the o
		 * @return true, if is equal
		 */
		public boolean isEqual(Object o) {
			if (o instanceof AkkEventType) {
				return (this.equals(o));
			} else if (o instanceof String) {
				return (this.toString().equals((String) o));
			}
			return false;
		}

		/**
		 * Gets the akk event type.
		 *
		 * @param desc the desc
		 * @return the akk event type
		 */
		public static AkkEventType getAkkEventType(String desc) {
			for (AkkEventType t : AkkEventType.values())
				if (t.isEqual(desc))
					return t;
			return Default;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		public String toString() {
			return this.desc;
		}
		
	}

	/** The event name. */
	private final String eventName;
	
	/** The event description. */
	private String eventDescription;
	
	/** The event begin time. */
	private final GregorianCalendar eventBeginTime;
	
	/** The event picture uri. */
	private String eventPictureRelativePath = null;
	
	public String getEventPictureRelativePath() {
		return eventPictureRelativePath;
	}

	/** The event place. */
	private final String eventPlace;
	
	/** The type. */
	private AkkEventType type = null;
	
	/** The is in calenar. */
	private boolean isInCalenar = false;

	/**
	 * Instantiates a new akk event.
	 *
	 * @param eventName the event name
	 * @param eventBeginTime the event begin time
	 * @param eventPlace the event place
	 */
	public AkkEvent(String eventName, GregorianCalendar eventBeginTime, String eventPlace) {
		this.eventName = eventName;
		this.eventBeginTime = eventBeginTime;
		this.eventPlace = eventPlace;
	}
	
	/**
	 * Instantiates a new akk event.
	 *
	 * @param eventName the event name
	 * @param eventDescription the event description
	 * @param eventPlace the event place
	 * @param eventType the event type
	 * @param eventDate the event date
	 */
	public AkkEvent (String eventName, String eventDescription, String eventPlace, AkkEventType eventType, GregorianCalendar eventDate, String picturePath) {
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.eventPlace = eventPlace;
		this.setType(eventType);
		this.eventBeginTime = eventDate;
		this.eventPictureRelativePath = picturePath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherEvent) {
		if (!(otherEvent instanceof AkkEvent)) {
			return false;
		}
		if (this.eventName.equals(((AkkEvent) otherEvent).getEventName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the event name.
	 *
	 * @return the event name
	 */
	public String getEventName() {
		return this.eventName;
	}

	/**
	 * Was updated.
	 *
	 * @param otherEvent the other event
	 * @return true, if successful
	 */
	public boolean wasUpdated(AkkEvent otherEvent) {
		if (!this.eventName.equals(otherEvent.getEventName())) {
			throw new IllegalArgumentException("Try to check if Event was updated. Failure: " + this.eventName + " was compared with " + otherEvent.getEventName());
		}
		if ((!this.eventDescription.equals(otherEvent.getEventDescription())) || (!this.eventBeginTime.equals(otherEvent.getEventBeginTime()))
				||(this.eventPictureRelativePath.equals(otherEvent.getEventPicRelPath()))) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the event pic realtive path.
	 *
	 * @return the event pic path
	 */
	public String getEventPicRelPath() {
		return this.eventPictureRelativePath;
	}

	/**
	 * Gets the event description.
	 *
	 * @return the event description
	 */
	public String getEventDescription() {
		return this.eventDescription;
	}
	
	/**
	 * Gets the event begin time.
	 *
	 * @return the event begin time
	 */
	public GregorianCalendar getEventBeginTime() {
		return this.eventBeginTime;
	}
	
	/**
	 * Gets the place.
	 *
	 * @return the place
	 */
	public String getPlace() {
		return this.eventPlace;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param newDesc the new description
	 */
	public void setDescription(String newDesc) {
		this.eventDescription = newDesc;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public AkkEventType getEventType() {
		return this.type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(AkkEventType type) {
		this.type = type;
	}
	
	/**
	 * Checks if is in calendar.
	 *
	 * @return true, if is in calendar
	 */
	public boolean isInCalendar() {
		return this.isInCalenar;
	}
	
	/**
	 * Sets the calendar state.
	 *
	 * @param state the new calendar state
	 */
	public void setCalendarState(boolean state) {
		this.isInCalenar = state;
	}
	
	public void setPicRelPath(String path) {
		this.eventPictureRelativePath = "bilder/" + path;
	}
}
